package com.fota.android.http;

import com.fota.android.app.Constants;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.HttpsUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.SharedPreferencesUtil;
import com.fota.android.utils.DeviceUtils;
import com.tencent.mmkv.MMKV;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * 1. 类的用途 封装OkHttp3的工具类 用单例设计模式
 */

public class WebSocketUtils {

    private static SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    /**
     * 懒汉 安全 加同步
     * 私有的静态成员变量 只声明不创建
     * 私有的构造方法
     * 提供返回实例的静态方法
     */

    private static WebSocketUtils webSocketUtils = null;

    private WebSocketUtils() {

    }

    public static WebSocketUtils getInstance() {
        if (webSocketUtils == null) {
            //加同步安全
            synchronized (WebSocketUtils.class) {
                if (webSocketUtils == null) {
                    webSocketUtils = new WebSocketUtils();
                }
            }

        }

        return webSocketUtils;
    }

    //websocket
    private static OkHttpClient websocketHttpClient = null;

    public synchronized static OkHttpClient getWebsocketClient() {
        if (websocketHttpClient == null) {
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

            //新建client
            websocketHttpClient = new OkHttpClient().newBuilder()
//                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
//                    .hostnameVerifier(sslParams.hostnameVerifier)
                    .pingInterval(30, TimeUnit.SECONDS)
                    .build();
        }
        return websocketHttpClient;
    }

    public static WebSocket doWebSocket(WebSocketListener listener) {
        OkHttpClient client = getWebsocketClient();
        client.pingIntervalMillis();
//        String language = AppConfigs.getLanguege().getLanguage();
        String language = MMKV.defaultMMKV().decodeString("language", "zh");

        //构造request对象
        Request request = new Request.Builder()
                .url(getWsAddress())
                .addHeader("Connection", "keep-alive")
                .addHeader("brokerId", Constants.BROKER_ID)
                .header("Version", DeviceUtils.getVersonName(FotaApplication.getInstance()))
                .header("Platform", "2")
                .header("Accept-Language", language)
                .build();
        //new_address 一个websocket调用对象并建立连接
        return client.newWebSocket(request, listener);
    }

    public static String getWsAddress() {
        //return "wss://www.fota.com/mapi/websocket";
//        return "ws://172.16.50.180:8089/mapi/websocket";
        if (Constants.DEBUG) {
            return Pub.isStringEmpty(AppConfigs.getWsAddress()) ? Constants.BASE_WEBSOCKET : AppConfigs.getWsAddress();
        } else {
            return Constants.BASE_WEBSOCKET_WSS;
        }
    }

}

