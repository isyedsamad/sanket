package com.appitor.sankethelp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.*;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private static final int REQ_PERM = 1001;
    private final String SERVICE_ID = "com.appitor.sankethelp";
    private ConnectionsClient connectionsClient;
    private List<DiscoveredEndpoint> endpoints = new ArrayList<>();
    private DiscoveredAdapter adapter;
    private Map<String, DiscoveredEndpoint> discoveredMap = new HashMap<>();
    private ListView discoveredList;
    private TextView statusTv;
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.NEARBY_WIFI_DEVICES
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (!hasAllPermissions()) {
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
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        connectionsClient = Nearby.getConnectionsClient(this);

        discoveredList = findViewById(R.id.discoveredList);
        statusTv = findViewById(R.id.statusTv);

//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, endpoints);
//        discoveredList.setAdapter(adapter);
        adapter = new DiscoveredAdapter(this,
                endpoints,
                connectionsClient,
                connectionLifecycleCallback);
        discoveredList.setAdapter(adapter);

        checkPermissionsAndStartDiscovery();

//        discoveredList.setOnItemClickListener((parent, view, position, id) -> {
//            DiscoveredEndpoint item = endpoints.get(position);
//            DiscoveredEndpoint ep = discoveredMap.get(item);
//            if (ep != null) {
//                statusTv.setText("Status: requesting connection to " + ep.name);
//                connectionsClient.requestConnection(
//                                Build.MODEL,
//                                ep.endpointId,
//                                connectionLifecycleCallback
//                        ).addOnSuccessListener(unused -> toast("Request sent"))
//                        .addOnFailureListener(e -> toast("Request failed: " + e.getMessage()));
//
//                // open chat activity
//                Intent i = new Intent(HomeActivity.this, ChatActivity.class);
//                i.putExtra("endpointId", ep.endpointId);
//                i.putExtra("endpointName", ep.name);
//                i.putExtra("isHost", false);
//                startActivity(i);
//            }
//        });

        findViewById(R.id.sosBtn).setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, SOSActivity.class);
            startActivity(i);
        });

//        findViewById(R.id.sosBtn2).setOnClickListener(v -> {
//            Intent i = new Intent(HomeActivity.this, SOSHelpActivity.class);
//            startActivity(i);
//        });
    }

    private void checkPermissionsAndStartDiscovery() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        ArrayList<String> toRequest = new ArrayList<>();
        for (String p : perms)
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                toRequest.add(p);
        if (!toRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, toRequest.toArray(new String[0]), REQ_PERM);
        } else {
            startDiscovery();
        }
    }

    private boolean hasAllPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (hasAllPermissions()) {
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions required for Nearby Chat!", Toast.LENGTH_LONG).show();
            }
        }
    }

//    private void startDiscovery() {
//        statusTv.setText("Status: discovering...");
//        DiscoveryOptions options = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
//        connectionsClient.startDiscovery(SERVICE_ID, endpointDiscoveryCallback, options)
//                .addOnSuccessListener(unused -> toast("Discovery started"))
//                .addOnFailureListener(e -> toast("Discovery failed: " + e.getMessage()));
//    }

//    private void startDiscovery() {
//        connectionsClient = Nearby.getConnectionsClient(this);
//        DiscoveryOptions discoveryOptions =
//                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
//
//        Toast.makeText(this, "Looking for SOS signals...", Toast.LENGTH_SHORT).show();
//
//        connectionsClient.startDiscovery(
//                SERVICE_ID,
//                endpointDiscoveryCallback,
//                discoveryOptions
//        ).addOnSuccessListener(unused -> {
//            Toast.makeText(this, "Discovery started", Toast.LENGTH_SHORT).show();
//        }).addOnFailureListener(e -> {
//            Toast.makeText(this, "Discovery failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        });
//    }
//
//    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
//        @Override
//        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info) {

    /// /            String name = info.getEndpointName();
    /// /            runOnUiThread(() -> {
    /// /                String display = name + " — " + getPreviewFromInfo(info);
    /// /                if (!discoveredMap.containsKey(display)) {
    /// /                    discoveredMap.put(display, new DiscoveredEndpoint(endpointId, name));
    /// /                    endpoints.add(display);
    /// /                    adapter.notifyDataSetChanged();
    /// /                }
    /// /                statusTv.setText("Status: found " + name);
    /// /            });
//            try {
//                // Parse JSON from advertiser
//                JSONObject json = new JSONObject(info.getEndpointName());
//                String name = json.optString("name", "Unknown");
//                String msg = json.optString("msg", "No message");
//
//                String displayText = name + " — “" + msg + "”";
//
//                discoveredMap.put(displayText, new DiscoveredEndpoint(endpointId, name, msg));
//                if (!endpoints.contains(displayText)) {
//                    endpoints.add(displayText);
//                    adapter.notifyDataSetChanged();
//                }
//            } catch (Exception e) {
//                // if endpointName not JSON (fallback)
//                String fallback = info.getEndpointName();
//                discoveredMap.put(fallback, new DiscoveredEndpoint(endpointId, fallback, ""));
//                endpoints.add(fallback);
//                adapter.notifyDataSetChanged();
//            }
//        }
//
//        @Override
//        public void onEndpointLost(@NonNull String endpointId) {
//            runOnUiThread(() -> {
//                // remove by endpointId
//                String keyToRemove = null;
//                for (Map.Entry<String, DiscoveredEndpoint> e : discoveredMap.entrySet()) {
//                    if (e.getValue().endpointId.equals(endpointId)) {
//                        keyToRemove = e.getKey();
//                        break;
//                    }
//                }
//                if (keyToRemove != null) {
//                    discoveredMap.remove(keyToRemove);
//                    endpoints.remove(keyToRemove);
//                    adapter.notifyDataSetChanged();
//                }
//            });
//        }
//    };
    private void startDiscovery() {
        DiscoveryOptions options = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        connectionsClient.startDiscovery(getPackageName(), discoveryCallback, options)
                .addOnSuccessListener(unused -> statusTv.setText("Discovering SOS signals..."));
//                .addOnFailureListener(e -> statusTv.setText("Discovery failed: " + e.getMessage()));
    }

    private final EndpointDiscoveryCallback discoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info) {
            try {
                JSONObject obj = new JSONObject(info.getEndpointName());

                String name = obj.optString("name", "Unknown");
                String msg = obj.optString("msg", "No message");

                // ✅ parse latitude & longitude (nullable)
                Double lat = obj.has("latitude") && !obj.isNull("latitude") ? obj.getDouble("latitude") : null;
                Double lon = obj.has("longitude") && !obj.isNull("longitude") ? obj.getDouble("longitude") : null;

                // Add endpoint with full data
                endpoints.add(new DiscoveredEndpoint(endpointId, name, msg, lat, lon));

            } catch (Exception e) {
                // fallback if not JSON
                endpoints.add(new DiscoveredEndpoint(endpointId, info.getEndpointName(), "No message", 0, 0));
            }

            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            DiscoveredEndpoint toRemove = null;
            for (DiscoveredEndpoint ep : endpoints) {
                if (ep.endpointId.equals(endpointId)) {
                    toRemove = ep;
                    break;
                }
            }
            if (toRemove != null) {
                endpoints.remove(toRemove);
                runOnUiThread(() -> adapter.notifyDataSetChanged());
                Toast.makeText(HomeActivity.this, toRemove.endpointName + " went offline", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private String getPreviewFromInfo(DiscoveredEndpointInfo info) {
        // Nearby doesn't provide arbitrary metadata in v2 discovery; show endpoint name
        return "active";
    }

    // simple wrapper
    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    // Keep a simple lifecycle callback to handle incoming connections (rare on discovery side)
//    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
//        @Override
//        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
//            // When we call requestConnection, the remote host will see this and can accept.
//            runOnUiThread(() -> {
//                toast("Connection initiated with " + connectionInfo.getEndpointName());
//                // accept automatically
//                Nearby.getConnectionsClient(HomeActivity.this).acceptConnection(endpointId, payloadCallback);
//            });
//        }
//
//        @Override
//        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
//            runOnUiThread(() -> {
//                if (result.getStatus().isSuccess()) {
//                    toast("Connected to " + endpointId);
//                } else {
//                    toast("Connection failed: " + result.getStatus().getStatusMessage());
//                }
//            });
//        }
//
//        @Override
//        public void onDisconnected(@NonNull String endpointId) {
//            runOnUiThread(() -> toast("Disconnected: " + endpointId));
//        }
//    };

    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            connectionsClient.acceptConnection(endpointId, new PayloadCallback() {
                @Override
                public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                }

                @Override
                public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
                }
            });
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
            if (result.getStatus().isSuccess()) {
                Toast.makeText(HomeActivity.this, "Connected to " + endpointId, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {
            Toast.makeText(HomeActivity.this, "Disconnected from " + endpointId, Toast.LENGTH_SHORT).show();
        }
    };

    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            if (payload.asBytes() != null) {
                String json = new String(payload.asBytes(), StandardCharsets.UTF_8);
                runOnUiThread(() -> toast("Msg from " + endpointId + ": " + json));
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
            // nothing for now
        }
    };
}
