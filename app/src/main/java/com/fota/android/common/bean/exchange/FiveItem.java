package com.fota.android.common.bean.exchange;

import java.io.Serializable;

public class FiveItem implements Serializable {

    String price;
    String amount;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
