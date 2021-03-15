package com.fota.android.moudles.mine.bean;

import java.io.Serializable;

/**
 * 谷歌验证信息
 * qrCodeData
 * String
 * BJGJFJHKLHHF7BKJB97NNJB1
 * 谷歌二维码内容
 * secretkey
 * String
 * BJGJFJHKLHHF7BKJB97NNJB
 * 谷歌验证秘钥
 */
public class GoogleBean implements Serializable {
    private String qrCodeData;
    private String secretkey;

    public String getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    @Override
    public String toString() {
        return "GoogleBean{" +
                "qrCodeData='" + qrCodeData + '\'' +
                ", secretkey='" + secretkey + '\'' +
                '}';
    }
}
