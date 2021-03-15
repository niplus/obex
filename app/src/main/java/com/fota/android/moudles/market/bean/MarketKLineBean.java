package com.fota.android.moudles.market.bean;

/**
 * market k
 * 数据
 * 服务返回的json bean
 */
public class MarketKLineBean extends MarketLineBean {

    public MarketKLineBean() {
        super();
    }

    private int resolution;

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

}
