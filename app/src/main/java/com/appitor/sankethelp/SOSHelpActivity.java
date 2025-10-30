package com.appitor.sankethelp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.*;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SOSHelpActivity extends AppCompatActivity {
    private String deviceName = "Device-" + Build.MODEL;
    private static final String SERVICE_ID = "com.example.nearbytestapp";
    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    private final List<String> connectedEndpoints = new ArrayList<>();
    private ConnectionsClient connectionsClient;
    private String connectedEndpointId;
    private TextView tvMessages;
    private EditText etMessage;
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_ADVERTISE,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.NEARBY_WIFI_DEVICES
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soshelp);
        Button btnAdvertise = findViewById(R.id.btnAdvertise);
        Button btnDiscover = findViewById(R.id.btnDiscover);
        Button btnSend = findViewById(R.id.btnSend);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);

        if (!hasPermissions()) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        connectionsClient = Nearby.getConnectionsClient(this);

        btnAdvertise.setOnClickListener(v -> startAdvertising());
        btnDiscover.setOnClickListener(v -> startDiscovery());
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private boolean hasPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startAdvertising() {
        AdvertisingOptions options = new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        connectionsClient.startAdvertising(
                        "Device A",
                        SERVICE_ID,
                        connectionLifecycleCallback,
                        options
                ).addOnSuccessListener(unused -> toast("Advertising..."))
                .addOnFailureListener(e -> toast("Error: " + e.getMessage()));
    }

    // 2️⃣ Start Discovery
    private void startDiscovery() {
        DiscoveryOptions options = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        connectionsClient.startDiscovery(
                        SERVICE_ID,
                        endpointDiscoveryCallback,
                        options
                ).addOnSuccessListener(unused -> toast("Discovering..."))
                .addOnFailureListener(e -> toast("Error: " + e.getMessage()));
    }

    // 3️⃣ Connection Callbacks
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback);
            connectedEndpointId = endpointId;
            toast("Connection initiated with: " + endpointId);
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
            if (result.getStatus().isSuccess()) {
                connectedEndpoints.add(endpointId);
                toast("Connected to: " + endpointId);
            } else {
                toast("Connection failed");
            }

        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {
            toast("Disconnected");
        }
    };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info) {
            connectionsClient.requestConnection("Device B", endpointId, connectionLifecycleCallback);
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            toast("Endpoint lost");
        }
    };

    // 4️⃣ Send & Receive Messages
    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            String json = new String(payload.asBytes(), StandardCharsets.UTF_8);

            try {
                JSONObject obj = new JSONObject(json);
                String sender = obj.getString("sender");
                String message = obj.getString("message");

                if (sender.equals(deviceName)) {
                    tvMessages.append("\nYou: " + message);
                } else {
                    tvMessages.append("\n" + sender + ": " + message);
                }

                // Relay to others (if this device is the hub)
                for (String id : connectedEndpoints) {
                    if (!id.equals(endpointId)) {
                        connectionsClient.sendPayload(id, payload);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) { }
    };

    private void sendMessage() {
        if (connectedEndpoints.isEmpty()) {
            toast("No connections yet!");
            return;
        }

        String msgText = etMessage.getText().toString();
        if (msgText.isEmpty()) return;

        // Create JSON message
        String json = "{\"sender\":\"" + deviceName + "\",\"message\":\"" + msgText + "\"}";
        Payload payload = Payload.fromBytes(json.getBytes(StandardCharsets.UTF_8));

        for (String endpointId : connectedEndpoints) {
            connectionsClient.sendPayload(endpointId, payload);
        }

        tvMessages.append("\nYou: " + msgText);
        etMessage.setText("");
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}