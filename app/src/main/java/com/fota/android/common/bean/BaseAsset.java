package com.fota.android.common.bean;

import com.fota.android.core.base.ft.FtKeyValue;

import java.io.Serializable;

public class BaseAsset implements Serializable, FtKeyValue {
    public BaseAsset() {
    }

    public BaseAsset(String assetId, String assetName) {
        this.assetId = assetId;
        this.assetName = assetName;
    }

    String assetId;

    String assetName;



    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getFirstName() {
        if (assetName == null) {
            return "";
        }
        String[] strs = assetName.split("/");
        if (strs == null && strs.length != 1) {
            return "";
        }
        return strs[0];
    }

    public String getSecondName() {
        if (assetName == null) {
            return "";
        }
        String[] strs = assetName.split("/");
        if (strs == null && strs.length != 1) {
            return "";
        }
        return strs[1];
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    @Override
    public String getKey() {
        return getAssetName();
    }

    @Override
    public String getValue() {
        return assetId + "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof BaseAsset) {
            ((BaseAsset) obj).assetId = assetId;
        }
        return super.equals(obj);
    }

}
