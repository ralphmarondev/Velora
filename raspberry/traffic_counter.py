#!/usr/bin/env python3
"""Raspberry Pi road monitor using two YOLO models on the same camera feed.

The COCO model supplies vehicle classes.  The custom model supplies police and
roadwork classes.  Both are tracked independently because their class IDs are
different.  The output is a small JSON event that an app/API can consume.
"""
from __future__ import annotations

import argparse
import json
import logging
import os
import time
from collections import defaultdict
from dataclasses import dataclass
from typing import Iterable

import cv2
import numpy as np
import requests
from dotenv import load_dotenv
from picamera2 import Picamera2
from ultralytics import YOLO

# ==================== CONFIGURATION CONSTANTS ====================
# Traffic detection thresholds
MEDIUM_COUNT = 2   # Number of vehicles for "medium" traffic
HIGH_COUNT = 3     # Number of vehicles for "high" traffic (congested)

# Time thresholds (in seconds)
POLICE_SECONDS = 600        # 10 minutes - police must be present this long
ROADWORK_HOLD_SECONDS = 30  # Hold roadwork state for 30 seconds after last detection
EVENT_COOLDOWN_SECONDS = 300  # 5 minutes cooldown between events

# Model confidence threshold
CONFIDENCE_THRESHOLD = 0.40

# Camera resolution
CAMERA_WIDTH = 1280
CAMERA_HEIGHT = 720

# ==================== END CONFIGURATION ====================

# COCO labels in yolo26n.pt ("tricycle" is not a standard COCO class).
VEHICLE_NAMES = {"car", "motorcycle", "bus", "truck", "tricycle", "trycicle"}
POLICE_NAMES = {"police", "police officer", "police car", "police vehicle"}
ROADWORK_NAMES = {"cone", "cones", "traffic cone", "barricade", "barricades",
                  "temporary control sign", "temporary control signs", "road work sign"}


def normalise(name: str) -> str:
    return name.lower().replace("_", " ").replace("-", " ").strip()


def class_name(names, class_id: int) -> str:
    return normalise(names[int(class_id)])


def inside_roi(point: tuple[int, int], polygon: np.ndarray | None) -> bool:
    return polygon is None or cv2.pointPolygonTest(polygon, point, False) >= 0


@dataclass
class SeenObject:
    label: str
    track_id: int
    point: tuple[int, int]


class FirebaseTrafficWriter:
    """Writes the current road state to Firebase Realtime Database."""

    def __init__(self, database_url: str, api_key: str, email: str, password: str):
        self.database_url = database_url.rstrip("/")
        self.api_key = api_key
        self.email = email
        self.password = password
        self.id_token: str | None = None
        self.last_sent_state: dict = {}  # Track last sent state to avoid duplicates

    def sign_in(self) -> None:
        response = requests.post(
            "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword",
            params={"key": self.api_key},
            json={"email": self.email, "password": self.password, "returnSecureToken": True},
            timeout=10,
        )
        response.raise_for_status()
        self.id_token = response.json()["idToken"]
        logging.info("Signed in to Firebase.")

    def write(self, data: dict) -> bool:
        """Write to Firebase. Returns True if data was sent, False if skipped."""
        if not self.id_token:
            self.sign_in()
        
        # Create the payload exactly as required
        is_congested = data.get("incident_suspected", False)
        is_under_construction = data.get("roadwork_detected", False)
        
        # ONLY send if there's a change in state (traffic or construction)
        current_state = {
            "isCongested": is_congested,
            "isUnderConstruction": is_under_construction
        }
        
        # Check if state changed from last send
        if self.last_sent_state == current_state:
            logging.debug("State unchanged, skipping Firebase update")
            return False
        
        firebase_record = {
            "isCongested": is_congested,
            "isUnderConstruction": is_under_construction,
            "timestamp": int(time.time() * 1000)
        }
        
        logging.info(f"STATE CHANGE DETECTED! Sending to Firebase: {firebase_record}")
        
        # Use PATCH to update specific fields
        response = requests.patch(
            f"{self.database_url}/traffic.json",
            params={"auth": self.id_token}, 
            json=firebase_record, 
            timeout=10,
        )
        
        # Firebase ID tokens expire. Sign in once more and retry on an auth error.
        if response.status_code in (401, 403):
            self.id_token = None
            self.sign_in()
            response = requests.patch(
                f"{self.database_url}/traffic.json",
                params={"auth": self.id_token}, 
                json=firebase_record, 
                timeout=10,
            )
        
        response.raise_for_status()
        self.last_sent_state = current_state
        logging.info(f"Successfully wrote to Firebase: {firebase_record}")
        return True


class RoadMonitor:
    def __init__(self, args: argparse.Namespace):
        self.args = args
        self.vehicles = YOLO(args.vehicle_model)
        self.custom = YOLO(args.custom_model)
        self.roi = self._parse_roi(args.roi)
        self.police_first_seen: dict[int, float] = {}
        self.last_roadwork_seen = 0.0
        self.last_event_at = 0.0
        self.firebase = self._make_firebase_writer()
        self.last_congested_state = False
        self.last_construction_state = False

    def _make_firebase_writer(self) -> FirebaseTrafficWriter | None:
        if not self.args.firebase:
            return None
        required = {"FIREBASE_DATABASE_URL": self.args.firebase_database_url,
                    "FIREBASE_API_KEY": os.getenv("FIREBASE_API_KEY"),
                    "FIREBASE_EMAIL": os.getenv("FIREBASE_EMAIL"),
                    "FIREBASE_PASSWORD": os.getenv("FIREBASE_PASSWORD")}
        missing = [key for key, value in required.items() if not value]
        if missing:
            raise SystemExit("Firebase is enabled but .env is missing: " + ", ".join(missing))
        return FirebaseTrafficWriter(required["FIREBASE_DATABASE_URL"], **{
            "api_key": required["FIREBASE_API_KEY"], "email": required["FIREBASE_EMAIL"],
            "password": required["FIREBASE_PASSWORD"]})

    @staticmethod
    def _parse_roi(value: str | None) -> np.ndarray | None:
        if not value:
            return None
        try:
            points = json.loads(value)
            polygon = np.array(points, dtype=np.int32)
            if polygon.ndim != 2 or polygon.shape[0] < 3 or polygon.shape[1] != 2:
                raise ValueError
            return polygon
        except (json.JSONDecodeError, ValueError):
            raise SystemExit("--roi must be JSON points, e.g. '[[20,300],[620,300],[640,470],[0,470]]'")

    @staticmethod
    def tracked_objects(result) -> Iterable[SeenObject]:
        boxes = result.boxes
        if boxes is None or boxes.id is None:
            return []
        xyxy = boxes.xyxy.cpu().numpy().astype(int)
        ids = boxes.id.cpu().numpy().astype(int)
        classes = boxes.cls.cpu().numpy().astype(int)
        return [SeenObject(class_name(result.names, cls), tid,
                           ((box[0] + box[2]) // 2, (box[1] + box[3]) // 2))
                for box, tid, cls in zip(xyxy, ids, classes)]

    def analyse(self, frame: np.ndarray, now: float) -> tuple[dict, np.ndarray]:
        # persist=True retains ByteTrack IDs between consecutive frames.
        vehicle_result = self.vehicles.track(frame, persist=True, verbose=False,
                                             conf=self.args.confidence, tracker="bytetrack.yaml")[0]
        custom_result = self.custom.track(frame, persist=True, verbose=False,
                                          conf=self.args.confidence, tracker="bytetrack.yaml")[0]

        counts: dict[str, set[int]] = defaultdict(set)
        for obj in self.tracked_objects(vehicle_result):
            if obj.label in VEHICLE_NAMES and inside_roi(obj.point, self.roi):
                counts[obj.label].add(obj.track_id)

        roadwork = False
        active_police_ids: set[int] = set()
        for obj in self.tracked_objects(custom_result):
            if not inside_roi(obj.point, self.roi):
                continue
            if obj.label in ROADWORK_NAMES:
                roadwork = True
            if obj.label in POLICE_NAMES:
                active_police_ids.add(obj.track_id)
                self.police_first_seen.setdefault(obj.track_id, now)

        # A brief missed frame must not reset the roadwork state.
        if roadwork:
            self.last_roadwork_seen = now
        roadwork_active = now - self.last_roadwork_seen <= self.args.roadwork_hold_seconds

        police_duration = max((now - self.police_first_seen[track_id]
                               for track_id in active_police_ids), default=0.0)
        police_persistent = police_duration >= self.args.police_seconds
        vehicle_counts = {name: len(counts[name]) for name in sorted(VEHICLE_NAMES - {"trycicle"})}
        total = sum(vehicle_counts.values())
        
        # Use constants for thresholds
        load = "high" if total >= self.args.high_count else "medium" if total >= self.args.medium_count else "low"
        
        # Determine congestion state - simple: high traffic = congested
        is_congested = load == "high"
        
        payload = {
            "timestamp": time.strftime("%Y-%m-%dT%H:%M:%S%z"),
            "vehicle_counts": vehicle_counts,
            "vehicle_total": total,
            "traffic_load": load,
            "roadwork_detected": roadwork_active,
            "police_present_seconds": round(police_duration),
            "police_persistent": police_persistent,
            "incident_suspected": is_congested,  # This maps to isCongested in Firebase
        }
        
        rendered = self.draw(frame.copy(), vehicle_result, custom_result, payload)
        return payload, rendered

    def draw(self, frame, vehicle_result, custom_result, data):
        # plot() expects RGB format, which is what picamera2 provides
        frame = vehicle_result.plot(img=frame, labels=True, boxes=True)
        frame = custom_result.plot(img=frame, labels=True, boxes=True)
        if self.roi is not None:
            cv2.polylines(frame, [self.roi], True, (0, 255, 255), 2)
        text = f"Vehicles: {data['vehicle_total']} ({data['traffic_load'].upper()})"
        cv2.putText(frame, text, (12, 30), cv2.FONT_HERSHEY_SIMPLEX, .75, (0, 255, 0), 2)
        cv2.putText(frame, f"Roadwork: {data['roadwork_detected']}  Police: {data['police_present_seconds']}s",
                    (12, 58), cv2.FONT_HERSHEY_SIMPLEX, .6, (0, 255, 0), 2)
        if data["incident_suspected"]:
            cv2.putText(frame, "INCIDENT / CONGESTION SUSPECTED", (12, 88),
                        cv2.FONT_HERSHEY_SIMPLEX, .65, (0, 0, 255), 2)
        return frame

    def send(self, data: dict, force: bool = False):
        """Send data to Firebase immediately when state changes."""
        sent = False
        try:
            if self.firebase:
                # This will return True only if data was actually sent (state changed)
                sent = self.firebase.write(data)
            
            if self.args.webhook_url:
                response = requests.post(self.args.webhook_url, json=data, timeout=5)
                response.raise_for_status()
                sent = True
            
            if sent:
                logging.info(f"Data sent. Congested: {data['incident_suspected']}, Roadwork: {data['roadwork_detected']}")
            
        except requests.RequestException as exc:
            logging.warning("Could not send traffic status: %s", exc)


def parse_args() -> argparse.Namespace:
    load_dotenv()
    parser = argparse.ArgumentParser()
    parser.add_argument("--vehicle-model", default="yolo26n.pt")
    parser.add_argument("--custom-model", default="best.pt")
    parser.add_argument("--webhook-url", help="Your app API endpoint; omit to only display/log")
    parser.add_argument("--firebase", action="store_true", help="Write directly to Firebase Realtime Database")
    parser.add_argument("--firebase-database-url", default=os.getenv("FIREBASE_DATABASE_URL"),
                        help="Firebase database URL (normally placed in .env)")
    parser.add_argument("--confidence", type=float, default=CONFIDENCE_THRESHOLD)
    parser.add_argument("--width", type=int, default=CAMERA_WIDTH)
    parser.add_argument("--height", type=int, default=CAMERA_HEIGHT)
    parser.add_argument("--roi", help="JSON polygon defining the monitored road area")
    parser.add_argument("--medium-count", type=int, default=MEDIUM_COUNT)
    parser.add_argument("--high-count", type=int, default=HIGH_COUNT)
    parser.add_argument("--police-seconds", type=int, default=POLICE_SECONDS)
    parser.add_argument("--roadwork-hold-seconds", type=int, default=ROADWORK_HOLD_SECONDS)
    parser.add_argument("--event-cooldown-seconds", type=int, default=EVENT_COOLDOWN_SECONDS)
    parser.add_argument("--no-preview", action="store_true")
    return parser.parse_args()


def main():
    args = parse_args()
    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
    
    logging.info("=== TRAFFIC MONITOR STARTING ===")
    logging.info(f"Medium count: {args.medium_count}, High count: {args.high_count}")
    logging.info(f"Police threshold: {args.police_seconds}s, Roadwork hold: {args.roadwork_hold_seconds}s")
    
    monitor = RoadMonitor(args)
    camera = Picamera2()
    camera.configure(camera.create_video_configuration(main={"size": (args.width, args.height), "format": "RGB888"}))
    camera.start()
    logging.info("Camera started. Press q in the preview window to stop.")
    
    try:
        frame_count = 0
        while True:
            # Get frame from camera (already in RGB format, no conversion needed)
            frame = camera.capture_array()
            data, rendered = monitor.analyse(frame, time.monotonic())
            monitor.send(data)  # Sends immediately if state changed
            
            # Log summary every 10 frames
            frame_count += 1
            if frame_count % 10 == 0:
                logging.info(f"Frame {frame_count}: Vehicles={data['vehicle_total']}, "
                           f"Load={data['traffic_load']}, "
                           f"Congested={data['incident_suspected']}, "
                           f"Roadwork={data['roadwork_detected']}")
            
            if not args.no_preview:
                cv2.imshow("Road monitor", rendered)
                if cv2.waitKey(1) & 0xFF == ord("q"):
                    break
    finally:
        camera.stop()
        cv2.destroyAllWindows()
        logging.info("Traffic monitor stopped.")


if __name__ == "__main__":
    main()