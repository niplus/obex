package com.fota.android.common.bean.exchange;

import com.fota.android.commonlib.utils.Pub;

import java.io.Serializable;

public class CurrentPriceBean implements Serializable {

    String price;
    String dailyReturn;
    String dailyReturnStatus;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDailyReturn() {
        return dailyReturn;
    }

    public void setDailyReturn(String dailyReturn) {
        this.dailyReturn = dailyReturn;
    }

    public String getDailyReturnStatus() {
        return dailyReturnStatus;
    }

    public void setDailyReturnStatus(String dailyReturnStatus) {
        this.dailyReturnStatus = dailyReturnStatus;
    }

    //四舍五入
    public String getPriceFloor(int length) {
        if(length == -1) {
            return price;
        }
        String price2 = Pub.getPriceStringForLengthRound(Pub.GetDouble(price), length);
        return price2;
    }
}
