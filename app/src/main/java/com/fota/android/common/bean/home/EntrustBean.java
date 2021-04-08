package com.fota.android.common.bean.home;


import androidx.annotation.NonNull;

import com.fota.android.commonlib.utils.Pub;

import java.io.Serializable;

public class EntrustBean implements Serializable, Comparable<EntrustBean> {

    private String price;
    private String amount;
    private String price2;
    private String price3;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    //calculte need
    public double getDoulePrice() {
        return Pub.GetDouble(price);
    }
    //calculte need
    public double getDoubleAmount() {
        return Pub.GetDouble(amount);
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceCut(int length) {
        price2 = Pub.getPriceStringForLength(getDoulePrice(), length, true);
        return price2;
    }

    public String getPriceCarry(int length) {
        price3 = Pub.getPriceStringForLength(getDoulePrice(), length, false);
        return price3;
    }

    @Override
    public int compareTo(@NonNull EntrustBean o) {
        int result = 0;
        if(o == null) {
            return result;
        }
        if (o.getDoulePrice() < getDoulePrice()) {
            result = -1;
        } else if (o.getDoulePrice() > getDoulePrice()) {
            result = 1;
        }
        return result;
    }

    @Override
    public String toString() {
        return "EntrustBean{" +
                "price='" + price + '\'' +
                ", amount='" + amount + '\'' +
                ", price2='" + price2 + '\'' +
                ", price3='" + price3 + '\'' +
                '}';
    }
}
