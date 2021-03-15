package com.fota.android.moudles.main.bean;

import com.fota.android.common.bean.exchange.ExchangeCurrency;
import com.fota.android.moudles.futures.FutureContractBean;

import java.io.Serializable;

/**
 * 行情 跳转 合约和usdt用到的实体
 */
public class BundleForTradeEntity implements Serializable {
    private ExchangeCurrency bundleForExchange;

    private String futureCoinName;
    private FutureContractBean bundleForFuture;

    private boolean isBuy;

    public ExchangeCurrency getBundleForExchange() {
        return bundleForExchange;
    }

    public void setBundleForExchange(ExchangeCurrency bundleForExchange) {
        this.bundleForExchange = bundleForExchange;
    }

    public String getFutureCoinName() {
        return futureCoinName;
    }

    public void setFutureCoinName(String futureCoinName) {
        this.futureCoinName = futureCoinName;
    }

    public FutureContractBean getBundleForFuture() {
        return bundleForFuture;
    }

    public void setBundleForFuture(FutureContractBean bundleForFuture) {
        this.bundleForFuture = bundleForFuture;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }
}
