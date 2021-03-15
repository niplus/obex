package com.fota.android.moudles.market;

import com.fota.android.commonlib.base.BaseView;

/**
 * Created by jiang on 2018/08/14.
 */

public interface FullScreenKlineViewInterface extends BaseView {

    /**
     */
    //刷新K线 重置
    void resetFreshKlineData();

    /**
     * 刷新k线 增量更新
     * add or refresh
     */
    void onRefreshKlineData(boolean isAdd);

    //通知k线周期横向滚动条可以点击
//    void notifyKlineTypeActive(boolean canClick);

    //持仓 or 合约到期日期 更新
    void onRefreshDeliveryOrHold();

    /**
     *
     * @param isShowLoading isLoading show
     */
    void setOverShowLoading(boolean isShowLoading);

    /**
     */
    void onNoDataCallBack();
}
