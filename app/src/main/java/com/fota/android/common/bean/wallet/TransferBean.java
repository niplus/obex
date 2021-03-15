package com.fota.android.common.bean.wallet;

import java.io.Serializable;

/**
 * Created by jiang on 2018/08/21.
 * 划转时需要 提交POST的bean
 */

public class TransferBean implements Serializable {

    //defaut 1 钱包账户 可互换
    private int fromType;
    //default 2 合约账户 可互换
    private int toType;
    private String amount;
    private String tradeToken;

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public int getToType() {
        return toType;
    }

    public void setToType(int toType) {
        this.toType = toType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTradeToken() {
        return tradeToken;
    }

    public void setTradeToken(String tradeToken) {
        this.tradeToken = tradeToken;
    }
}
