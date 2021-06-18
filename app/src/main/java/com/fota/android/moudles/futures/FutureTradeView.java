package com.fota.android.moudles.futures;

import com.fota.android.core.base.BtbMap;
import com.fota.android.moudles.exchange.index.ExchangeTradeView;
import com.fota.android.moudles.futures.bean.SpotIndex;

public interface FutureTradeView extends ExchangeTradeView {

    void setContractAccount(FutureTopInfoBean map);

    void setContractDelivery(BtbMap map);

    void setPreciseMargin(BtbMap map);

    void onLeverChange();

    void onSelectView();

    void onSpotUpdate(SpotIndex spotIndex);
}
