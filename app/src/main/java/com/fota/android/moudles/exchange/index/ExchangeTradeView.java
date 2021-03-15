package com.fota.android.moudles.exchange.index;

import com.fota.android.moudles.common.BaseTradeViewInterface;

public interface ExchangeTradeView extends BaseTradeViewInterface {

    void refreshCurrency();

    void showTopInfo(String name);

    void refreshComplete();
}