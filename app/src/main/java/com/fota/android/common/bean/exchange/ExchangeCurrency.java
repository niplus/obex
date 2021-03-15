package com.fota.android.common.bean.exchange;

import com.fota.android.common.bean.BaseAsset;
import com.fota.android.commonlib.utils.Pub;

public class ExchangeCurrency extends BaseAsset {
    //jiang
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ExchangeCurrency) {
            String name = ((ExchangeCurrency) obj).getAssetName();
            return !Pub.isStringEmpty(this.getAssetName()) && this.getAssetName().equals(name);
        }
        return super.equals(obj);
    }

    int usdtTradePricePrecision;
    int usdtTradeAmountPrecision;
    int usdtTradeValuePrecision;
    // 最大可买卖的小数位
    int usdkMaxMinPricePrecision;
    private String defaultAmount;
    private String coinIconUrl;

    public String getCoinIconUrl() {
        return coinIconUrl;
    }

    public void setCoinIconUrl(String coinIconUrl) {
        this.coinIconUrl = coinIconUrl;
    }


    public int getUsdtTradePricePrecision() {
        return usdtTradePricePrecision;
    }

    public void setUsdtTradePricePrecision(int usdtTradePricePrecision) {
        this.usdtTradePricePrecision = usdtTradePricePrecision;
    }

    public int getUsdtTradeAmountPrecision() {
        return usdtTradeAmountPrecision;
    }

    public void setUsdtTradeAmountPrecision(int usdtTradeAmountPrecision) {
        this.usdtTradeAmountPrecision = usdtTradeAmountPrecision;
    }

    public String getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(String defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

    public int getUsdtTradeValuePrecision() {
        return usdtTradeValuePrecision;
    }

    public void setUsdtTradeValuePrecision(int usdtTradeValuePrecision) {
        this.usdtTradeValuePrecision = usdtTradeValuePrecision;
    }

    public int getUsdkMaxMinPricePrecision() {
        return usdkMaxMinPricePrecision;
    }

    public void setUsdkMaxMinPricePrecision(int usdkMaxMinPricePrecision) {
        this.usdkMaxMinPricePrecision = usdkMaxMinPricePrecision;
    }

    @Override
    public String getKey() {
        return getAssetName();
    }


}


