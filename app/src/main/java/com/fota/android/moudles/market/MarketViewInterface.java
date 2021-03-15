package com.fota.android.moudles.market;

import com.fota.android.common.bean.home.BannerBean;
import com.fota.android.common.bean.home.NoticeBean;
import com.fota.android.commonlib.base.BaseView;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.moudles.market.bean.FutureItemEntity;

import java.util.List;

/**
 * Created by jiang on 2018/08/02.
 * 行情页面的interface
 * market view interface
 */

public interface MarketViewInterface extends BaseView {

    /**
     * 自己处理空数据与错误
     */
    void onErrorDeal(ApiException e);
    /**
     * 停止loading
     */
    void onCompleteLoading();

    /**
     * @param list 行情 合约列表
     */
    void setMarketList(List<FutureItemEntity> list);

    void marketRefresh(boolean isSocket);

    /**
     * banner
     * @param bannerBean
     */
    void setDoingEntity(List<BannerBean> bannerBean);

    /**
     * 公告
     * @param listBean
     */
    void setNotice(List<NoticeBean> listBean);
}
