package com.fota.android.moudles.common;

import com.fota.android.common.bean.exchange.CurrentPriceBean;
import com.fota.android.common.bean.home.EntrustBean;
import com.fota.android.commonlib.base.BaseView;

import java.util.List;

/**
 * Created by jiang on 2018/08/16.
 */

public interface BaseTradeViewInterface extends BaseView {

    //回设行情数据
    void onRefreshTicker(CurrentPriceBean tickerModel);

    //刷新Depth
    void onRefreshDepth(List<EntrustBean> buys, List<EntrustBean> sells, List<String> precisions, Float dollarEvaluation);

    /**
     * list 分时图列表 -- 此参数已经去掉
     */
    //刷新T线 重置
    void resetFreshTimelineData();

    //刷新T线 增量更新
    void onRefreshTimelineData();

    //刷新K线 重置
    void resetFreshKlineData();

    /**
     * 刷新k线 增量更新
     *
     * @param isAdd true add;false refresh
     */
    void onRefreshKlineData(boolean isAdd);

    //持仓 or 合约到期日期 更新
    void onRefreshDeliveryOrHold();

    /**
     * T线对应的
     *  spotList 现货列表
     *  holdingPrice 持有价格
     */
    //现货 重置
//    void resetFreshTimeSpotData(List<ChartLineEntity> spotList, double holdingPrice);

    /**
     * @param charType 0 timeline ; 1 kline
     *
     * @param isShowLoading isLoading show
     */
    void setOverShowLoading(int charType, boolean isShowLoading);

    /**
     * @param charType 0 timeline ; 1 kline
     */
    void onNoDataCallBack(int charType);
}
