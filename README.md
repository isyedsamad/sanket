<h1 align="center">Sanket - an offline SOS Mesh Chat</h1>

<p align="center">
  <b>Offline emergency communication app powered by Google Nearby Connections API</b><br>
  <i>Built entirely offline — no internet, no servers, just device-to-device mesh chat.</i>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Language-Java-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Nearby_Connections-API-red?style=for-the-badge" />
</p>

---

## 📱 About the Project

**Offline SOS Mesh Chat** allows users to send SOS alerts and communicate with nearby devices **without internet** — using **Google’s Nearby Connections API**.  
Each SOS broadcast includes:
- 📝 SOS message  
- 📍 GPS coordinates (latitude, longitude)  
- 📡 Real-time offline chat between connected users  

All devices form a **peer-to-peer mesh**, enabling local communication even in network outages or remote areas.

---

## ⚙️ Key Features

| Feature | Description |
|----------|-------------|
| 🚨 **SOS Broadcast** | Send your SOS message and location instantly via Nearby Advertising. |
| 📍 **Location Sharing** | Automatically includes your latitude & longitude (if permission granted). |
| 💬 **Offline Chat** | Devices within range can connect and chat in real time. |
| 📶 **Mesh Network** | Works without internet or cellular — completely offline. |
| 🕶️ **Dark Red UI Theme** | Minimalistic dark design inspired by emergency tones. |
| 🔒 **Privacy First** | No data leaves your device; everything stays local. |

---

## 🛠️ Tech Stack

| Component | Description |
|------------|-------------|
| **Language** | Java |
| **Framework** | Android SDK |
| **Offline Networking** | Google Nearby Connections API |
| **Location** | FusedLocationProviderClient |
| **UI** | Material Design + Custom Dark Theme |

---

## 🗺️ How It Works

### 🧭 SOS Host
1. Opens the **SOS Screen**  
2. Enters a message → starts advertising  
3. Broadcasts data as **JSON payload** containing message + latitude + longitude  

### 🔍 Discoverer
1. Scans for nearby SOS signals  
2. Sees a **list of discovered users** with name, message, and map link  
3. Taps a user → connects to their room  
4. Gets confirmation toast on successful connection  

### 💬 Chat (Future Scope)
Once connected, devices can exchange:
- SOS messages
- Location updates  
- (Planned) Images and files  

---

## 🧾 Permissions Required

| Permission | Purpose |
|-------------|----------|
| `BLUETOOTH_CONNECT` | Nearby data transmission |
| `BLUETOOTH_SCAN` | Device discovery |
| `ACCESS_WIFI_STATE` | Peer connection |
| `ACCESS_FINE_LOCATION` | Attach location to SOS (optional) |

---

## 🚀 Setup & Run

### 1️⃣ Clone the repository
```bash
git clone https://github.com/<your-username>/<your-repo-name>.git
```
## 2️⃣ Open in Android Studio

- Open the cloned project  
- Sync Gradle  

---

## 3️⃣ Add Dependencies

Add the following dependencies in your **app-level `build.gradle`** file:

```gradle
implementation 'com.google.android.gms:play-services-nearby:18.3.0'
implementation 'com.google.android.gms:play-services-location:21.0.1'
```

## 4️⃣ Build & Run

Run the app on **two or more real Android devices**.  
> ⚠️ The Nearby Connections API does **not** work on emulators!

---

## 📍 Future Enhancements

✅ Auto reconnect to active SOS after disconnection  
✅ Allow image/payload sharing over mesh  
