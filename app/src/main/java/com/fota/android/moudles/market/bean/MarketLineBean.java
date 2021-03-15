package com.fota.android.moudles.market.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * market 基类
 * 下有k t bean
 * 数据
 * 服务返回的json bean
 */
public class MarketLineBean implements Serializable {

    public MarketLineBean() {
        this.line = new ArrayList<>();
    }

    private List<ChartLineEntity> line;

    //1-指数 2-行情合约 3-USDT
    private int type;
    protected String name;
    //合约、指数、USDK对应的唯一id
    private int id;
    //小数位
    private int decimal;

    //个人持仓均价
    private String avgPrice;
    //个人持仓多空几手
    private String positionInfo;
    //个人持仓多空 0-0手；1-空；2-多
    private int positionType;

    //assetName contractType只有合约才有 type == 2

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public int getContractType() {
        return contractType;
    }

    public void setContractType(int contractType) {
        this.contractType = contractType;
    }

    //btc - eth
    private String assetName;
    //1 2 3
    private int contractType;

    public int getPositionType() {
        return positionType;
    }

    public void setPositionType(int positionType) {
        this.positionType = positionType;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ChartLineEntity> getLine() {
        return line;
    }

    public void setLine(List<ChartLineEntity> line) {
        this.line = line;
    }

    public String getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
    }

    public String getPositionInfo() {
        return positionInfo;
    }

    public void setPositionInfo(String positionInfo) {
        this.positionInfo = positionInfo;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }
}
