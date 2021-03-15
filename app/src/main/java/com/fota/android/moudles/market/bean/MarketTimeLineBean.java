package com.fota.android.moudles.market.bean;

/**
 * market timeline 分时图数据
 * 服务返回的json bean
 */
public class MarketTimeLineBean extends MarketLineBean {

    public MarketTimeLineBean(String futureName) {
        super();
        this.name = futureName;
    }
    private String deliveryDate;
    //交割中 交割后等
    private int status;
    //1-天 2-小时 3-分钟 4-秒
    private int deliveryType;

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

}
