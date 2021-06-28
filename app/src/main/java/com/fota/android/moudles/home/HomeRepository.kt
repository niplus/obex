package com.fota.android.moudles.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.fota.android.app.SocketKey
import com.fota.android.common.bean.home.BannerBean
import com.fota.android.core.base.BtbMap
import com.fota.android.http.ApiService
import com.fota.android.http.Http
import com.fota.android.http.WebSocketClient1
import com.fota.android.moudles.market.bean.ChartLineEntity
import com.fota.android.moudles.market.bean.FutureItemEntity
import com.fota.android.moudles.market.bean.MarketCardItemBean
import com.fota.android.socket.WebSocketEntity
import com.fota.android.socket.params.SocketCardParam
import com.ndl.lib_common.base.BaseRepository
import com.ndl.lib_common.base.Response
import com.ndl.lib_common.utils.LiveDataBus
import java.util.ArrayList

class HomeRepository: BaseRepository(){

    companion object{
        val marketInfoLivedata = MutableLiveData<List<FutureItemEntity>>()
    }

    init {
        LiveDataBus.getBus<List<FutureItemEntity>>(SocketKey.HangQingKaPianReqType.toString()).observeForever{
            if (marketInfoLivedata.value.isNullOrEmpty()){
                marketInfoLivedata.value = it
            }else{
                val cacheList = marketInfoLivedata.value!!
                val result = it.map { item->
                    cacheList.forEach { cacheItem->
                        if (item.entityId == cacheItem.entityId && item.entityType == cacheItem.entityType){
                            val datas = cacheItem.datas

                            if (datas.isEmpty()) return@observeForever
                            val lastData = datas.last()
                            if (item.datas[0].time == lastData.time){
                                lastData.close = item.datas[0].close
                            }else{
                                datas.add(item.datas[0])
                            }
                            item.datas.clear()
                            item.datas.addAll(datas)
                            return@forEach
                        }
                    }

                    item
                }

                marketInfoLivedata.value = result
            }
        }
    }

    suspend fun getBanner(): Response<List<BannerBean>>{
        return apiCall {
            Http.getRetrofit().create(ApiService::class.java).banner()
        }
    }

    suspend fun getMarketCards(): Response<List<MarketCardItemBean>>{
        val currentTimeMillis = System.currentTimeMillis()
        val endTime = currentTimeMillis.toString() + ""
        //15min
        val startTime = (currentTimeMillis - 48 * 15 * 60 * 1000).toString() + ""

        val paramsMap = BtbMap()
        paramsMap["resolution"] = "2"
        paramsMap["startTime"] = startTime
        paramsMap["endTime"] = endTime
        paramsMap["type"] = "1"

        return apiCall {
            val result = Http.getRetrofit().create(ApiService::class.java).getMarketCards(paramsMap)
            marketInfoLivedata.postValue(dealFutureItemEntities(result.data))
            result
        }
    }


    /**
     * @param list
     * @return
     */
    private fun dealFutureItemEntities(list: List<MarketCardItemBean>): List<FutureItemEntity> {
        val socketEntity = WebSocketEntity<SocketCardParam>()
        //订阅 全部的卡片推送
        socketEntity.setParam(SocketCardParam(0, "2"))
        socketEntity.reqType = SocketKey.HangQingKaPianReqType
        //订阅socket
        WebSocketClient1.register(socketEntity)
//        //client.addChannel(socketEntity, IWebSocketObserver { reqType, jsonString, additionEntity ->  })
        val result: MutableList<FutureItemEntity> = ArrayList()
        for (each in list) {
            val future = FutureItemEntity(each.name)
            future.lastPrice = each.lastPrice
            future.trend = each.gain
            future.contractType = each.contractType
            future.assetName = each.assetName
            future.uscPrice = each.uscPrice
            if (!StringUtils.isEmpty(each.totalVolume)) {
                future.volume = each.totalVolume
            }
            future.isFavorite = each.isCollect
            if (each.isCollect) {
                future.collectTime = each.collectTime
            }
            future.isHot = each.isFire
            future.entityId = each.id
            future.entityType = each.type
            if (each.line != null) {
                val line = each.line
                for (i in line.indices) {
                    val tmp = line[i] ?: continue
                    // 有指数，但是没有对应节点的future-reverse-reset
                    if (tmp.open == 0.0 && tmp.close == 0.0 && tmp.volume == 0f) {
                        if (i == 0 || line[i - 1] == null) continue else {
                            val entityAdd = ChartLineEntity()
                            var value: Double = if (future.datas.size > 0) {
                                val length = future.datas.size
                                future.datas[length - 1].close
                            } else {
                                line[i - 1]!!.close
                            }
                            entityAdd.open = value
                            entityAdd.close = value
                            entityAdd.high = value
                            entityAdd.low = value
                            entityAdd.time = tmp.time
                            future.datas.add(entityAdd)
                        }
                    } else future.datas.add(tmp)
                }
            }
            //            if(future.getDatas().size() > 0) {
            result.add(future)
            //            }
        }
        return result
    }
}