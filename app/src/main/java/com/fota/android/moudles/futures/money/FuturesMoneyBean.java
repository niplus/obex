package com.fota.android.moudles.futures.money;

import android.content.Context;

import com.fota.android.R;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.TimeUtils;

import java.io.Serializable;

public class FuturesMoneyBean implements Serializable {
    private String id;
    private long gmtCreate;
    private long gmtModified;
    private String userId;
    private String contractId;
    private String contractName;
    private int positionType;
    private String amount;
    private String averagePrice;
    private int lever;
    private String applies;
    private String margin;
    private String contractSize;

    private String openPositionPrice;
    private String currentPrice;
    private String earningRate;
    private int quantile;

    private boolean isCanceled;

    private int closePrecision;
    private int closePricePrecision;
    private String avaQty;
    private String positionQty;

    private String lastMatchPrice;

    public String getLastMatchPrice() {
        return lastMatchPrice;
    }

    public void setLastMatchPrice(String lastMatchPrice) {
        this.lastMatchPrice = lastMatchPrice;
    }

    public String getAvaQty() {
        return avaQty;
    }

    public void setAvaQty(String avaQty) {
        this.avaQty = avaQty;
    }

    public String getPositionQty() {
        return positionQty;
    }

    public void setPositionQty(String positionQty) {
        this.positionQty = positionQty;
    }

    public int getClosePrecision() {
        return closePrecision;
    }

    public void setClosePrecision(int closePrecision) {
        this.closePrecision = closePrecision;
    }

    public int getClosePricePrecision() {
        return closePricePrecision;
    }

    public void setClosePricePrecision(int closePricePrecision) {
        this.closePricePrecision = closePricePrecision;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setGmtCreate(long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtModified(long gmtModified) {
        this.gmtModified = gmtModified;
    }

    public long getGmtModified() {
        return gmtModified;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractName() {
        return contractName;
    }

    public void setPositionType(int positionType) {
        this.positionType = positionType;
    }

    public int getPositionType() {
        return positionType;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAveragePrice(String averagePrice) {
        this.averagePrice = averagePrice;
    }

    public String getAveragePrice() {
        return averagePrice;
    }

    public void setLever(int lever) {
        this.lever = lever;
    }

    public int getLever() {
        return lever;
    }

    public void setApplies(String applies) {
        this.applies = applies;
    }

    public String getApplies() {
        return applies;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public String getMargin() {
        return margin;
    }

    public void setContractSize(String contractSize) {
        this.contractSize = contractSize;
    }

    public String getContractSize() {
        return contractSize;
    }

    /**
     * 交易类型：1-卖，2-买
     * 1-多，2-空
     *
     * @return
     */
    public boolean isBuy() {
        return 2 == positionType;
    }

    public String getFormatBuyOrSell(Context context) {
        if (isBuy()) {
            return CommonUtils.getResouceString(context, R.string.tradehis_duo_short);
        } else {
            return CommonUtils.getResouceString(context, R.string.tradehis_kong_short);
        }
    }


    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public String getFormatTime() {
        return TimeUtils.getDateToString(getGmtCreate());
    }

    public String getOpenPositionPrice() {
        return openPositionPrice;
    }

    public void setOpenPositionPrice(String openPositionPrice) {
        this.openPositionPrice = openPositionPrice;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getEarningRate() {
        return earningRate;
    }

    public void setEarningRate(String earningRate) {
        this.earningRate = earningRate;
    }

    public int getQuantile() {
        return quantile;
    }

    public void setQuantile(int quantile) {
        this.quantile = quantile;
    }

    @Override
    public String toString() {
        return "FuturesMoneyBean{" +
                "id='" + id + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", userId='" + userId + '\'' +
                ", contractId='" + contractId + '\'' +
                ", contractName='" + contractName + '\'' +
                ", positionType=" + positionType +
                ", amount='" + amount + '\'' +
                ", averagePrice='" + averagePrice + '\'' +
                ", lever=" + lever +
                ", applies='" + applies + '\'' +
                ", margin='" + margin + '\'' +
                ", contractSize='" + contractSize + '\'' +
                ", openPositionPrice='" + openPositionPrice + '\'' +
                ", currentPrice='" + currentPrice + '\'' +
                ", earningRate='" + earningRate + '\'' +
                ", quantile=" + quantile +
                ", isCanceled=" + isCanceled +
                '}';
    }
}
