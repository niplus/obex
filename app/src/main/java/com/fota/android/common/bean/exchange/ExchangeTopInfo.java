package com.fota.android.common.bean.exchange;

import java.io.Serializable;

public class ExchangeTopInfo implements Serializable {

    private String amount;
    private int direction;
    private String price;
    private String symbol;

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSymbolWithBtc() {
        return symbol + "/BTC";
    }


    /**
     * isBuy 1-卖，2-买
     *
     * @param
     */
    public boolean isBuy() {
        return 2 == direction;
    }
}
