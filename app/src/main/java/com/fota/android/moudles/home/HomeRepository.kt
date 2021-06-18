package com.fota.android.moudles.home

import com.fota.android.common.bean.home.BannerBean
import com.fota.android.core.base.BtbMap
import com.fota.android.http.ApiService
import com.fota.android.http.Http
import com.fota.android.moudles.market.bean.MarketCardItemBean
import com.ndl.lib_common.base.BaseRepository
import com.ndl.lib_common.base.Response

class HomeRepository: BaseRepository(){

    suspend fun getBanner(): Response<List<BannerBean>>{
        return apiCall {
            Http.getRetrofit().create(ApiService::class.java).banner()
        }
    }

    suspend fun getMarketCards(): Response<List<MarketCardItemBean>>{
        val currentTimeMillis = System.currentTimeMillis()
        val endTime = currentTimeMillis.toString() + ""
        //15min
        //15min
        val startTime = (currentTimeMillis - 48 * 15 * 60 * 1000).toString() + ""

        val paramsMap = BtbMap()
        paramsMap["resolution"] = "2"
        paramsMap["startTime"] = startTime
        paramsMap["endTime"] = endTime
        paramsMap["type"] = "1"

        return apiCall {
            Http.getRetrofit().create(ApiService::class.java).getMarketCards(paramsMap)
        }
    }
}