package com.appitor.sankethelp;

public class DiscoveredEndpoint {
    public String endpointId;
    public String endpointName;
    public String message;
    public double latitude;
    public double longitude;

    public DiscoveredEndpoint(String endpointId, String endpointName, String message, double latitude, double longitude) {
        this.endpointId = endpointId;
        this.endpointName = endpointName;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}


