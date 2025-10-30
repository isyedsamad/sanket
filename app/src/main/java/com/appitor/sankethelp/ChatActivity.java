package com.appitor.sankethelp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private ConnectionsClient connectionsClient;
    private String endpointId;
    private String endpointName;
    private boolean isHost;
    private TextView chatStatus;
    private ListView messageListView;
    private EditText messageInput;
    private Button sendBtn;
    private ArrayList<String> messages = new ArrayList<>();
    private ArrayAdapter<String> messageAdapter;

    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String fromEndpointId, @NonNull Payload payload) {
            if (payload.asBytes() != null) {
                String json = new String(payload.asBytes(), StandardCharsets.UTF_8);
                addMessage("[" + fromEndpointId + "] " + json);
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) { }
    };

    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String id, @NonNull ConnectionInfo connectionInfo) {
            runOnUiThread(() -> {
                // if we initiated a request and are now here, accept
                addMessage("Connection initiated: " + connectionInfo.getEndpointName());
                Nearby.getConnectionsClient(ChatActivity.this).acceptConnection(id, payloadCallback);
            });
        }

        @Override
        public void onConnectionResult(@NonNull String id, @NonNull ConnectionResolution result) {
            runOnUiThread(() -> {
                if (result.getStatus().isSuccess()) {
                    addMessage("Connected with " + id);
                    chatStatus.setText("Connected to " + id);
                } else {
                    addMessage("Connection failed: " + result.getStatus().getStatusMessage());
                    chatStatus.setText("Connection failed");
                }
            });
        }

        @Override
        public void onDisconnected(@NonNull String id) {
            runOnUiThread(() -> {
                addMessage("Disconnected: " + id);
                chatStatus.setText("Disconnected");
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        connectionsClient = Nearby.getConnectionsClient(this);

        endpointId = getIntent().getStringExtra("endpointId");
        endpointName = getIntent().getStringExtra("endpointName");
        isHost = getIntent().getBooleanExtra("isHost", false);

        chatStatus = findViewById(R.id.chatStatus);
        messageListView = findViewById(R.id.messageList);
        messageInput = findViewById(R.id.messageInput);
        sendBtn = findViewById(R.id.sendBtn);

        messageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        messageListView.setAdapter(messageAdapter);

        chatStatus.setText("Connecting to " + endpointName);

        // If we are client (discovered and requested), we already requested connection from MainActivity.
        // If we are host side, ChatActivity might be opened after host accepted; either way, just register callbacks.
        // Register payload callback by requesting a connection callback (Nearby will route to our central client)
        connectionsClient.stopAllEndpoints(); // ensure clean? (optional) — comment out if undesired
        // We don't want to stop others in real app; this is to keep minimal and avoid stale state.
        // Instead just ensure we accept incoming if any. We'll rely on existing lifecycle callbacks.

        // send button
        sendBtn.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (text.isEmpty()) return;
            // craft JSON minimal
            String json = "{ \"from\":\"" + android.os.Build.MODEL + "\", \"msg\":\"" + text + "\" }";
            byte[] payload = json.getBytes(StandardCharsets.UTF_8);
            // send to endpointId
            connectionsClient.sendPayload(endpointId, Payload.fromBytes(payload))
                    .addOnSuccessListener(unused -> {
                        addMessage("[Me] " + text);
                        messageInput.setText("");
                    }).addOnFailureListener(e -> addMessage("Send failed: " + e.getMessage()));
        });

        // Attach a general connection lifecycle callback so we receive results. In many cases the app-wide ConnectionsClient will call callbacks created during requestConnection/startAdvertising.
        // For simplicity, re-registering a lifecycle callback via a dummy request isn't necessary; Nearby keeps lifecycle callbacks provided in original calls.
        // So here we just set a status and rely on existing callbacks to update.
    }

    private void addMessage(String s) {
        runOnUiThread(() -> {
            messages.add(s);
            messageAdapter.notifyDataSetChanged();
            messageListView.smoothScrollToPosition(messages.size()-1);
        });
    }
}
