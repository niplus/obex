package com.fota.android.socket.params;

import java.io.Serializable;

public class SocketCardParam implements Serializable {

    int type;
    String resolution;

    public SocketCardParam(int type, String resolution) {
        this.type = type;
        this.resolution = resolution;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
