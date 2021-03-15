package com.fota.android.http;

import com.fota.android.common.bean.home.DealBean;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.BaseHttpResult;
import com.fota.android.core.base.BtbMap;
import com.fota.android.moudles.market.bean.CardFavorParamBean;
import com.fota.android.moudles.market.bean.MarketCardItemBean;
import com.fota.android.moudles.market.bean.MarketKLineBean;
import com.fota.android.moudles.market.bean.MarketTimeLineBean;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * 行情 相关的请求
 */
public interface MarketService {

    @GET("market/home")
    Observable<BaseHttpResult<List<MarketCardItemBean>>> getMarketCards(@QueryMap BtbMap map);

    @GET("market/polyLine")
    Observable<BaseHttpResult<MarketTimeLineBean>> getTimeLineDatas(@QueryMap BtbMap map);

    @POST("market/collection")
    Observable<BaseHttpEntity> postFavor(@Body CardFavorParamBean body);

    @GET("home/trade/matched/all")
    Observable<BaseHttpResult<List<DealBean>>> getAllDeal(@QueryMap BtbMap map);

    @GET("market/kLine")
    Observable<BaseHttpResult<MarketKLineBean>> getKlineDatas(@QueryMap BtbMap map);

    //    https://mock.emc.top/repository/editor?id=18&mod=61&itf=515
    @GET("trade/index/matched")
    Observable<BaseHttpResult<List<DealBean>>> getSpotDeal(@QueryMap BtbMap map);
}
