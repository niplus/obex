package com.fota.android.moudles.market.bean;

import java.io.Serializable;

/**
 * holding 相关
 * holding price
 * holding description
 */
public class HoldingEntity implements Serializable {
    private double holdingPrice;
    private String holdingDescription;
    //小数位的附加信息也放在这里吧
    private int decimal = 2;
    //合约到期日期
    private String futureLimtDays;
    //交割中，等状态 ; 2:正常 3:交割中
    private int status;
    private boolean isStatusChange;

    public int getStatus() {
        return status;
    }

    public boolean isStatusChange() {
        return isStatusChange;
    }

    public void setStatusChange(boolean statusChange) {
        isStatusChange = statusChange;
    }

    public void setStatus(int status) {
        if(status == this.status) {
            return;
        }
        if(status == 2 && this.status == 3) {
            isStatusChange = true;
        }
        this.status = status;
    }

    public String getFutureLimtDays() {
        return futureLimtDays;
    }

    public void setFutureLimtDays(String futureLimtDays) {
        this.futureLimtDays = futureLimtDays;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //id composite -- deprecated
    // -- 另做他用 现在表示 各个类型可以变化的id （type - id中的id）
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //id 已经废弃
    //-- 另做他用 现在表示 各个类型可以变化的id （type - id中的id）
    //1-指数 2-行情合约 3-USDT
    private int type;
    protected String name;
    //合约、指数、USDK对应的唯一id
    private int id;

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public HoldingEntity(float holdingPrice, String holdingDescription) {
        this.holdingPrice = holdingPrice;
        this.holdingDescription = holdingDescription;
    }

    public HoldingEntity(float holdingPrice, String holdingDescription, int decimal) {
        this.holdingPrice = holdingPrice;
        this.holdingDescription = holdingDescription;
        this.decimal = decimal;
    }

    public double getHoldingPrice() {
        return holdingPrice;
    }

    public void setHoldingPrice(double holdingPrice) {
        this.holdingPrice = holdingPrice;
    }

    public String getHoldingDescription() {
        return holdingDescription;
    }

    public void setHoldingDescription(String holdingDescription) {
        this.holdingDescription = holdingDescription;
    }

    //1-天 2-小时 3-分钟 4-秒
    private int deliveryType;

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }
}
