package com.fota.android.socket.params;

import java.io.Serializable;

public class SocketEntrustParam implements Serializable {

    int type;
    int id;
    String param;

    public SocketEntrustParam(String param) {
        this.param = param;
    }

    public SocketEntrustParam(int type, int id, String param) {
        this.type = type;
        this.id = id;
        this.param = param;
    }

    public SocketEntrustParam(int type, int id) {
        this.type = type;
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
