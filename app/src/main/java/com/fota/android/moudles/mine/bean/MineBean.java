package com.fota.android.moudles.mine.bean;

import java.io.Serializable;

/**
 * 我的首页bean
 */
//"data": {
//        "userSecurity": {
//        "id": 1,
//        "userId": 1,
//        "isEmailAuth": "",
//        "isPhoneAuth": "",
//        "isFutureTradeAuth": "",
//        "isFutureTradeExpert": "",
//        "isOptionTradeAuth": "",
//        "isOptionTradeExpert": "",
//        "googleKey": "",
//        "isGoogleAuth": "",
//        "isGoogleLogin": "",
//        "identityCard": 1,
//        "identityCardType": 0,
//        "cardCheckStatus": 2,
//        "cardPictureFrontUrl": "",
//        "cardPictureBackUrl": "",
//        "country": "",
//        "firstName": "",
//        "lastName": "",
//        "kycLevel": 0,
//        "gmtSecurityModified": "",
//        "gmtCreate": "",
//        "gmtModified": "",
//        "desc": "",
//        "isValid": "",
//        "isFundPwdSet": "true"
//        },
//        "capitalAmount": 1,
//        "contractAmount": 1,
//        "totalAmount": 1
//        }
public class MineBean implements Serializable {
    private String capitalAmount;
    private String contractAmount;
    private String totalAmount;
    private String totalValuation;
    private UserSecurity userSecurity;
    private int messageNum;
    private int activityNum;

    public static class UserSecurity implements Serializable {
        private boolean isEmailAuth;
        private boolean isPhoneAuth;
        private boolean isGoogleAuth;
        private boolean isGoogleLogin;
        private String googleKey;
        private String userName;
        private boolean isFundPwdSet; //资金密码是否设置
        private int cardCheckStatus;//证件审核状态

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getCardCheckStatus() {
            return cardCheckStatus;
        }

        public void setCardCheckStatus(int cardCheckStatus) {
            this.cardCheckStatus = cardCheckStatus;
        }

        public boolean isEmailAuth() {
            return isEmailAuth;
        }

        public void setEmailAuth(boolean emailAuth) {
            isEmailAuth = emailAuth;
        }

        public boolean isPhoneAuth() {
            return isPhoneAuth;
        }

        public void setPhoneAuth(boolean phoneAuth) {
            isPhoneAuth = phoneAuth;
        }

        public boolean isGoogleAuth() {
            return isGoogleAuth;
        }

        public void setGoogleAuth(boolean googleAuth) {
            isGoogleAuth = googleAuth;
        }

        public boolean isGoogleLogin() {
            return isGoogleLogin;
        }

        public void setGoogleLogin(boolean googleLogin) {
            isGoogleLogin = googleLogin;
        }

        public String getGoogleKey() {
            return googleKey;
        }

        public void setGoogleKey(String googleKey) {
            this.googleKey = googleKey;
        }

        public boolean isFundPwdSet() {
            return isFundPwdSet;
        }

        public void setFundPwdSet(boolean fundPwdSet) {
            isFundPwdSet = fundPwdSet;
        }

        @Override
        public String toString() {
            return "UserSecurity{" +
                    "isEmailAuth=" + isEmailAuth +
                    ", isPhoneAuth=" + isPhoneAuth +
                    ", isGoogleAuth=" + isGoogleAuth +
                    ", isGoogleLogin=" + isGoogleLogin +
                    ", googleKey='" + googleKey + '\'' +
                    ", isFundPwdSet=" + isFundPwdSet +
                    ", cardCheckStatus=" + cardCheckStatus +
                    '}';
        }
    }

    public String getCapitalAmount() {
        return capitalAmount;
    }

    public void setCapitalAmount(String capitalAmount) {
        this.capitalAmount = capitalAmount;
    }

    public String getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(String contractAmount) {
        this.contractAmount = contractAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public UserSecurity getUserSecurity() {
        return userSecurity;
    }

    public void setUserSecurity(UserSecurity userSecurity) {
        this.userSecurity = userSecurity;
    }

    public int getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(int messageNum) {
        this.messageNum = messageNum;
    }

    public int getActivityNum() {
        return activityNum;
    }

    public void setActivityNum(int activityNum) {
        this.activityNum = activityNum;
    }

    public String getTotalValuation() {
        return totalValuation;
    }

    public void setTotalValuation(String totalValuation) {
        this.totalValuation = totalValuation;
    }

    @Override
    public String toString() {
        return "MineBean{" +
                "capitalAmount='" + capitalAmount + '\'' +
                ", contractAmount='" + contractAmount + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ", totalValuation='" + totalValuation + '\'' +
                ", userSecurity=" + userSecurity +
                ", messageNum=" + messageNum +
                ", activityNum=" + activityNum +
                '}';
    }
}

