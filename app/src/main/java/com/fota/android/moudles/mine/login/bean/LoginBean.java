package com.fota.android.moudles.mine.login.bean;


import java.io.Serializable;

//{
//        "code": 0,
//        "msg": "",
//        "data": {
//        "id": 205,
//        "email": "tao.huang@emc.top",
//        "phone": "18858110927",
//        "phoneCountryCode": "86",
//        "phoneCountryKey": "CN",
//        "registerType": 1,
//        "password": "UH6iuWJLViIQ0GE7AAQ8ogYyxjjvZZHycQSxAuP2nh0",
//        "nickName": "",
//        "loginFailTimes": 0,
//        "passwordUnlockTime": "2018-07-13T08:16:23.000+0000",
//        "isFrozen": false,
//        "desc": "",
//        "gmtCreate": 2018,
//        "gmtModified": 2018,
//        "isValid": false,
//        "token": "12324242352"
//        }
//        }
public class LoginBean implements Serializable {
    private long id;
    private String email;
    private String phone;
    private String phoneCountryCode;
    private String phoneCountryKey;
    private int registerType;
    private String nickName;
    private int loginFailTimes;
    private String passwordUnlockTime;
    private boolean isFrozen;
    private String desc;
    private boolean isValid;
    private String token;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public String getPhoneCountryKey() {
        return phoneCountryKey;
    }

    public void setPhoneCountryKey(String phoneCountryKey) {
        this.phoneCountryKey = phoneCountryKey;
    }

    public int getRegisterType() {
        return registerType;
    }

    public void setRegisterType(int registerType) {
        this.registerType = registerType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getLoginFailTimes() {
        return loginFailTimes;
    }

    public void setLoginFailTimes(int loginFailTimes) {
        this.loginFailTimes = loginFailTimes;
    }

    public String getPasswordUnlockTime() {
        return passwordUnlockTime;
    }

    public void setPasswordUnlockTime(String passwordUnlockTime) {
        this.passwordUnlockTime = passwordUnlockTime;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
