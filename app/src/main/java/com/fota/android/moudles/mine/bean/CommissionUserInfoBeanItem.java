package com.fota.android.moudles.mine.bean;

import android.text.TextUtils;

import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;

import java.io.Serializable;

public class CommissionUserInfoBeanItem implements Serializable {
    private String token;
    private int registerType;
    private String nickName;
    private String phoneCountryCode;
    private String udid;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getRegisterType() {
        return registerType;
    }

    public void setRegisterType(int registerType) {
        this.registerType = registerType;
    }

    public String getNickName() {
        String account = UserLoginUtil.getLoginedAccount();
        if (!TextUtils.isEmpty(account) && account.contains("@")) {
            return hideEmaile(account);
        } else if (!TextUtils.isEmpty(account)) {
            return hidePhone(account);
        } else {
            return "";
        }
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    private String hidePhone(String str) {
        return StringFormatUtils.getHidePhone(str);
    }

    private String hideEmaile(String str) {
        return StringFormatUtils.getHideEmail(str);
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }
}
