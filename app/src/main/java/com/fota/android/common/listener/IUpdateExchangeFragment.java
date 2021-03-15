package com.fota.android.common.listener;

import com.fota.android.common.bean.exchange.ExchangeCurrency;

public interface IUpdateExchangeFragment {

    void updateInstance(ExchangeCurrency bundleForExchange, boolean isBuy);

}
