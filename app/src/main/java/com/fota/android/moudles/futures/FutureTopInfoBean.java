package com.fota.android.moudles.futures;

import java.io.Serializable;

public class FutureTopInfoBean implements Serializable {

    private String total;
    private String available;
    private String maxAskAmount;
    private String maxBidAmount;
    private String securityBorder;
    private String floatProfit;
    private String accountMargin;
    private String effectiveLever;
    private String fundFeeRate;

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTotal() {
        return total;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public void setMaxAskAmount(String maxAskAmount) {
        this.maxAskAmount = maxAskAmount;
    }

    public String getMaxAskAmount() {
        return maxAskAmount;
    }

    public void setMaxBidAmount(String maxBidAmount) {
        this.maxBidAmount = maxBidAmount;
    }

    public String getMaxBidAmount() {
        return maxBidAmount;
    }

    public void setSecurityBorder(String securityBorder) {
        this.securityBorder = securityBorder;
    }

    public String getSecurityBorder() {
        return securityBorder;
    }

    public void setFloatProfit(String floatProfit) {
        this.floatProfit = floatProfit;
    }

    public String getFloatProfit() {
        return floatProfit;
    }

    public void setAccountMargin(String accountMargin) {
        this.accountMargin = accountMargin;
    }

    public String getAccountMargin() {
        return accountMargin;
    }

    public void setEffectiveLever(String effectiveLever) {
        this.effectiveLever = effectiveLever;
    }

    public String getEffectiveLever() {
        return effectiveLever;
    }

    public String getFundFeeRate() {
        return fundFeeRate;
    }

    public void setFundFeeRate(String fundFeeRate) {
        this.fundFeeRate = fundFeeRate;
    }
}
