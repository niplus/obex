package com.fota.android.common.bean.home;

import java.io.Serializable;

/**
 * 账户概览
 * <p>
 * <p>
 * "yieldRate": "-12.5%",
 * "marginRate": "78.12%"
 */
public class GaiLanBean implements Serializable {
    private String yieldRate;
    private String marginRate;

    public String getYieldRate() {
        return yieldRate;
    }

    public void setYieldRate(String yieldRate) {
        this.yieldRate = yieldRate;
    }

    public String getMarginRate() {
        return marginRate;
    }

    public void setMarginRate(String marginRate) {
        this.marginRate = marginRate;
    }

    @Override
    public String toString() {
        return "GaiLanBean{" +
                "yieldRate='" + yieldRate + '\'' +
                ", marginRate='" + marginRate + '\'' +
                '}';
    }
}
