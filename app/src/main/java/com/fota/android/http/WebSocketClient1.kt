package com.fota.android.http

import android.util.Log
import com.fota.android.app.Constants
import com.fota.android.app.FotaApplication
import com.fota.android.app.GsonSinglon
import com.fota.android.socket.WebSocketEntity
import com.fota.android.utils.DeviceUtils
import com.fota.android.utils.UserLoginUtil
import com.tencent.mmkv.MMKV
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
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
                Log.i("websocket", "onMessage : $text")
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

    fun register(webSocketEntity: WebSocketEntity<*>){
        //添加token
        if (UserLoginUtil.getToken() != null) {
            webSocketEntity.token = UserLoginUtil.getToken()
        }

        if (registerMap[webSocketEntity.reqType] != null){
            //已经订阅也需要判断是否还连接，如果已经断开则重连
            if (webSocketClient == null){
                openWebsocket()
            }
            return
        }

        registerMap[webSocketEntity.reqType] = webSocketEntity

        //如果socket已经断开，则先重连
        if (webSocketClient == null){
            openWebsocket()
            return
        }

        sendMsg(webSocketEntity)
    }

    fun sendMsg(msg: Any){
        Log.d("websocket", "send Msg: $msg")
        val json = GsonSinglon.getInstance().toJson(msg)
        webSocketClient!!.send(json)
    }

}