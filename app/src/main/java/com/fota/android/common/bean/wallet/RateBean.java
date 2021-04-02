package com.fota.android.common.bean.wallet;

public class RateBean {
    private String fixedFeeAmount;
    private String withdrawFeeRate;
    private String minWithdrawFeeStandard;

    public String getFixedFeeAmount() {
        return fixedFeeAmount;
    }

    public void setFixedFeeAmount(String fixedFeeAmount) {
        this.fixedFeeAmount = fixedFeeAmount;
    }

    public String getWithdrawFeeRate() {
        return withdrawFeeRate;
    }

    public void setWithdrawFeeRate(String withdrawFeeRate) {
        this.withdrawFeeRate = withdrawFeeRate;
    }

    public String getMinWithdrawFeeStandard() {
        return minWithdrawFeeStandard;
    }

    public void setMinWithdrawFeeStandard(String minWithdrawFeeStandard) {
        this.minWithdrawFeeStandard = minWithdrawFeeStandard;
    }

    @Override
    public String toString() {
        return "RateBean{" +
                "fixedFeeAmount='" + fixedFeeAmount + '\'' +
                ", withdrawFeeRate='" + withdrawFeeRate + '\'' +
                ", minWithdrawFeeStandard='" + minWithdrawFeeStandard + '\'' +
                '}';
    }
}
