package com.appitor.sankethelp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionsClient;

import java.util.List;

public class DiscoveredAdapter extends ArrayAdapter<DiscoveredEndpoint> {
    private final Context context;
    private final List<DiscoveredEndpoint> endpoints;
    private final ConnectionsClient connectionsClient;
    private final ConnectionLifecycleCallback connectionLifecycleCallback;

    public DiscoveredAdapter(Context context, List<DiscoveredEndpoint> endpoints,
                             ConnectionsClient connectionsClient,
                             ConnectionLifecycleCallback connectionLifecycleCallback) {
        super(context, 0, endpoints);
        this.context = context;
        this.endpoints = endpoints;
        this.connectionsClient = connectionsClient;
        this.connectionLifecycleCallback = connectionLifecycleCallback;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_discovered, parent, false);
        }

        DiscoveredEndpoint ep = endpoints.get(position);

        TextView nameText = convertView.findViewById(R.id.deviceName);
        TextView msgText = convertView.findViewById(R.id.deviceMsg);
        TextView locText = convertView.findViewById(R.id.deviceLocation);
        Button mapBtn = convertView.findViewById(R.id.btnViewMap);

        nameText.setText(ep.endpointName);
        msgText.setText(ep.message != null ? ep.message : "No message");
        locText.setText("Lat: " + ep.latitude + ", Lon: " + ep.longitude);

        // 🔗 Request connection on item click
        convertView.setOnClickListener(v -> {
            connectionsClient.requestConnection(
                    Build.MODEL,
                    ep.endpointId,
                    connectionLifecycleCallback
            ).addOnSuccessListener(unused ->
                    Toast.makeText(context, "Request sent to " + ep.endpointName, Toast.LENGTH_SHORT).show()
            ).addOnFailureListener(e ->
                    Toast.makeText(context, "Request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        });

        // 🗺️ View on Map button
        mapBtn.setOnClickListener(v -> {
            String uri = "geo:" + ep.latitude + "," + ep.longitude + "?q=" + ep.latitude + "," + ep.longitude + "(SOS: " + ep.endpointName + ")";
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        });

        return convertView;
    }
}
