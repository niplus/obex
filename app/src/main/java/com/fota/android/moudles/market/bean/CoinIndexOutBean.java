package com.fota.android.moudles.market.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 现货指数
 * 外围市场的指标参数数据
 */
public class CoinIndexOutBean implements Serializable {
    //{"cnName":"Okex","enName":"Okex","iconUrl":"url","price":"4033.29"}
    private String cnName;
    private String enName;
    private String iconUrl;
    private String price;

    public CoinIndexOutBean() {
    }

    public String getCnName() {
        if(TextUtils.isEmpty(cnName)) {
            return "--";
        }
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        if(TextUtils.isEmpty(enName)) {
            return "--";
        }
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
