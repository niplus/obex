package com.fota.android.http

import com.fota.android.common.bean.home.BannerBean
import com.fota.android.commonlib.http.BaseHttpResult
import com.fota.android.core.base.BtbMap
import com.fota.android.moudles.market.bean.MarketCardItemBean
import com.ndl.lib_common.base.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
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
}