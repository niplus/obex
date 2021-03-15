package com.fota.android.moudles.mine.bean;

import com.fota.android.commonlib.base.AppConfigs;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

//{
//        isOriginal: Bool, // 是否是国外用户
//        registerAccount： "157****656", //用户信息
//        rebateRatioSum: 0.5, //返佣奖励
//        feeDiscount: 0.9, //折扣比例
//        url: '', //h5注册页地址
//        inviteCode: '123456' // 邀请码
//        }
//邀请弹窗bean
public class CommissionBean implements Serializable {

    private boolean isOriginal; // 是否是国外用户
    private String registerAccount; //用户信息
    private String rebateRatioSum; //返佣奖励
    private String feeDiscount; //折扣比例
    private String url; //h5注册页地址
    private String inviteCode; // 邀请码

    public boolean getOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }

    public String getRegisterAccount() {
        return registerAccount;
    }

    public void setRegisterAccount(String registerAccount) {
        this.registerAccount = registerAccount;
    }

    public String getRebateRatioSum() {
//        if (TextUtils.isEmpty(rebateRatioSum))
//            return "";
        double rebateDouble = 0;
        try {
            rebateDouble = Double.parseDouble(rebateRatioSum);
        } catch (Exception e) {

        }

        int rebateRatioSumInt = getInt(rebateDouble * 100);
        return rebateRatioSumInt + "%";
    }

    public String getFeeDiscount() {
//        if (TextUtils.isEmpty(feeDiscount))
//            return "";
        double feeDiscountDouble = 0;
        try {
            feeDiscountDouble = Double.parseDouble(feeDiscount);
        } catch (Exception e) {

        }
        if (AppConfigs.getLanguegeInt() == 1) {
            feeDiscountDouble = 1 - feeDiscountDouble;
        }
        return fun2(feeDiscountDouble * 10) + "";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public static int getInt(double number) {
        BigDecimal bd = new BigDecimal(number).setScale(0, BigDecimal.ROUND_HALF_UP);
        return Integer.parseInt(bd.toString());
    }

    /**
     * 判断double是否是整数
     *
     * @param obj
     * @return
     */
    public static boolean isIntegerForDouble(double obj) {
        double eps = 1e-10;  // 精度范围
        return obj - Math.floor(obj) < eps;
    }

    public String fun2(double amount) {
        if (isIntegerForDouble(amount)) {
            DecimalFormat df = new DecimalFormat("#");
            return df.format(amount);
        } else {
            DecimalFormat df = new DecimalFormat("#.0");
            return df.format(amount);
        }

    }
}
