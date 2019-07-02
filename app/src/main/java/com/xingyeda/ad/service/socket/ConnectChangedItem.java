package com.xingyeda.ad.service.socket;

public class ConnectChangedItem {
    private boolean connecting = false;

    public ConnectChangedItem(boolean connect){
        this.connecting = connect;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    }

    public boolean isConnecting() {
        return connecting;
    }
}
