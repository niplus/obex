package com.fota.android.socket.params;

import java.io.Serializable;

/**
 * holding 相关
 * holding price
 * holding description
 */
public class SocketFutureParam implements Serializable {
    //1 2 3 周月季
    private int contractType = 2;
    //btc
    private String assetName;

    public SocketFutureParam(int contractType, String assetName) {
        this.contractType = contractType;
        this.assetName = assetName;
    }

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
}
