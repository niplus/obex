package com.fota.android.widget.btbwidget;

/**
 * Created by xhu_ww on 2018/6/1.
 * description:
 */
public class IncomeBean {
    public IncomeBean(String tradeDate, double value) {
        this.tradeDate = tradeDate;
        this.value = value;
    }

    /**
     * tradeDate : 20180502
     * value : 0.03676598
     */


    private String tradeDate;
    private double value;

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
