package com.fota.android.moudles.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.fota.android.app.FotaApplication
import com.fota.android.app.SocketKey
import com.fota.android.common.bean.home.BannerBean
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.moudles.market.bean.ChartLineEntity
import com.fota.android.moudles.market.bean.FutureItemEntity
import com.fota.android.moudles.market.bean.MarketCardItemBean
import com.fota.android.socket.IWebSocketObserver
import com.fota.android.socket.WebSocketClient
import com.fota.android.socket.WebSocketEntity
import com.fota.android.socket.params.SocketCardParam
import java.util.*

class HomeViewModel: BaseViewModel() {

    val bannerLiveData = MutableLiveData<List<BannerBean>>()

    val futureListLiveData = MutableLiveData<List<FutureItemEntity>>()

    private val client =
        FotaApplication.getInstance().client

    private val repository = HomeRepository()
    fun getBanner(){
        launchUI {
            val result = repository.getBanner()

            if (result.code == 0){
                bannerLiveData.value = result.data
            }
        }
    }

    fun getCoinData(){
        launchUI {
            val result = repository.getMarketCards()
            if (result.code != 0)
                return@launchUI

            val list = result.data

            futureListLiveData.value = dealFutureItemEntities(list)
        }
    }


    /**
     * @param list
     * @return
     */
    private fun dealFutureItemEntities(list: List<MarketCardItemBean>): List<FutureItemEntity> {
        val socketEntity = WebSocketEntity<SocketCardParam>()
        //订阅 全部的卡片推送
        socketEntity.setParam(SocketCardParam(1, "2"))
        socketEntity.reqType = SocketKey.HangQingKaPianReqType
        //订阅socket
        client.addChannel(socketEntity, IWebSocketObserver { reqType, jsonString, additionEntity ->  })
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