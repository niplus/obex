package com.fota.android.common.bean.wallet;

import java.io.Serializable;

/**
 * Created by fjw on 2018/4/24.
 */

public class RechargeEntity implements Serializable {

    private String address;
    private int confirmNum;
    private int withdrawConfirmNum;
    private String depositMinSize;
    private int status;
    private String tag;

    public String getTag() {
        if (tag == null) {
            return "";
        }
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setConfirmNum(int confirmNum) {
        this.confirmNum = confirmNum;
    }

    public int getConfirmNum() {
        return confirmNum;
    }

    public void setWithdrawConfirmNum(int withdrawConfirmNum) {
        this.withdrawConfirmNum = withdrawConfirmNum;
    }

    public int getWithdrawConfirmNum() {
        return withdrawConfirmNum;
    }

    public void setDepositMinSize(String depositMinSize) {
        this.depositMinSize = depositMinSize;
    }

    public String getDepositMinSize() {
        return depositMinSize;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
