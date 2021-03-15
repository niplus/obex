package com.fota.android.common.bean.wallet;

import com.guoziwei.fota.util.DateUtils;

import java.io.Serializable;

/**
 * Created by jiang on 2018/08/21.
 * 分页返回的划转记录单项
 */

public class TransferListItemBean implements Serializable {

    //defaut 1 钱包账户 可互换
    private int fromType;
    //default 2 合约账户 可互换
    private int toType;
    private String amount;
    private long gmtCreate;
    private long gmtModified;

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

    public long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getFormatTime() {
        String result = DateUtils.formatDate(gmtCreate, "yyyy-MM-dd HH:mm");
        return result;
    }
}
