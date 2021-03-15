package com.fota.android.http;

import com.fota.android.common.bean.BaseAsset;
import com.fota.android.common.bean.exchange.CurrentPriceBean;
import com.fota.android.common.bean.exchange.ExchangeBody;
import com.fota.android.common.bean.exchange.ExchangeCurrency;
import com.fota.android.common.bean.home.DepthBean;
import com.fota.android.common.bean.wallet.CoinBean;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.BaseHttpPage;
import com.fota.android.commonlib.http.BaseHttpResult;
import com.fota.android.core.base.BtbMap;
import com.fota.android.moudles.exchange.orders.ExchangeOrderBean;
import com.fota.android.moudles.futures.FutureTopInfoBean;
import com.fota.android.moudles.futures.complete.FuturesCompleteBean;
import com.fota.android.moudles.futures.money.FuturesMoneyBean;
import com.fota.android.moudles.futures.order.FuturesOrderBean;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface ExchargeService {

    @GET("trade/usdk/list")
    Observable<BaseHttpResult<List<ExchangeCurrency>>> getUsdtList();

    @GET("trade/usdk/list")
    Observable<BaseHttpResult<List<BaseAsset>>> getBaseAssetList();

    @GET("asset/coin/list")
    Observable<BaseHttpResult<List<CoinBean>>> getCoinList();

    @GET("trade/usdt/entrust")
    Observable<BaseHttpResult<BaseHttpPage<ExchangeOrderBean>>> getOrders(@QueryMap BtbMap map);

    @POST("trade/usdk/order")
    Observable<BaseHttpEntity> makeOrder(@Body ExchangeBody body);

    @POST("trade/contract/order")
    Observable<BaseHttpEntity> makeContractOrder(@Body ExchangeBody body);

    @POST("trade/contract/preciseMargin")
    Observable<BaseHttpResult<BtbMap>> preciseMargin(@Body ExchangeBody body);

    @DELETE("trade/usdk/order")
    Observable<BaseHttpEntity> deleteOrder(@QueryMap BtbMap map);

    @DELETE("trade/contract/order")
    Observable<BaseHttpEntity> deleteContractOrder(@QueryMap BtbMap map);

    @GET("home/trade/order/all")
    Observable<BaseHttpResult<DepthBean>> getFiveItemList(@QueryMap BtbMap map);

    @GET("trade/contract/position")
    Observable<BaseHttpResult<BaseHttpPage<FuturesMoneyBean>>> getFufureMoneys(@QueryMap BtbMap map);

    @GET("trade/contract/entrust")
    Observable<BaseHttpResult<BaseHttpPage<FuturesOrderBean>>> getFutureOrders(@QueryMap BtbMap map);

    @GET("trade/contract/matched")
    Observable<BaseHttpResult<BaseHttpPage<FuturesCompleteBean>>> getFutureCompletes(@QueryMap BtbMap map);


    @GET("trade/current/matched")
    Observable<BaseHttpResult<CurrentPriceBean>> getNowPrice(@QueryMap BtbMap map);

    @GET("trade/contract/list")
    Observable<BaseHttpResult<List<ContractAssetBean>>> getContractTree();


    @GET("asset/contract/account")
    Observable<BaseHttpResult<FutureTopInfoBean>> getContractAccount(@QueryMap BtbMap map);

    //当前现货指数
    @GET("trade/current/delivery")
    Observable<BaseHttpResult<BtbMap>> getContractDelivery(@QueryMap BtbMap map);


    @GET("trade/verify/password")
    Observable<BaseHttpResult<BtbMap>> verifyPassword(@QueryMap BtbMap map);

    //资金密码token验证
    @POST("user/verify/trade/password")
    Observable<BaseHttpResult<String>> changePasswordToToken(@Body BtbMap body);


}
