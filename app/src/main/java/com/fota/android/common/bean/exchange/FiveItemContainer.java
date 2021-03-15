package com.fota.android.common.bean.exchange;

import com.fota.android.common.bean.home.EntrustBean;

import java.util.List;

public class FiveItemContainer {
    int id;
    String pricision;
    List<EntrustBean> asks;
    List<EntrustBean> bids;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPricision() {
        return pricision;
    }

    public void setPricision(String pricision) {
        this.pricision = pricision;
    }

    public List<EntrustBean> getAsks() {
        return asks;
    }

    public void setAsks(List<EntrustBean> asks) {
        this.asks = asks;
    }

    public List<EntrustBean> getBids() {
        return bids;
    }

    public void setBids(List<EntrustBean> bids) {
        this.bids = bids;
    }
}
