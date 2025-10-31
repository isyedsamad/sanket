<h1 align="center">Sanket - an offline SOS Mesh Chat (HackX 3.0)</h1>

<p align="center">
  <b>An offline peer-to-peer emergency communication app powered by Google Nearby Connections API.</b>




  <i>Built for disaster response scenarios where internet and cellular networks have failed.</i>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=for-the-badge" alt="Platform: Android"/>
  <img src="https://img.shields.io/badge/Language-Java-blue?style=for-the-badge" alt="Language: Java"/>
  <img src="https://img.shields.io/badge/Nearby_Connections-API-red?style=for-the-badge" alt="API: Nearby Connections"/>
</p>

💡 About the Project

Offline SOS Mesh Chat is an emergency application designed to work when traditional infrastructure fails. It enables users to broadcast SOS Alerts and establish a real-time, peer-to-peer chat with other nearby devices, entirely without requiring internet or cellular service.

By utilizing the Google Nearby Connections API, the app creates a localized mesh network using Bluetooth, Wi-Fi Direct, and Wi-Fi Hotspot technology.

Problem Statement (Disaster Response PS #1)

This project tackles the HackX 3.0 problem statement focused on Emergency Communication Mesh, ensuring survivors and first responders can coordinate effectively in disaster-hit zones where centralized networks are often destroyed.

⚙️ Key Features

Feature

Description

🚨 SOS Host/Broadcast

A user initiates an SOS by entering a message (e.g., "Trapped, need medical help") and begins Advertising the crisis details as a room name.

🔍 SOS Discovery

Other nearby users Discover active SOS rooms in the main list, showing the host's message and device name.

💬 Mesh Chat

Once connected, devices join a secure, local multi-party chat room. Messages are reliably routed peer-to-peer.

🌐 Offline First

The entire system operates without external servers, making it robust during complete network outages.

🎨 Dark Red UI Theme

A clean, minimalistic user interface designed with a dark background and red accents to reflect the emergency purpose.

👤 User Identification

All chat messages display the sender's device name, ensuring clear communication flow in the mesh.

🧩 App Structure (Android)

app/
├── java/com/hackx/sosmesh/
│   ├── MainActivity.java           # Handles Discovery, lists SOS rooms, initiates connection requests.
│   ├── SOSActivity.java            # Gathers SOS message and starts Advertising as a Host.
│   └── ChatActivity.java           # Manages the chat room, payload transmission, and ongoing connection lifecycle.
│
└── res/
    ├── layout/
    │   ├── activity_main.xml       # Main screen with the large SOS button and room list.
    │   ├── activity_sos.xml        # Layout for entering the emergency message.
    │   ├── activity_chat.xml       # Primary chat interface.
    │   ├── item_sos_room.xml       # Card layout for discovered SOS signals.
    │   └── item_chat_message.xml   # Layout for incoming/outgoing chat bubbles.
    └── values/
        └── styles.xml, colors.xml  # Custom Dark/Red theme definition.


🛠️ Tech Stack

Component

Description

Language

Java

Framework

Android SDK, Material Design (for components)

Offline Networking

Google Nearby Connections API (Strategy: P2P_CLUSTER)

State Management

Local Activity and SharedPreferences for device name persistence

Data Format

JSON Payloads (Payload.Type.BYTES) for message transmission

🗺️ How It Works

The application leverages the power of the Nearby Connections API to automatically manage the underlying radio technologies (Bluetooth, Wi-Fi Direct) necessary for connection.

SOS Host Initiation (SOSActivity):

The user provides an emergency message.

The device starts Advertising, packaging the SOS message and host name into the endpoint name (as a JSON string).

Discoverer/Client Joins (MainActivity):

Clients constantly perform Discovery.

When an SOS signal is found, the endpoint name is parsed, and a room card is displayed.

Clicking "Join" sends a requestConnection.

Chat Mesh Formation (ChatActivity):

Once the connection is accepted (which is automatic in this implementation), the endpoint is added to a local list of connected peers (connectedEndpoints).

The host continues to accept new connections, and the client may now act as a relay if needed by the P2P_CLUSTER strategy.

Message Exchange:

Sending a message multicasts the byte payload to all connected endpoints in the connectedEndpoints set.

🧾 Permissions Required

The following permissions are critical for the Nearby Connections API to function correctly, particularly for discovering and advertising using Bluetooth and Wi-Fi.

Permission

Purpose

BLUETOOTH_CONNECT/SCAN

Nearby data transmission and device scanning.

ACCESS_WIFI_STATE/CHANGE_WIFI_STATE

Setting up high-bandwidth connections (Wi-Fi Hotspot/Direct).

ACCESS_FINE_LOCATION

Required by Android to access device-to-device communication technologies.

NEARBY_WIFI_DEVICES

Required for devices running Android 13+ (API 33).

🚀 Setup & Run

Clone the Repository:

git clone [https://github.com/](https://github.com/)<your-username>/sos-mesh-hackathon.git


Dependencies Check (Android Studio):
Ensure your app/build.gradle file includes the necessary Google Play Services dependencies:

// Required for Nearby Connections
implementation 'com.google.android.gms:play-services-nearby:18.3.0'
// Recommended for location-based features (though not fully implemented, needed for permission context)
implementation 'com.google.android.gms:play-services-location:21.0.1'


Run on Real Devices:
The Nearby Connections API does not function reliably on Android Emulators. You must build and run the app on two or more physical Android devices to test the P2P chat functionality. Ensure both devices have Location (GPS) and Bluetooth enabled.

📍 Future Enhancements

These additions can be considered to further enhance the product:

✅ Auto Reconnect/Relaying: Explicitly implement logic to retry connections and demonstrate true mesh relaying across non-adjacent devices.

✅ Location Display: Integrate the Location API (FusedLocationProviderClient) in SOSActivity to send real-time coordinates and display a map link in MainActivity.

✅ Image/Payload Sharing: Allow the transfer of image payloads (small photos of the emergency) over the established connection.

✅ Security: Add a manual confirmation dialog for accepting incoming connections instead of automatic acceptance.
