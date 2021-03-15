package com.fota.android.moudles.market.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * market 页签 列表中的单项
 * 比如 btc 当周
 * 还可能有 btc现货指数
 * 服务返回的json bean
 */
public class MarketCardItemBean implements Serializable {

//    {
//        "price": [
//        ],
//        "volume": [
//        ],
//        "time":[
//        ],
//        "lastPrice": "7000.00",
//        "type": 2,
//        "gain": "+0.00%",
//        "collect": false,
//        "name": "BTC0203",
//        "id": 1001,
//        "isFire": true
//          gains:[]
//    }

    public MarketCardItemBean(String futureName) {
        this.name = futureName;
        this.line = new ArrayList<>();
    }

    private List<ChartLineEntity> line;
    //
    private String lastPrice;
    //带$的price--指数和合约的情况，跟lastPrice一样，加¥

    public String getUscPrice() {
        return uscPrice;
    }

    public void setUscPrice(String uscPrice) {
        this.uscPrice = uscPrice;
    }

    private String uscPrice;

    //1-指数 2-行情合约 3-USDT
    private int type;
    //updown
    private String gain;
    //self choose
    private boolean collect;
    //btc1811 etc
    private String name;
    //合约、指数、USDK对应的唯一id
    private int id;
    private boolean isFire;
    private String totalVolume;

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

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    //合约标的物名称 合约币的名字 btc
    private String assetName;
    //周月季
    private int contractType;
    //收藏时间
    private long collectTime;

    public String getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(String totalVolume) {
        this.totalVolume = totalVolume;
    }

    public List<ChartLineEntity> getLine() {
        return line;
    }

    public void setLine(List<ChartLineEntity> line) {
        this.line = line;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGain() {
        return gain;
    }

    public void setGain(String gain) {
        this.gain = gain;
    }

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
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

    public boolean isFire() {
        return isFire;
    }

    public void setFire(boolean fire) {
        isFire = fire;
    }

    public long getCollectTime() {
        return collectTime;
    }
}
