package com.fota.android.moudles.market.bean;

import java.io.Serializable;
import java.util.List;

/**
 * market 指数
 * 数据
 * 服务返回的json bean
 */
public class MarketIndexBean implements Serializable {

    public String getUsdtSpotIndex() {
        return usdtSpotIndex;
    }

    public void setUsdtSpotIndex(String usdtSpotIndex) {
        this.usdtSpotIndex = usdtSpotIndex;
    }

    public List<CoinIndexOutBean> getCoinList() {
        return coinList;
    }

    public void setCoinList(List<CoinIndexOutBean> coinList) {
        this.coinList = coinList;
    }

    public String getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(String averagePrice) {
        this.averagePrice = averagePrice;
    }

    public String getUsdtRate() {
        return usdtRate;
    }

    public void setUsdtRate(String usdtRate) {
        this.usdtRate = usdtRate;
    }

    public MarketIndexBean() {


    }

    private String usdtSpotIndex;

    private List<CoinIndexOutBean> coinList;

    private String averagePrice;

    private String usdtRate;
}
