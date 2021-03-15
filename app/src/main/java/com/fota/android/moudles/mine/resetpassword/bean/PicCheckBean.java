package com.fota.android.moudles.mine.resetpassword.bean;

import java.io.Serializable;

public class PicCheckBean implements Serializable {
    private boolean isGoogleAuth;

    public boolean isGoogleAuth() {
        return isGoogleAuth;
    }

    public void setGoogleAuth(boolean googleAuth) {
        isGoogleAuth = googleAuth;
    }

    @Override
    public String toString() {
        return "PicCheckBean{" +
                "isGoogleAuth=" + isGoogleAuth +
                '}';
    }
}
