package com.fota.android.moudles.futures;

import com.fota.android.core.base.BtbMap;
import com.fota.android.moudles.exchange.index.ExchangeTradeView;

public interface FutureTradeView extends ExchangeTradeView {

    void setContractAccount(FutureTopInfoBean map);

    void setContractDelivery(BtbMap map);

    void setPreciseMargin(BtbMap map);
}
