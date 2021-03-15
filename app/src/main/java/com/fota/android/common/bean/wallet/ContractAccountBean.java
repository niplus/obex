package com.fota.android.common.bean.wallet;


import java.io.Serializable;

/**
 * Created by jiang on 2018/8/21.
 */

public class ContractAccountBean implements Serializable {
    //账号权益
    private String total;
    //未平仓盈亏
    private String floatProfit;
    //可用保证金
    private String available;
    //已用保证金
    private String margin;
    //委托冻结
    private String lockedAmount;
    //估值
    private String totalValuation;


    public void setLockedAmount(String lockedAmount) {
        this.lockedAmount = lockedAmount;
    }

    public String getLockedAmount() {
        return lockedAmount;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getFloatProfit() {
        return floatProfit;
    }

    public void setFloatProfit(String floatProfit) {
        this.floatProfit = floatProfit;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public String getTotalValuation() {
        return totalValuation;
    }

    public void setTotalValuation(String totalValuation) {
        this.totalValuation = totalValuation;
    }
}
