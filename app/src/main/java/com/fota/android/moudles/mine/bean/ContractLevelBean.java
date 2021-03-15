package com.fota.android.moudles.mine.bean;

import java.io.Serializable;

//    "assetId": 2,
//            "assetName": "BTC",
//            "lever": 10
public class ContractLevelBean implements Serializable {
    private int assetId;
    private String assetName;
    private int lever;

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public int getLever() {
        return lever;
    }

    public void setLever(int lever) {
        this.lever = lever;
    }

    @Override
    public String toString() {
        return "ContractLevelBean{" +
                "assetId=" + assetId +
                ", assetName='" + assetName + '\'' +
                ", lever=" + lever +
                '}';
    }
}
