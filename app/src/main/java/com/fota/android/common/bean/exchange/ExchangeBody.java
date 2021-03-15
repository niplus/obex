package com.fota.android.common.bean.exchange;

import java.io.Serializable;

public class ExchangeBody implements Serializable {

    private ExchangeEntity obj;
    private String tradeToken = "";

    public String getTradeToken() {
        return tradeToken;
    }

    public void setTradeToken(String tradeToken) {
        this.tradeToken = tradeToken;
    }

    public ExchangeEntity getObj() {
        return obj;
    }

    public void setObj(ExchangeEntity obj) {
        this.obj = obj;
    }
}
