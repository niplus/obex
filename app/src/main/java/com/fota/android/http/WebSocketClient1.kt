package com.fota.android.http

import android.util.Log
import com.fota.android.app.Constants
import com.fota.android.app.FotaApplication
import com.fota.android.app.GsonSinglon
import com.fota.android.app.SocketKey
import com.fota.android.common.bean.SpotBean
import com.fota.android.common.bean.exchange.CurrentPriceBean
import com.fota.android.common.bean.home.DepthBean
import com.fota.android.common.bean.wallet.WalletBean
import com.fota.android.commonlib.http.BaseHttpPage
import com.fota.android.moudles.exchange.orders.ExchangeOrderBean
import com.fota.android.moudles.futures.FutureTopInfoBean
import com.fota.android.moudles.futures.bean.ConditionOrdersBean
import com.fota.android.moudles.futures.complete.FuturesCompleteBean
import com.fota.android.moudles.futures.money.FuturesMoneyBean
import com.fota.android.moudles.futures.order.FuturesOrderBean
import com.fota.android.moudles.market.bean.FutureItemEntity
import com.fota.android.moudles.market.bean.MarketCardItemBean
import com.fota.android.moudles.mine.bean.XianhuoChengjiaoBean.XianhuoChengjiaoBeanItem
import com.fota.android.socket.WebSocketEntity
import com.fota.android.socket.params.SocketEntrustParam
import com.fota.android.utils.DeviceUtils
import com.fota.android.utils.GsonUtils
import com.fota.android.utils.UserLoginUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ndl.lib_common.utils.LiveDataBus.getBus
import com.tencent.mmkv.MMKV
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object WebSocketClient1 {
    private var webSocketClient: WebSocket? = null
    //当前订阅的参数，可用来重连订阅和订阅去重
    private var registerMap = ConcurrentHashMap<Int, WebSocketEntity<*>>()

    fun openWebsocket(){
        if (webSocketClient != null) return
        //String language = AppConfigs.getLanguege().getLanguage();
        val language = MMKV.defaultMMKV()!!.decodeString("language", "zh")
        //构造request对象
        val request = Request.Builder()
            .url(WebSocketUtils.getWsAddress())
            .addHeader("Connection", "keep-alive")
            .addHeader("brokerId", Constants.BROKER_ID)
            .header("Version", DeviceUtils.getVersonName(FotaApplication.getInstance()))
            .header("Platform", "2")
            .header("Accept-Language", language!!)
            .build()


        Http.getClient().newWebSocket(request, object : WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.i("websocket", "open")
                webSocketClient = webSocket

                //重连订阅之前的
                if (registerMap.isNotEmpty()){
                    registerMap.forEach {
                        sendMsg(it.value)
                    }
                }
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                dealMessage(text)
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.i("websocket", "onClosed reason")
                webSocketClient = null
            }
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocketClient = null
            }
        })
    }

    fun dealMessage(text: String){
        val msg = JSONObject(text)
        val jsonString = msg.getString("data")
        when (msg.getInt("reqType")){
            SocketKey.HangQingKaPianReqType -> {
                val list = GsonUtils.parseToList(jsonString, Array<MarketCardItemBean>::class.java)
                    dealCardJson(list)
            }

            SocketKey.TradeWeiTuoReqType->{
                Log.i("websocket", "TradeWeiTuoReqType text: $text")
                val bean: DepthBean = GsonSinglon.getInstance().fromJson(jsonString, DepthBean::class.java)
                getBus<DepthBean>(SocketKey.TradeWeiTuoReqType.toString()).postValue(bean)
            }

            SocketKey.HangQingNewlyPriceReqType->{
                val newPrice = GsonSinglon.getInstance().fromJson(jsonString, CurrentPriceBean::class.java)
                getBus<CurrentPriceBean>(SocketKey.HangQingNewlyPriceReqType.toString()).postValue(newPrice)
                Log.i("websocket", "HangQingNewlyPriceReqType text: $text")
            }

            SocketKey.MineEntrustReqType -> {
                Log.i("websocket", "MineEntrustReqType text: $text")
                val type = object : TypeToken<BaseHttpPage<ExchangeOrderBean>>() {}.type
                val list = GsonSinglon.getInstance()
                    .fromJson<BaseHttpPage<ExchangeOrderBean>>(jsonString, type)

                getBus<BaseHttpPage<ExchangeOrderBean>>(SocketKey.MineEntrustReqType.toString()).postValue(list)
            }

            SocketKey.MineDealReqType ->{
                val type = object : TypeToken<BaseHttpPage<XianhuoChengjiaoBeanItem>>() {}.type
                val list = GsonSinglon.getInstance()
                    .fromJson<BaseHttpPage<XianhuoChengjiaoBeanItem>>(jsonString, type)
                getBus<BaseHttpPage<XianhuoChengjiaoBeanItem>>(SocketKey.MineDealReqType.toString()).postValue(list)
                Log.i("websocket", "MineDealReqType text: $text")
            }

            SocketKey.MineAssetReqType ->{
                Log.i("websocket", "MineAssetReqType text: $text")
                //                Type type = new TypeToken<BaseHttpResult<WalletBean>>() {
//                }.getType();
//                BaseHttpResult<WalletBean> list = GsonSinglon.getInstance().fromJson(jsonString, type);
                val list = GsonSinglon.getInstance().fromJson(jsonString, WalletBean::class.java)
                getBus<WalletBean>(SocketKey.MineAssetReqType.toString()).postValue(list)
            }

            SocketKey.FUTURE_TOP->{
                val map = GsonSinglon.getInstance().fromJson(jsonString, FutureTopInfoBean::class.java)
                getBus<FutureTopInfoBean>(SocketKey.FUTURE_TOP.toString()).postValue(map)
                Log.i("websocket", "FUTURE_TOP text: $text")
            }
            SocketKey.MARKET_SPOTINDEX->{
                Log.i("websocket", "MARKET_SPOTINDEX text: $text")
                val spotBean: SpotBean = Gson().fromJson<SpotBean>(text, SpotBean::class.java)
                getBus<SpotBean>(SocketKey.MARKET_SPOTINDEX.toString()).postValue(spotBean)
            }
            SocketKey.CONDITION_ORDER->{
                val orders = Gson().fromJson(jsonString, ConditionOrdersBean::class.java)
                getBus<ConditionOrdersBean>(SocketKey.CONDITION_ORDER.toString()).postValue(orders)
            }
            SocketKey.MinePositionReqType->{
                val type = object : TypeToken<BaseHttpPage<FuturesMoneyBean>>() {}.type
                val list = GsonSinglon.getInstance().fromJson<BaseHttpPage<FuturesMoneyBean>>(jsonString, type)
                getBus<BaseHttpPage<FuturesMoneyBean>>(SocketKey.MinePositionReqType.toString()).postValue(list)
                Log.i("websocket", "MinePositionReqType text: $text")
            }
            SocketKey.MineEntrustReqType_CONTRACT->{
                Log.i("websocket", "MineEntrustReqType_CONTRACT text: $text")
                val type = object : TypeToken<BaseHttpPage<FuturesOrderBean>>() {}.type
                val list = GsonSinglon.getInstance()
                    .fromJson<BaseHttpPage<FuturesOrderBean>>(jsonString, type)
                getBus<BaseHttpPage<FuturesOrderBean>>(SocketKey.MineEntrustReqType_CONTRACT.toString()).postValue(list)
            }
            SocketKey.TradeDealReqType->{
                Log.i("websocket", "TradeDealReqType text: $text")
                val type = object : TypeToken<BaseHttpPage<FuturesCompleteBean>>() {}.type
                val list = GsonSinglon.getInstance()
                    .fromJson<BaseHttpPage<FuturesCompleteBean>>(jsonString, type)
                getBus<BaseHttpPage<FuturesCompleteBean>>(SocketKey.TradeDealReqType.toString()).postValue(list)
            }
        }
    }

    /**
     * @param gson
     * @param jsonString 处理 card的推送
     */
    private fun dealCardJson(cards: List<MarketCardItemBean>) {
        val list = mutableListOf<FutureItemEntity>()
        for (each in cards) {
            val future = FutureItemEntity(each.name)
            future.lastPrice = each.lastPrice
            future.trend = each.gain
            future.uscPrice = each.uscPrice
            future.isHot = each.isFire
            future.entityId = each.id
            future.entityType = each.type
            future.volume = each.totalVolume
            future.assetName = each.assetName
            future.contractType = each.contractType
            val datas = future.datas
            if (each.line != null) {
                for (i in each.line.indices) {
                    val entity = each.line[i]
                    datas.add(entity)
                }
            }

            list.add(future)
        }
        getBus<List<FutureItemEntity>>(SocketKey.HangQingKaPianReqType.toString()).postValue(list)
    }

    fun unRegist(socketKey: Int){
        Log.i("++++++++++++", "send socket: $socketKey")
        val socketEntity = WebSocketEntity<SocketEntrustParam>()
        socketEntity.reqType = socketKey
        socketEntity.setHandleType(3)
        registerMap.remove(socketKey)
        sendMsg(socketEntity)
    }

    fun register(webSocketEntity: WebSocketEntity<*>){
        Log.i("++++++++++++", "socket: $webSocketEntity")
        //添加token
        if (UserLoginUtil.getToken() != null) {
            webSocketEntity.token = UserLoginUtil.getToken()
        }

        if (registerMap[webSocketEntity.reqType] != null){
            val cacheEntity = registerMap[webSocketEntity.reqType]
            when(webSocketEntity.reqType){
                //有些订阅会切换，如果内容一样则不重复订阅
                SocketKey.FUTURE_TOP, SocketKey.CONDITION_ORDER,SocketKey.MinePositionReqType,
                SocketKey.MineEntrustReqType_CONTRACT, SocketKey.TradeDealReqType->{
                    if (webSocketEntity.param.equals(cacheEntity!!.param) && webSocketEntity.token.equals(cacheEntity.token)){
                        Log.i("websocket", "socket same")
                        //已经订阅也需要判断是否还连接，如果已经断开则重连
                        if (webSocketClient == null){
                            openWebsocket()
                        }
                        return
                    }else{
                        //如果不相等，这类订阅则需要先取消订阅再去订阅新的
                        unRegist(webSocketEntity.reqType)
                    }
                }
                SocketKey.MARKET_SPOTINDEX, SocketKey.TradeWeiTuoReqType, SocketKey.HangQingNewlyPriceReqType->{
                    if (webSocketEntity.param.equals(cacheEntity!!.param)){
                        Log.i("websocket", "socket same")
                        //已经订阅也需要判断是否还连接，如果已经断开则重连
                        if (webSocketClient == null){
                            openWebsocket()
                        }
                        return
                    }else{
                        //如果不相等，这类订阅则需要先取消订阅再去订阅新的
                        unRegist(webSocketEntity.reqType)
                    }
                }
                else-> {
                    //已经订阅也需要判断是否还连接，如果已经断开则重连
                    if (webSocketClient == null){
                        openWebsocket()
                    }
                    return
                }
            }
        }

        registerMap[webSocketEntity.reqType] = webSocketEntity

        //如果socket已经断开，则先重连
        if (webSocketClient == null){
            openWebsocket()
            return
        }
        Log.i("++++++++++++", "send socket: $webSocketEntity")
        sendMsg(webSocketEntity)
    }

    fun sendMsg(msg: Any){
        Log.d("websocket", "send Msg: $msg")
        val json = GsonSinglon.getInstance().toJson(msg)
        webSocketClient!!.send(json)
    }

}