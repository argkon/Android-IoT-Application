package com.example.panos.Connectivity;


public class ConnectionMode {
    boolean automatic = false; // false -> start with manual mode for MQTT Connection

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }
}
