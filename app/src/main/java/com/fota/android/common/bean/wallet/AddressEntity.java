package com.fota.android.common.bean.wallet;

import java.io.Serializable;

/**
 * Created by fjw on 2018/4/19.
 */
public class AddressEntity implements Serializable {

    public AddressEntity(String address, String remarks) {
        this.address = address;
        this.remarks = remarks;
    }

    private int id;

    private String address;

    private String assetId;

    private String assetName;

    private String remarks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof AddressEntity) {
            return getId() == ((AddressEntity) obj).getId();
        }
        return super.equals(obj);
    }
}