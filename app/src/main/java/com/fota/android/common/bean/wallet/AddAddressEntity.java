package com.fota.android.common.bean.wallet;

import com.fota.android.core.base.BtbMap;

import java.io.Serializable;

/**
 * Created by fjw on 2018/4/21.
 */
public class AddAddressEntity implements Serializable {

    private String title;
    private String currencyId;
    private String address;
    private String tradePwd;
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTradePwd() {
        return tradePwd;
    }

    public void setTradePwd(String tradePwd) {
        this.tradePwd = tradePwd;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public BtbMap toMap() {
        BtbMap map = new BtbMap();
        map.put("title", title);
        map.put("currencyId", currencyId);
        map.put("address", address);
        map.put("tradePwd", tradePwd);
        map.put("tag", tag);
        return map;
    }
}
