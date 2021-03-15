package com.fota.android.common.bean.home;

import java.io.Serializable;

public class DealBean implements Serializable {
    private String amount;
    //1 2 1买家主动，买单 2卖家主动 卖单
    private int matchType;
    //deal time
    private long ts;
    private String price;
    private int id;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getMatchType() {
        return matchType;
    }

    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return com.guoziwei.fota.util.DateUtils.formatDate(ts, "HH:mm:ss");
    }
}
