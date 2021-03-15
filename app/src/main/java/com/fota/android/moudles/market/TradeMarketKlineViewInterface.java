package com.fota.android.moudles.market;

import com.fota.android.common.bean.home.DealBean;
import com.fota.android.moudles.common.BaseTradeViewInterface;
import com.fota.android.moudles.market.bean.MarketIndexBean;

import java.util.List;

/**
 * Created by jiang on 2018/08/02.
 */

public interface TradeMarketKlineViewInterface extends BaseTradeViewInterface {

    //刷新Deal
    void onRefreshDeal(List<DealBean> list);

    //刷新spot Deal
    void onRefreshSpot(MarketIndexBean indexBean);

    //通知k线周期横向滚动条可以点击
//    void notifyKlineTypeActive(boolean canClick);
}
