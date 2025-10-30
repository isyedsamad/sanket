package com.appitor.sankethelp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SOSActivity extends AppCompatActivity {
    private ConnectionsClient connectionsClient;
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private Double currentLat = null, currentLon = null;
    private EditText messageInput;
    private Button startSOSBtn;
    private TextView statusText;

    private final Strategy STRATEGY = Strategy.P2P_CLUSTER;
    private boolean isAdvertising = false;

    // Store connected endpoints
    private final Map<String, String> connectedDevices = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        connectionsClient = Nearby.getConnectionsClient(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation(); // Try to fetch the location when activity starts

        messageInput = findViewById(R.id.messageInput);
        startSOSBtn = findViewById(R.id.startSOSBtn);
        statusText = findViewById(R.id.statusText);

        connectionsClient = Nearby.getConnectionsClient(this);
        requestNearbyPermissions();

        startSOSBtn.setOnClickListener(v -> {
            if (!isAdvertising) startAdvertising();
            else sendMessageToAll();
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLon = location.getLongitude();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void requestNearbyPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.NEARBY_WIFI_DEVICES
            }, 1);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
        }
    }

    private void startAdvertising() {
        String msg = messageInput.getText().toString().trim();
        if (msg.isEmpty()) {
            msg = "Help Needed!";
        }
        try {
            JSONObject json = new JSONObject();
            json.put("name", Build.MODEL);
            json.put("msg", msg);
            json.put("latitude", currentLat != null ? currentLat : 0);
            json.put("longitude", currentLon != null ? currentLon : 0);

            AdvertisingOptions options =
                    new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();

            connectionsClient.startAdvertising(
                    json.toString(),               // endpoint name = JSON
                    getPackageName(),              // serviceId
                    connectionLifecycleCallback,   // callback
                    options
            ).addOnSuccessListener(unused -> {
                isAdvertising = true;
                startSOSBtn.setText("SEND MESSAGE");
                statusText.setText("Status: SOS Active 🚨 (waiting for connections)");
            }).addOnFailureListener(e -> {
                statusText.setText("Status: Failed to start SOS");
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            });
        } catch (Exception e) {
            Toast.makeText(this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
//        connectionsClient.stopAllEndpoints();
//        connectionsClient.stopDiscovery();
//        connectionsClient.stopAdvertising();
//        AdvertisingOptions advertisingOptions =
//                new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
//        statusText.setText("Status: Starting SOS...");
//        Toast.makeText(this, "Broadcasting SOS...", Toast.LENGTH_SHORT).show();
//        connectionsClient.startAdvertising(
//                Build.MODEL, // device name
//                getPackageName(),
//                connectionLifecycleCallback,
//                advertisingOptions
//        ).addOnSuccessListener(unused -> {
//            isAdvertising = true;
//            startSOSBtn.setText("SEND MESSAGE");
//            statusText.setText("Status: SOS Active 🚨 (waiting for connections)");
//        }).addOnFailureListener(e -> {
//            statusText.setText("Status: Failed to start SOS");
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        });
    }

    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Toast.makeText(SOSActivity.this, "Connection requested by " + connectionInfo.getEndpointName(), Toast.LENGTH_SHORT).show();
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().getStatusCode() == ConnectionsStatusCodes.STATUS_OK) {
                        connectedDevices.put(endpointId, "Device");
                        statusText.setText("Connected: " + connectedDevices.size() + " user(s)");
                        Toast.makeText(SOSActivity.this, "Connected with " + endpointId, Toast.LENGTH_SHORT).show();

                        // Send first SOS message
                        String msg = messageInput.getText().toString().trim();
                        sendPayload(endpointId, "SOS: " + msg);
                    } else {
                        Toast.makeText(SOSActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    connectedDevices.remove(endpointId);
                    statusText.setText("User left. Connected: " + connectedDevices.size());
                }
            };

    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            if (payload.asBytes() != null) {
                String msg = new String(payload.asBytes());
                Toast.makeText(SOSActivity.this, "Msg from " + endpointId + ": " + msg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) { }
    };

    private void sendPayload(String endpointId, String message) {
        connectionsClient.sendPayload(endpointId, Payload.fromBytes(message.getBytes()));
    }

    private void sendMessageToAll() {
        if (connectedDevices.isEmpty()) {
            Toast.makeText(this, "No users connected yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        String msg = messageInput.getText().toString().trim();
        if (msg.isEmpty()) {
            Toast.makeText(this, "Enter a message!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String endpointId : connectedDevices.keySet()) {
            sendPayload(endpointId, "Host: " + msg);
        }

        Toast.makeText(this, "Message sent to all connected users!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        connectionsClient.stopAllEndpoints();
        connectionsClient.stopAdvertising();
    }
}
