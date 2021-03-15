package com.fota.android.moudles.mine.bean;

import java.io.Serializable;

/**
 * 返佣传给h5的用户信息
 */
public class CommissionUserInfoBean implements Serializable {
    private String code;
    private CommissionUserInfoBeanItem data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CommissionUserInfoBeanItem getCommissionUserInfoBeanItem() {
        return data;
    }

    public void setCommissionUserInfoBeanItem(CommissionUserInfoBeanItem data) {
        this.data = data;
    }
}
