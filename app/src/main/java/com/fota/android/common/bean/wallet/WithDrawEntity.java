package com.fota.android.common.bean.wallet;

import java.io.Serializable;

/**
 * Created by fjw on 2018/4/19.
 */

public class WithDrawEntity implements Serializable {

    String assetId;
    String assetName;
    String toAddress;
    String amount;
    String fee;
    String smsCode;
    String googleCode;
    String tradeToken;
    private String network;

    public WithDrawEntity() {
    }

    public String getGoogleCode() {
        return googleCode;
    }

    public void setGoogleCode(String googleCode) {
        this.googleCode = googleCode;
    }

    public String getTradeToken() {
        return tradeToken;
    }

    public void setTradeToken(String tradeToken) {
        this.tradeToken = tradeToken;
    }


    public WithDrawEntity(WalletItem model) {
        this.assetId = model.getAssetId() + "";
        this.assetName = model.getAssetName();
        this.network = model.getNetWork();
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

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public void noneInfo() {
        assetId = "";
    }

    public String getNetWork() {
        return network;
    }

    public void setNetWork(String netWork) {
        this.network = netWork;
    }
}
