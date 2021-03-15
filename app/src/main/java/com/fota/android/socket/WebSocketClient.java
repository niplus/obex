package com.fota.android.socket;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.fota.android.app.GsonSinglon;
import com.fota.android.app.SocketKey;
import com.fota.android.common.bean.BeanChangeFactory;
import com.fota.android.common.bean.home.DepthBean;
import com.fota.android.commonlib.http.BaseHttpResult;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.http.WebSocketUtils;
import com.fota.android.moudles.market.bean.ChartLineEntity;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.fota.android.moudles.market.bean.HoldingEntity;
import com.fota.android.moudles.market.bean.MarketCardItemBean;
import com.fota.android.moudles.market.bean.MarketKLineBean;
import com.fota.android.moudles.market.bean.MarketTimeLineBean;
import com.fota.android.socket.params.SocketBaseParam;
import com.fota.android.socket.params.SocketEntrustParam;
import com.fota.android.socket.params.SocketMarketParam;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static com.fota.android.app.FotaApplication.getInstance;

/**
 * emmmm 我们的客户端
 */

public class WebSocketClient implements IWebSocketSubject {
    Handler handler = new Handler();

    private ConcurrentHashMap<String, List<IWebSocketObserver>> observers = new ConcurrentHashMap();
    //jiang 断连的时候，用来暂存observers，用于重新订阅
    private ConcurrentHashMap<String, List<IWebSocketObserver>> tempObservers = new ConcurrentHashMap<>();

    /**
     * @param type
     * @param o
     * @return 是否添加成功，是否真正添加
     */
    private boolean registerObserver(String type, IWebSocketObserver o) {
        //jiang1213 重新注册也需要先移除responsMap的对应内容
        if(responseMap != null) {
            responseMap.remove(type);
        }
        if (o == null) {
            //o == null 登录登出需要sendMessage
            return true;
        }
        if (observers == null) {
            return false;
        }

        List<IWebSocketObserver> typeObserver = observers.get(type);
        if (typeObserver != null) {
            if (!typeObserver.contains(o)) {
                typeObserver.add(o);
            } else {
                return false;
            }
        } else {
            typeObserver = new ArrayList<>();
            typeObserver.add(o);
            observers.put(type, typeObserver);
        }

        return true;
    }


    /**
     * @param type
     * @param o
     * @return 是否移除成功，是否真正移除
     */
    private boolean removeObserver(String type, IWebSocketObserver o) {
        //jiang 先移除responsMap的对应内容
        if(responseMap != null) {
            responseMap.remove(type);
        }

        if (o == null) {
            //o == null 登录登出需要sendMessage
            return true;
        }
        if (observers == null) {
            return false;
        }
        List<IWebSocketObserver> typeObserver = observers.get(type);
        if (typeObserver == null) {
            //虽然不是真正的移除，但是不该sendMessage了
            return false;
        }
        int i = typeObserver.indexOf(o);
        if (typeObserver.size() <= 0)
            observers.remove(type);
        if (i >= 0) {
            typeObserver.remove(i);
            return true;
        } else {
            return false;
        }
    }

    private void notifyObervers(String message) {
        if (observers == null) {
            return;
        }

        try {
            Gson gson = GsonSinglon.getInstance();
            final JSONObject resultJsonObj = new JSONObject(message);
            if (resultJsonObj.getInt("code") == 0 && resultJsonObj.get("reqType") != null && resultJsonObj.get("handleType") != null) {
                final BaseHttpResult result = gson.fromJson(message, BaseHttpResult.class);
                String jsonString = gson.toJson(result.getData());
                int handleType = resultJsonObj.getInt("handleType");
                int reqType = resultJsonObj.getInt("reqType");
//                String messageInJson = resultJsonObj.getString("message");
                int code = resultJsonObj.getInt("code");
//                L.e("ws notify", reqType + "");

                Object dataJson = new JSONTokener(jsonString).nextValue();
                SocketAdditionEntity entity;
                if (dataJson instanceof JSONObject) {
                    switch (reqType) {
                        case SocketKey.TradeWeiTuoReqType:
                        case SocketKey.HangQingNewlyPriceReqType:
                        case SocketKey.HangQingFenShiTuZheXianTuReqType:
                        case SocketKey.HangQingKlinePushReqType:
                            String paramString = resultJsonObj.getString("param");
                            SocketEntrustParam param = (SocketEntrustParam) gson.fromJson(paramString, SocketEntrustParam.class);
                            entity = new SocketAdditionEntity<SocketEntrustParam>(handleType, code, "");
                            entity.setParam(param);
                            break;
                        default:
                            entity = new SocketAdditionEntity<SocketEntrustParam>(handleType, code, "");
                    }

                    dealMessageType(gson, jsonString, reqType, entity);
                } else if (dataJson instanceof JSONArray) {
                    final JSONArray dataJsonArr = (JSONArray) dataJson;
                    if (dataJsonArr.length() > 0) {
                        entity = new SocketAdditionEntity(handleType, code, "");
                        if(reqType == SocketKey.HangQingTradeDetailReqType) {
                            String paramString = resultJsonObj.getString("param");
                            SocketBaseParam param = (SocketBaseParam) gson.fromJson(paramString, SocketBaseParam.class);
                            entity = new SocketAdditionEntity<SocketBaseParam>(handleType, code, "");
                            entity.setParam(param);
                        }
                        dealMessageType(gson, jsonString, reqType, entity);
                    }
                } else {
                    if (!TextUtils.isEmpty(jsonString)) {
                        entity = new SocketAdditionEntity(handleType, code, "");
                        dealMessageType(gson, jsonString, reqType, entity);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean isDuplicate(int reqType, String json) {
        if (TextUtils.isEmpty(json)) {
            return true;
        }
        String key = reqType + "";
        if (responseMap.containsKey(key)) {
            String before = responseMap.get(key);
            if (json.equals(before)) {
                return true;
            } else {
                responseMap.put(key, json);
            }
        } else {
            responseMap.put(key, json);
        }
        return false;
    }

    //dispatch message 分类处理
    private void dealMessageType(Gson gson, String jsonString, int reqType, SocketAdditionEntity addtionEntity) {
        try {
            if (needExceDuplicate(reqType) && isDuplicate(reqType, jsonString)) {
                return;
            }

            L.e("ws afterfliter", jsonString + "----" + reqType);
            switch (reqType) {
                case SocketKey.TradeWeiTuoReqType://委托
                    DepthBean bean = gson.fromJson(jsonString, DepthBean.class);
                    String key = bean.getType() + "-" + bean.getId();
//                    getInstance().getDepthMap().clear();
                    getInstance().getDepthMap().put(key, bean);
                    break;
                case SocketKey.HangQingKaPianReqType://card
                    dealCardJson(gson, jsonString);
                    break;
                case SocketKey.HangQingFenShiTuZheXianTuReqType:// timeline
//                    L.e("ws 6timeline", jsonString);
                    MarketTimeLineBean timeLineBean = gson.fromJson(jsonString, MarketTimeLineBean.class);
                    dealTimelinePush(timeLineBean);
                    break;
                case SocketKey.HangQingKlinePushReqType:// kline
//                    L.e("ws 9kline", jsonString);
                    MarketKLineBean kLineBean = gson.fromJson(jsonString, MarketKLineBean.class);
                    isAddFlagForKlineSocket = "refresh";
                    dealKlinePush(kLineBean);
                    jsonString = isAddFlagForKlineSocket;
                    break;
                case SocketKey.DELIVERY_TIME_CHANGED://交割日期
                case SocketKey.POSITION_LINE://持仓
                    MarketTimeLineBean tempBean = gson.fromJson(jsonString, MarketTimeLineBean.class);
                    tempBean.setType(2);
                    //add decimal
                    HoldingEntity holdingEntity = getInstance().getHoldingEntity();
                    tempBean.setDecimal(holdingEntity == null ? 0 : holdingEntity.getDecimal());
                    getInstance().updateAppHoldingInfo(tempBean, reqType);
                    break;
                default://7最新成交价 8成交列表 15热门合约 1权益可用保证金率 等各页面自己处理
                    break;
            }
            //尽量不用try去catch问题。  如果必用的话  请处理相关异常
            if (observers == null || observers.isEmpty() || observers.get(reqType + "") == null) {
                return;
            }
            for (IWebSocketObserver each : observers.get(reqType + "")) {
                if (needNotify) {
                    each.updateWebSocket(reqType, jsonString, addtionEntity);
                }
            }
            needNotify = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean needExceDuplicate(int reqType) {
        switch (reqType) {
            case SocketKey.MineEntrustReqType_CONTRACT:
            case SocketKey.MineEntrustReqType:
            case SocketKey.TradeSuccessNotiification:
                return false;
            default:
                return true;
        }
    }

//    private void dealHoldingAndLimit(MarketTimeLineBean bean) {
//        getInstance().updateAppHoldingInfo(bean);
//    }

    /**
     * @param gson
     * @param jsonString 处理 card的推送
     */
    private void dealCardJson(Gson gson, String jsonString) {
        JsonArray cardJsonArray = new JsonParser().parse(jsonString).getAsJsonArray();
        List<MarketCardItemBean> cards = new ArrayList<>();
        for (final JsonElement elem : cardJsonArray) {
            //循环遍历把对象添加到集合
            cards.add(gson.fromJson(elem, MarketCardItemBean.class));
        }
        for (MarketCardItemBean each : cards) {
            FutureItemEntity future = new FutureItemEntity(each.getName());
            future.setLastPrice(each.getLastPrice());
            future.setTrend(each.getGain());
            future.setUscPrice(each.getUscPrice());
//                        future.setFavorite(each.isCollect());
            future.setHot(each.isFire());
            future.setEntityId(each.getId());
            future.setEntityType(each.getType());
            future.setVolume(each.getTotalVolume());
            future.setAssetName(each.getAssetName());
            future.setContractType(each.getContractType());
            List<ChartLineEntity> datas = future.getDatas();
            if (each.getLine() != null) {
                for (int i = 0; i < each.getLine().size(); i++) {
                    ChartLineEntity entity = each.getLine().get(i);
                    datas.add(entity);
                }
            }

            List<FutureItemEntity> marketCards = getInstance().getMarketsCardsList();
            boolean cacheContainsFuture = false;
            for (FutureItemEntity cacheItem : marketCards) {
                if (cacheItem.getEntityType() != 2) {
                    if (cacheItem.getFutureName().equals(future.getFutureName())) {
                        setCacheItem(future, cacheItem);
                        break;
                    }
                } else if (cacheItem.getAssetName().equals(future.getAssetName()) && cacheItem.getContractType() == future.getContractType()) {
                    setCacheItem(future, cacheItem);
                    break;
                }
            }
        }
    }

    private void setCacheItem(FutureItemEntity future, FutureItemEntity cacheItem) {
        boolean cacheContainsFuture;
        if (!TextUtils.isEmpty(future.getLastPriceOrigin())) {
            cacheItem.setLastPrice(future.getLastPrice());
            cacheItem.setTrend(future.getTrend());
        }
        if(!TextUtils.isEmpty(future.getUscPrice())) {
            cacheItem.setUscPrice(future.getUscPrice());
        }
        if(!StringUtils.isEmpty(future.getVolumeOrigin())) {
            cacheItem.setVolume(future.getVolume());
        }
        cacheItem.setFutureName(future.getFutureName());
        cacheItem.setEntityId(future.getEntityId());
        cacheItem.setEntityType(future.getEntityType());
        cacheContainsFuture = true;
        if (cacheItem.getDatas() == null || cacheItem.getDatas().size() <= 0) {
            //出现此种情况多半是出现 数据问题了，不能直接add
//                                    marketCards.add(marketCards.indexOf(cacheItem), future);
            return;
        }
        updateApplicationCardCache(cacheItem, future);
    }

    /**
     * @param kLineBean
     */
    private void dealKlinePush(MarketKLineBean kLineBean) {
        Map<String, List<ChartLineEntity>> caches = getInstance().getMarketsKlinesMap();
        if (kLineBean == null) {
            return;
        }
        int type = kLineBean.getType();
        List<ChartLineEntity> datas = new ArrayList<>();
        List<ChartLineEntity> spots = new ArrayList<>();

        String tmpTypeAndId = type + "-" + kLineBean.getId();
        if (kLineBean.getResolution() == this.resolution && tmpTypeAndId.equals(typeAndId)) {
            BeanChangeFactory.iterateSocketDataList(datas, spots, type, kLineBean.getLine());
        }
        updateAppMarketsCache(type + "", datas, caches, 501);
        if (type == 2) {
            updateAppMarketsCache("1", spots, caches, 501);
        }
    }

    /**
     * @param timelineBean 处理 timeline数据
     */
    private void dealTimelinePush(MarketTimeLineBean timelineBean) {
        Map<String, List<ChartLineEntity>> caches = getInstance().getMarketsTimesMap();
        if (timelineBean == null) {
            return;
        }
        int type = timelineBean.getType();
        List<ChartLineEntity> datas = new ArrayList<>();
        List<ChartLineEntity> spots = new ArrayList<>();

        String tmpTypeAndId = type + "-" + timelineBean.getId();
        if(tmpTypeAndId.equals(typeAndId)) {
            BeanChangeFactory.iterateSocketDataList(datas, spots, type, timelineBean.getLine());
        }
        //必须放在处理前--1009更新，如果前面已经判断过推送json是否跟之前的一致，就不用走此逻辑
//        checkIsNeedNotify(type, datas, spots, caches);
        if (type == 2 || type == 3) {
            updateAppMarketsCache(type + "", datas, caches, 97);
        }
        updateAppMarketsCache("1", spots, caches, 97);
    }

    private void checkIsNeedNotify(int type, List<ChartLineEntity> datas, List<ChartLineEntity> spots, Map<String, List<ChartLineEntity>> caches) {
        List<ChartLineEntity> cacheList = caches.get(type + "");
        if (cacheList == null || cacheList.size() == 0) {
            return;
        }
        //先看data是否一样，不一样，可以直接推送了
        if (datas != null && datas.size() > 0) {
            if (!datas.get(datas.size() - 1).equals(cacheList.get(cacheList.size() - 1)))
                return;
        }
        //走到这里说明，data的值是一样的，或者是datas是null
        if (type != 2) {
            needNotify = false;
        } else {
            List<ChartLineEntity> spotCacheList = caches.get("1");
            if (spotCacheList == null || spotCacheList.size() == 0) {
                return;
            }
            if (spots != null && spots.size() > 0) {
                if (spots.get(spots.size() - 1).equals(spotCacheList.get(spotCacheList.size() - 1)))
                    needNotify = false;
            }
        }

    }

    /**
     * @param type
     * @param datas  用于更新cache的数据列表
     * @param caches map对，可能包含了数据列表
     * @param max
     */
    private void updateAppMarketsCache(String type, List<ChartLineEntity> datas, Map<String, List<ChartLineEntity>> caches, int max) {
        if (datas == null || datas.size() <= 0) {
            return;
        }

        if (caches.get(type) == null) {
            caches.put(type, datas);
        } else {
            List<ChartLineEntity> updateDatas = caches.get(type);
            int length = updateDatas.size();
            if (length == 0) {
                return;
            }
            for (ChartLineEntity chartLineEntity : datas) {
                if (chartLineEntity == null || updateDatas.get(length - 1) == null) {
                    continue;
                }
                //一样就不处理了
                if (chartLineEntity.equals(updateDatas.get(length - 1))) {
                    continue;
                }
                if (chartLineEntity.getTime() == updateDatas.get(length - 1).getTime()) {
                    if (chartLineEntity.getOpen() == 0 && chartLineEntity.getClose() == 0 && chartLineEntity.getVolume() == 0) {
                        continue;
                    }
                    ChartLineEntity updateEntity = updateDatas.get(length - 1);
                    updateEntity.setClose(chartLineEntity.getClose());
                    updateEntity.setAmount(chartLineEntity.getAmount());
                    updateEntity.setOpen(chartLineEntity.getOpen());
                    updateEntity.setHigh(chartLineEntity.getHigh());
                    updateEntity.setLow(chartLineEntity.getLow());
                    updateEntity.setVolume(chartLineEntity.getVolume());
                } else if (chartLineEntity.getTime() > updateDatas.get(length - 1).getTime()) {
                    if (chartLineEntity.getOpen() == 0 && chartLineEntity.getClose() == 0 && chartLineEntity.getVolume() == 0) {
                        double value = 0;
                        if (updateDatas.size() > 0) {
                            value = updateDatas.get(length - 1).getClose();
                        }
                        chartLineEntity.setOpen(value);
                        chartLineEntity.setClose(value);
                        chartLineEntity.setHigh(value);
                        chartLineEntity.setLow(value);
                    }
                    updateDatas.add(chartLineEntity);
                    isAddFlagForKlineSocket = "add";
                }
            }

            length = updateDatas.size();
            if (length > max) {
                int step = length - max;
                for (int i = 0; i < step; i++) {
                    updateDatas.remove(0);
                }
            }
        }
    }

    /**
     * @param updateItem
     * @param future     更新 application的缓存，保证每个品种
     *                   指数、合约1、合约2、合约3 usdt兑换
     *                   每个都最多只有50条最新的数据
     */
    private void updateApplicationCardCache(FutureItemEntity updateItem, FutureItemEntity future) {
        int length = updateItem.getDatas().size();
        for (ChartLineEntity chartLineEntity : future.getDatas()) {
            if (chartLineEntity.getTime() == updateItem.getDatas().get(length - 1).getTime()) {
                ChartLineEntity updateEntity = updateItem.getDatas().get(length - 1);
                updateEntity.setClose(chartLineEntity.getClose());
                updateEntity.setVolume(chartLineEntity.getVolume());
            } else if (chartLineEntity.getTime() > updateItem.getDatas().get(length - 1).getTime()) {
                //补点
                if (chartLineEntity.getOpen() == 0 && chartLineEntity.getClose() == 0 && chartLineEntity.getVolume() == 0) {
                    double value = 0;
                    if (updateItem.getDatas().size() > 0) {
                        value = updateItem.getDatas().get(length - 1).getClose();
                    }
                    chartLineEntity.setOpen(value);
                    chartLineEntity.setClose(value);
                    chartLineEntity.setHigh(value);
                    chartLineEntity.setLow(value);
                }
                updateItem.getDatas().add(chartLineEntity);
            }
        }

        int newlength = updateItem.getDatas().size();
        if (newlength > 49) {
            int step = newlength - 49;
            for (int i = 0; i < step; i++) {
                updateItem.getDatas().remove(0);
            }
        }
    }

    //服务端没有做去重处理，自己过滤吧
    private Map<String, String> responseMap = new HashMap<>();

    private WebSocket webSocket;

    boolean connected = false;

    //k线推过来的数据是添加还是更新
    private String isAddFlagForKlineSocket;

    private int reconnect = 0;
    //k线数据推送的时候，需要根据resolution过滤
    private int resolution;
    //需要容错服务端数据，记录k和分时的推送type-id，核对正常数据，过滤错误数据
    private String typeAndId;
    //如果分时推送数据没变化，那么needNotity为false
    private boolean needNotify = true;

    public void openWebSocket() {
        WebSocketListener webSocketListener = new WebSocketListener() {
            @Override
            public void onOpen(final WebSocket webSocket, Response response) {
                //保存引用，用于后续操作
                WebSocketClient.this.webSocket = webSocket;
                setConnected(true);
                //回写给客户端
                //建立连接成功后，服务器端注册
                //首次链接，temp observers为null，如果是重连，那么通知observers重新刷新，重新发送消息
                //利用tempObservers,用完之后重置
                if(tempObservers != null && !tempObservers.isEmpty()) {
                    for(String key : tempObservers.keySet()) {
                        List<IWebSocketObserver> list = tempObservers.get(key);
                        if(list != null) {
                            for (IWebSocketObserver each : list) {
                                BasePresenter presenter = (BasePresenter)each;
                                presenter.onRefresh();
                            }
                        }
                    }
                    tempObservers.clear();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                notifyObervers(text);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                LogUtils.e("wsocket_onClosed", code, reason);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                LogUtils.e("wsocket_onClosing", code, reason);
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                LogUtils.e("wsocket_onFaulure", t.getMessage() + "----" + t.toString());
                super.onFailure(webSocket, t, response);
                disConnected();
            }

        };
        WebSocketUtils.doWebSocket(webSocketListener);
    }

    /**
     * s发送消息
     */
    private void send(final WebSocketEntity entity, IWebSocketObserver observer) {
        try {
            String json = GsonSinglon.getInstance().toJson(entity);
            Log.i("wsocket_send", json);
            webSocket.send(json);
        } catch (Exception e) {
            e.printStackTrace();
            //如果是订阅类的发送失败，要从oberservers去除
            if(entity.handleType != 3) {
                removeObserver(entity.reqType + "", observer);
            }
            if (e instanceof NullPointerException) {
                reconnect = 5;
                reConnect(false);

//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        L.e("ws send error", connected + "");
//                        if (isConnected()) {
//                            reSend(entity);
//                        }
//                    }
//                }, 10);
            }
        }
    }

    private void reSend(final WebSocketEntity entity) {
        try {
            webSocket.send(GsonSinglon.getInstance().toJson(entity));
        } catch (Exception e) {
            e.printStackTrace();
//            L.e("ws resend error", e.getMessage() + "");
        }
    }

    /**
     * 订阅频道
     *
     * @param
     */
    @Override
    public void addChannel(WebSocketEntity webSocketEntity, IWebSocketObserver observer) {
        int requestType = webSocketEntity.reqType;
        webSocketEntity.setIsSubscribe(1);
        //添加 2 和 4之后，如果2和4的情况，不需要拦截
        if (!registerObserver(requestType + "", observer) && webSocketEntity.handleType == 1) {
            return;
        }
        if (UserLoginUtil.getToken() != null) {
            webSocketEntity.setToken(UserLoginUtil.getToken());
        }
        //add resolution
        if (requestType == SocketKey.HangQingKlinePushReqType && webSocketEntity.getParam() instanceof SocketMarketParam) {
            SocketMarketParam param = (SocketMarketParam) webSocketEntity.getParam();
            this.resolution = Integer.parseInt(param.getResolution());
            this.typeAndId = param.getType() + "-" + param.getId();
        } else if(requestType == SocketKey.HangQingFenShiTuZheXianTuReqType && webSocketEntity.getParam() instanceof SocketMarketParam) {
            SocketMarketParam param = (SocketMarketParam) webSocketEntity.getParam();
            this.typeAndId = param.getType() + "-" + param.getId();
        }
        send(webSocketEntity, observer);
    }

    @Override
    public void removeChannel(int requestType, IWebSocketObserver observer) {
        removeChannel(requestType, observer, null);
    }

    @Override
    public void removeChannel(int requestType, IWebSocketObserver o, Object params) {
        if (!removeObserver(requestType + "", o))
            return;
        WebSocketEntity webSocketEntity = new WebSocketEntity();
        webSocketEntity.setIsSubscribe(3);
        if (params != null) {
            webSocketEntity.setParam(params);
        }
        webSocketEntity.setReqType(requestType);
        send(webSocketEntity, o);
    }

    public void setConnected(boolean connected) {
        reconnect = 0;
        this.connected = connected;
    }

    public void clearWebSocket(boolean needReconnect) {
        try {
            if (webSocket != null) {
                webSocket.cancel();
                webSocket.close(1000, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(needReconnect)
            resetAndConnect(true);
        else {//主动断开链接,进入期权的时候
            webSocket = null;
            this.connected = false;
        }
    }

    /**
     * @param isChangeIp
     * 如果是在测试环境，切换了IP，那么不需要缓存观察者
     */
    private void resetAndConnect(boolean isChangeIp) {
        webSocket = null;
        this.connected = false;

        reConnect(isChangeIp);
    }

    private void disConnected() {
        try {
            if (webSocket != null) {
                webSocket.cancel();
                webSocket.close(1002, "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resetAndConnect(false);
    }

    private void reConnect(final boolean isChangeIp) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isChangeIp && !observers.isEmpty()) {
                    tempObservers.putAll(observers);
                }
                //jiang 1130 断开连接，清空observers
                observers.clear();
                responseMap.clear();
                if (webSocket == null && !isConnected() && reconnect <= 5) {
                    L.v("fota ws reconnect", "fota ws reconnect");
                    reconnect++;
                    openWebSocket();
                }
            }
        }, 1000);
    }

    public boolean isConnected() {
        return connected;
    }

    public void resetReconnect() {
        this.reconnect = 0;
    }

    public void tryReconnect() {
        if(!isConnected() && webSocket == null) {
            openWebSocket();
        }
    }

}
