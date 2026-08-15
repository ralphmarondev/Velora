from picamera2 import Picamera2
from ultralytics import YOLO
import cv2

model = YOLO("yolo11n.pt")

camera = Picamera2()
camera.configure(camera.create_preview_configuration(
    main={"size": (640, 480), "format": "RGB888"}
))
camera.start()

while True:
    frame = camera.capture_array()

    results = model(frame, imgsz=320, conf=0.5, verbose=False)
    annotated = results[0].plot()

    cv2.imshow("YOLO11 - Camera Module 3", annotated)

    if cv2.waitKey(1) & 0xFF == ord("q"):
        break

camera.stop()
cv2.destroyAllWindows()