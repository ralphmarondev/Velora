import random
import time
import requests
import os
from dotenv import load_dotenv

load_dotenv()

API_KEY = os.getenv("API_KEY")
DATABASE_URL = os.getenv("DATABASE_URL")

print("=== Velora Raspberry Pi Simulator ===\n")

email = input("Firebase Email: ").strip()
password = input("Firebase Password: ").strip()

# Sign in to Firebase Authentication
response = requests.post(
    f"https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key={API_KEY}",
    json={
        "email": email,
        "password": password,
        "returnSecureToken": True
    }
)

if not response.ok:
    print("Login failed.")
    print(response.text)
    exit(1)

id_token = response.json()["idToken"]

print("Successfully signed in.\n")

while True:
    traffic_record = {
        "isCongested": random.choice([True, False]),
        "isUnderConstruction": random.choice([True, False]),
        "timestamp": int(time.time() * 1000)
    }

    response = requests.put(
        f"{DATABASE_URL}/traffic.json?auth={id_token}",
        json=traffic_record
    )

    if response.ok:
        print("Updated:", traffic_record)
    else:
        print("Update failed:", response.status_code)
        print(response.text)

    time.sleep(3)