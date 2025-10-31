<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SOS Offline Mesh Chat (HackX 3.0)</title>
    <!-- Load Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- Custom theme configuration -->
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        'dark-bg': '#121212',
                        'dark-card': '#1F1F1F',
                        'red-primary': '#FF3B30',
                        'red-accent': '#FF6347',
                    },
                    fontFamily: {
                        sans: ['Inter', 'sans-serif'],
                    }
                }
            }
        }
    </script>
    <style>
        /* Custom scrollbar for a darker look */
        body::-webkit-scrollbar {
            width: 8px;
        }
        body::-webkit-scrollbar-track {
            background: #121212;
        }
        body::-webkit-scrollbar-thumb {
            background-color: #3A3A3A;
            border-radius: 20px;
        }
        /* Style for the code blocks */
        pre {
            background-color: #1F1F1F !important;
            padding: 1rem;
            border-radius: 0.5rem;
            border: 1px solid #3A3A3A;
            overflow-x: auto;
        }
        table th, table td {
            padding: 0.75rem;
            border-bottom: 1px solid #3A3A3A;
            text-align: left;
        }
        table th {
            color: #FF3B30;
            font-weight: 600;
        }
    </style>
</head>
<body class="bg-dark-bg text-gray-100 font-sans min-h-screen">

    <div class="container mx-auto p-4 sm:p-8 max-w-4xl">

        <!-- Header Section -->
        <header class="text-center mb-12 border-b border-red-primary pb-6">
            <h1 class="text-4xl sm:text-5xl font-extrabold text-red-primary mb-2">
                🆘 Offline SOS Mesh Chat (HackX 3.0)
            </h1>
            <p class="text-lg text-gray-300 mb-4">
                <b>An offline peer-to-peer emergency communication app powered by Google Nearby Connections API.</b>
            </p>
            <p class="text-md italic text-gray-400 mb-6">
                Built for disaster response scenarios where internet and cellular networks have failed.
            </p>
            
            <!-- Badges -->
            <div class="flex justify-center space-x-4">
                <img src="https://img.shields.io/badge/Platform-Android-10AC84?style=for-the-badge&logo=android" alt="Platform: Android" class="h-8 rounded-full">
                <img src="https://img.shields.io/badge/Language-Java-007396?style=for-the-badge&logo=openjdk" alt="Language: Java" class="h-8 rounded-full">
                <img src="https://img.shields.io/badge/Networking-Nearby_Connections_API-CC0000?style=for-the-badge&logo=google-cloud" alt="API: Nearby Connections" class="h-8 rounded-full">
            </div>
        </header>

        <!-- About Section -->
        <section class="mb-10">
            <h2 class="text-3xl font-bold text-red-accent border-b border-gray-700 pb-2 mb-4">💡 About the Project</h2>
            <p class="mb-4 text-gray-300">
                <span class="font-bold text-red-accent">Offline SOS Mesh Chat</span> is an emergency application designed to work when traditional infrastructure fails. It enables users to broadcast 
                <span class="font-bold">SOS Alerts</span> and establish a real-time, peer-to-peer chat with other nearby devices, entirely 
                <span class="font-bold text-red-accent">without requiring internet or cellular service</span>.
            </p>
            <p class="mb-4 text-gray-300">
                By utilizing the <span class="font-bold">Google Nearby Connections API</span>, the app creates a localized 
                <span class="font-bold">mesh network</span> using Bluetooth, Wi-Fi Direct, and Wi-Fi Hotspot technology.
            </p>

            <h3 class="text-xl font-semibold text-gray-200 mt-6 mb-2">Problem Statement (Disaster Response PS #1)</h3>
            <p class="text-gray-400">
                This project tackles the HackX 3.0 problem statement focused on **Emergency Communication Mesh**, ensuring survivors and first responders can coordinate effectively in disaster-hit zones where centralized networks are often destroyed.
            </p>
        </section>

        <!-- Key Features Section -->
        <section class="mb-10">
            <h2 class="text-3xl font-bold text-red-accent border-b border-gray-700 pb-2 mb-6">⚙️ Key Features</h2>
            <table class="w-full border-collapse">
                <thead>
                    <tr>
                        <th class="w-1/4">Feature</th>
                        <th class="w-3/4">Description</th>
                    </tr>
                </thead>
                <tbody class="text-gray-300">
                    <tr><td>🚨 <b>SOS Host/Broadcast</b></td><td>A user initiates an SOS by entering a message (e.g., "Trapped, need medical help") and begins <b>Advertising</b> the crisis details as a room name.</td></tr>
                    <tr><td>🔍 <b>SOS Discovery</b></td><td>Other nearby users <b>Discover</b> active SOS rooms in the main list, showing the host's message and device name.</td></tr>
                    <tr><td>💬 <b>Mesh Chat</b></td><td>Once connected, devices join a secure, local multi-party chat room. Messages are reliably routed peer-to-peer.</td></tr>
                    <tr><td>🌐 <b>Offline First</b></td><td>The entire system operates without external servers, making it robust during complete network outages.</td></tr>
                    <tr><td>🎨 <b>Dark Red UI Theme</b></td><td>A clean, minimalistic user interface designed with a dark background and red accents to reflect the emergency purpose.</td></tr>
                    <tr><td>👤 <b>User Identification</b></td><td>All chat messages display the sender's device name, ensuring clear communication flow in the mesh.</td></tr>
                </tbody>
            </table>
        </section>

        <!-- App Structure Section -->
        <section class="mb-10">
            <h2 class="text-3xl font-bold text-red-accent border-b border-gray-700 pb-2 mb-6">🧩 App Structure (Android)</h2>
            <pre class="text-sm">
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
</pre>
        </section>

        <!-- Tech Stack Section -->
        <section class="mb-10">
            <h2 class="text-3xl font-bold text-red-accent border-b border-gray-700 pb-2 mb-6">🛠️ Tech Stack</h2>
            <table class="w-full border-collapse">
                <thead>
                    <tr>
                        <th class="w-1/4">Component</th>
                        <th class="w-3/4">Description</th>
                    </tr>
                </thead>
                <tbody class="text-gray-300">
                    <tr><td><b>Language</b></td><td>Java</td></tr>
                    <tr><td><b>Framework</b></td><td>Android SDK, Material Design (for components)</td></tr>
                    <tr><td><b>Offline Networking</b></td><td><b>Google Nearby Connections API</b> (Strategy: <code>P2P_CLUSTER</code>)</td></tr>
                    <tr><td><b>State Management</b></td><td>Local Activity and <code>SharedPreferences</code> for device name persistence</td></tr>
                    <tr><td><b>Data Format</b></td><td>JSON Payloads (<code>Payload.Type.BYTES</code>) for message transmission</td></tr>
                </tbody>
            </table>
        </section>

        <!-- How It Works Section -->
        <section class="mb-10">
            <h2 class="text-3xl font-bold text-red-accent border-b border-gray-700 pb-2 mb-6">🗺️ How It Works</h2>
            <p class="mb-4 text-gray-300">
                The application leverages the power of the Nearby Connections API to automatically manage the underlying radio technologies (Bluetooth, Wi-Fi Direct) necessary for connection.
            </p>
            <ol class="list-decimal list-inside space-y-3 text-gray-300 ml-4">
                <li>
                    <span class="font-bold">SOS Host Initiation (<code>SOSActivity</code>):</span>
                    <ul>
                        <li class="list-disc ml-6 text-sm text-gray-400">The user provides an emergency message.</li>
                        <li class="list-disc ml-6 text-sm text-gray-400">The device starts <b>Advertising</b>, packaging the SOS message and host name into the endpoint name (as a JSON string).</li>
                    </ul>
                </li>
                <li>
                    <span class="font-bold">Discoverer/Client Joins (<code>MainActivity</code>):</span>
                    <ul>
                        <li class="list-disc ml-6 text-sm text-gray-400">Clients constantly perform <b>Discovery</b>.</li>
                        <li class="list-disc ml-6 text-sm text-gray-400">When an SOS signal is found, the endpoint name is parsed, and a room card is displayed.</li>
                        <li class="list-disc ml-6 text-sm text-gray-400">Clicking "Join" sends a <code>requestConnection</code>.</li>
                    </ul>
                </li>
                <li>
                    <span class="font-bold">Chat Mesh Formation (<code>ChatActivity</code>):</span>
                    <ul>
                        <li class="list-disc ml-6 text-sm text-gray-400">Once the connection is accepted (which is automatic in this implementation), the endpoint is added to a local list of connected peers (<code>connectedEndpoints</code>).</li>
                        <li class="list-disc ml-6 text-sm text-gray-400">The host continues to accept new connections, and the client may now act as a relay if needed by the <code>P2P_CLUSTER</code> strategy.</li>
                    </ul>
                </li>
                <li>
                    <span class="font-bold">Message Exchange:</span>
                    <ul>
                        <li class="list-disc ml-6 text-sm text-gray-400">Sending a message multicasts the byte payload to <b>all</b> connected endpoints in the <code>connectedEndpoints</code> set.</li>
                    </ul>
                </li>
            </ol>
        </section>

        <!-- Permissions Section -->
        <section class="mb-10">
            <h2 class="text-3xl font-bold text-red-accent border-b border-gray-700 pb-2 mb-6">🧾 Permissions Required</h2>
            <p class="mb-4 text-gray-300">
                The following permissions are critical for the Nearby Connections API to function correctly, particularly for discovering and advertising using Bluetooth and Wi-Fi.
            </p>
            <table class="w-full border-collapse">
                <thead>
                    <tr>
                        <th class="w-1/4">Permission</th>
                        <th class="w-3/4">Purpose</th>
                    </tr>
                </thead>
                <tbody class="text-gray-300">
                    <tr><td><code>BLUETOOTH_CONNECT/SCAN</code></td><td>Nearby data transmission and device scanning.</td></tr>
                    <tr><td><code>ACCESS_WIFI_STATE/CHANGE_WIFI_STATE</code></td><td>Setting up high-bandwidth connections (Wi-Fi Hotspot/Direct).</td></tr>
                    <tr><td><code>ACCESS_FINE_LOCATION</code></td><td><b>Required by Android</b> to access device-to-device communication technologies.</td></tr>
                    <tr><td><code>NEARBY_WIFI_DEVICES</code></td><td>Required for devices running Android 13+ (API 33).</td></tr>
                </tbody>
            </table>
        </section>

        <!-- Setup & Run Section -->
        <section class="mb-10">
            <h2 class="text-3xl font-bold text-red-accent border-b border-gray-700 pb-2 mb-6">🚀 Setup & Run</h2>

            <h3 class="text-xl font-semibold text-gray-200 mt-6 mb-2">1. Clone the Repository:</h3>
            <pre class="text-sm bg-dark-card"><code class="text-red-primary">git clone https://github.com/&lt;your-username&gt;/sos-mesh-hackathon.git</code></pre>

            <h3 class="text-xl font-semibold text-gray-200 mt-6 mb-2">2. Dependencies Check (Android Studio):</h3>
            <p class="text-gray-300 mb-2">
                Ensure your <code>app/build.gradle</code> file includes the necessary Google Play Services dependencies:
            </p>
            <pre class="text-sm bg-dark-card"><code class="text-yellow-400">
// Required for Nearby Connections
implementation 'com.google.android.gms:play-services-nearby:18.3.0'
// Recommended for location-based features (though not fully implemented, needed for permission context)
implementation 'com.google.android.gms:play-services-location:21.0.1'
</code></pre>

            <h3 class="text-xl font-semibold text-gray-200 mt-6 mb-2">3. Run on Real Devices:</h3>
            <p class="text-gray-300">
                The <span class="font-bold text-red-accent">Nearby Connections API does not function reliably on Android Emulators</span>. You must build and run the app on <b>two or more physical Android devices</b> to test the P2P chat functionality. Ensure both devices have <b>Location (GPS) and Bluetooth enabled</b>.
            </p>
        </section>

        <!-- Future Enhancements Section -->
        <section class="mb-10">
            <h2 class="text-3xl font-bold text-red-accent border-b border-gray-700 pb-2 mb-6">📍 Future Enhancements</h2>
            <ul class="list-disc list-inside space-y-2 text-gray-300 ml-4">
                <li>✅ <b>Auto Reconnect/Relaying:</b> Explicitly implement logic to retry connections and demonstrate true mesh relaying across non-adjacent devices.</li>
                <li>✅ <b>Location Display:</b> Integrate the Location API (<code>FusedLocationProviderClient</code>) in <code>SOSActivity</code> to send real-time coordinates and display a map link in <code>MainActivity</code>.</li>
                <li>✅ <b>Image/Payload Sharing:</b> Allow the transfer of image payloads (small photos of the emergency) over the established connection.</li>
                <li>✅ <b>Security:</b> Add a manual confirmation dialog for accepting incoming connections instead of automatic acceptance.</li>
            </ul>
        </section>

        <!-- Author Section -->
        <section class="mb-10">
            <h2 class="text-3xl font-bold text-red-accent border-b border-gray-700 pb-2 mb-6">🧑‍💻 Author</h2>
            <table class="w-full border-collapse">
                <thead>
                    <tr>
                        <th>Role</th>
                        <th>Name</th>
                        <th>Link</th>
                    </tr>
                </thead>
                <tbody class="text-gray-300">
                    <tr><td>Team</td><td>[Your Team Name]</td><td>[Link to GitHub/LinkedIn]</td></tr>
                </tbody>
            </table>
            <p class="text-gray-400 mt-4 text-sm">
                *Replace with your team details*
            </p>
        </section>

        <!-- Footer -->
        <footer class="text-center py-6 border-t border-gray-700 text-gray-500 text-sm">
            &copy; 2025 Offline SOS Mesh Chat. Built for HackX 3.0 at MUJ.
        </footer>
    </div>

</body>
</html>
