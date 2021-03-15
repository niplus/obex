package com.fota.android.common.bean.wallet;


import java.io.Serializable;
import java.util.List;

/**
 * Created by fjw on 2018/4/20.
 */
public class AddressListBean implements Serializable {

    private int total;
    private int msgType;
    private List<AddressEntity> addressList;
    private boolean googleVerification;
    private boolean phoneVerification;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public List<AddressEntity> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<AddressEntity> addressList) {
        this.addressList = addressList;
    }

    public boolean isGoogleVerification() {
        return googleVerification;
    }

    public void setGoogleVerification(boolean googleVerification) {
        this.googleVerification = googleVerification;
    }

    public boolean isPhoneVerification() {
        return phoneVerification;
    }

    public void setPhoneVerification(boolean phoneVerification) {
        this.phoneVerification = phoneVerification;
    }
}
