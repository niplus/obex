package com.fota.android.socket.params;

import java.io.Serializable;

public class SocketMarketParam implements Serializable {

    int type;
    int id;
    String resolution;

    public int getContractType() {
        return contractType;
    }

    public void setContractType(int contractType) {
        this.contractType = contractType;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    private int contractType;
    private String assetName;

    public SocketMarketParam(int id, int type, String resolution) {
        this.type = type;
        this.id = id;
        this.resolution = resolution;
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

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
