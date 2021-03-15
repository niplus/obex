package com.fota.android.socket.params;

import java.io.Serializable;

public class SocketBaseParam implements Serializable {

    int type;
    int id;

    public SocketBaseParam(int type, int id) {
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

}
