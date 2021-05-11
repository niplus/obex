package com.fota.android.http

import com.fota.android.common.bean.home.BannerBean
import com.fota.android.commonlib.http.BaseHttpResult
import com.fota.android.core.base.BtbMap
import com.fota.android.moudles.futures.bean.CancelOrderEntitiy
import com.fota.android.moudles.futures.bean.ConditionOrderEntity
import com.fota.android.moudles.futures.bean.ConditionOrdersBean
import com.fota.android.moudles.futures.bean.StopOrderEntity
import com.fota.android.moudles.market.bean.MarketCardItemBean
import com.ndl.lib_common.base.Response
import retrofit2.http.*
import rx.Observable

interface ApiService {

    //登录token验证
    @GET("account/user/checkToken")
    suspend fun loginTokenCheck(@Query("token") token: String): Response<String>

    //首页
    //轮播图
    @GET("home/banner")
    suspend fun banner(): Response<List<BannerBean>>

    @GET("market/home")
    suspend fun getMarketCards(@QueryMap map: BtbMap): Response<List<MarketCardItemBean>>

    @POST("contract/condition-order/place")
    suspend fun conditionOrder(@Body body: ConditionOrderEntity): Response<String>

    @POST("contract/condition-order/stop/order")
    suspend fun stopOrder(@Body body: StopOrderEntity): Response<String>

    @GET("contract/condition-order/histories")
    suspend fun getConditionHistoryOrder(@Query("pageNO")pageNo: Int, @Query("pageSize")pageSize: Int, @Query("startTime")startTime: String?, @Query("endTime")endTime:String?): Response<ConditionOrdersBean>

    @GET("contract/condition-order/orders/now")
    suspend fun getConditionOrder(@Query("pageNO")pageNo: Int, @Query("pageSize")pageSize: Int, @Query("startTime")startTime: String?, @Query("endTime")endTime:String?): Response<ConditionOrdersBean>

    @POST("contract/condition-order/cancel")
    suspend fun cancelConditionOrder(@Body body: CancelOrderEntitiy): Response<String>
}