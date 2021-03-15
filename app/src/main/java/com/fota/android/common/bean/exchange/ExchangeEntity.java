package com.fota.android.common.bean.exchange;

import java.io.Serializable;

public class ExchangeEntity implements Serializable {

    private String assetId;
    private String contractId;
    private int priceType;
    private String totalAmount;
    private String price;
    private String orderDirection;
    private String assetName;
    private String entrustValue;
    private String contractName;
    private String lever;

    public String getLever() {
        return lever;
    }

    public void setLever(String lever) {
        this.lever = lever;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public String getEntrustValue() {
        return entrustValue;
    }

    public void setEntrustValue(String entrustValue) {
        this.entrustValue = entrustValue;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    /**
     * @param isBuy 1-卖，2-买
     */
    public void setIsBuy(boolean isBuy) {
        if (isBuy) {
            orderDirection = "2";
        } else {
            orderDirection = "1";
        }
    }

    /**
     * isBuy 1-卖，2-买
     *
     * @param
     */
    public boolean isBuy() {
        return "2".equals(orderDirection);
    }

}
