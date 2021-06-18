package com.fota.android.moudles.common;


import android.util.Log;

import androidx.annotation.NonNull;

import com.fota.android.app.FotaApplication;
import com.fota.android.app.SocketKey;
import com.fota.android.common.bean.BeanChangeFactory;
import com.fota.android.common.bean.exchange.CurrentPriceBean;
import com.fota.android.common.bean.home.DepthBean;
import com.fota.android.common.bean.home.EntrustBean;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.Http;
import com.fota.android.moudles.market.bean.ChartLineEntity;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.fota.android.moudles.market.bean.MarketKLineBean;
import com.fota.android.moudles.market.bean.MarketTimeLineBean;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;
import com.fota.android.socket.params.SocketBaseParam;
import com.fota.android.socket.params.SocketEntrustParam;
import com.fota.android.socket.params.SocketFutureParam;
import com.fota.android.socket.params.SocketMarketParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.fota.android.app.FotaApplication.getInstance;

/**
 * @param <T> kline exchange future
 *            Fragment 共同使用的基类
 */
public class BaseTradePresenter<T extends BaseTradeViewInterface> extends BasePresenter {
    private String period;
    private int type;
    private int entityId;

    public void setBasePrecision(String basePrecision) {
        this.basePrecision = basePrecision;
    }

    private String basePrecision = "-1";

    public BaseTradePresenter(T view) {
        super(view);
    }

    @Override
    public T getView() {
        return (T) super.getView();
    }

    /**
     * 获取五档盘口
     * entityType      1 spot 2 future 3 usdt
     *
     * @param precision 精度
     */
    public void getDepthFive(final int type, final int entityId, final String precision) {
        this.type = type;
        this.entityId = entityId;
        //委托 http获取到实时委托之后开始订阅实时委托推送
        WebSocketEntity<SocketEntrustParam> socketEntity = new WebSocketEntity<>();
        SocketEntrustParam socketEntrustParam = null;

        if(!"-1".equals(precision)) {
            socketEntrustParam = new SocketEntrustParam(type, entityId, precision);
        } else {
            socketEntrustParam = new SocketEntrustParam(type, entityId);
        }

        socketEntity.setParam(socketEntrustParam);
        socketEntity.setReqType(SocketKey.TradeWeiTuoReqType);
        socketEntity.setHandleType(2);
        client.addChannel(socketEntity, BaseTradePresenter.this);
    }

    protected void onNextDepth(DepthBean bean) {
        if(getView() == null) {
            return;
        }
        if (bean == null) {
            getView().onRefreshDepth(null, null, null, 0f);
            return;
        }

        Map depthMap = FotaApplication.getInstance().getDepthMap();
        String key = type + "-" + entityId;
        depthMap.put(key, bean);

        List<EntrustBean> limitSells = BeanChangeFactory.getSellEntrustBeans(bean.getAsks(), 5);
        List<EntrustBean> limitBuys = BeanChangeFactory.getEntrustBeans(bean.getBids(), 5);
        if(limitSells != null) {
            Collections.sort(limitSells);
            Collections.reverse(limitSells);
        }
        if(limitBuys != null) {
            Collections.sort(limitBuys);
        }
        if (getView() != null) {
            if("-1".equals(basePrecision)) {
                getView().onRefreshDepth(limitBuys, limitSells, bean.getPricisionList() == null ? null : bean.getPricisionList(), bean.getValuation());
            } else {
                getView().onRefreshDepth(limitBuys, limitSells, null, bean.getValuation());
            }
        }
    }

    public void getDepthFive(final int type, final int entityId) {
        getDepthFive(type, entityId, basePrecision);
//        getDepthFive(type, entityId, "0.1");
    }

    public void changeDigitalChannel(String remove, String add) {
        client.removeChannel(SocketKey.TradeWeiTuoReqType, BaseTradePresenter.this, new SocketEntrustParam(type, entityId, remove));
        client.removeChannel(SocketKey.HangQingNewlyPriceReqType, BaseTradePresenter.this, new SocketEntrustParam(type, entityId, remove));

//        WebSocketEntity<SocketEntrustParam> socketEntity = new WebSocketEntity<>();
//        socketEntity.setParam(new SocketEntrustParam(type, entityId, add + ""));
//        socketEntity.setReqType(3);
//        client.addChannel(socketEntity, BaseTradePresenter.this);
        basePrecision = add;
        getDepthFive(type, entityId, add);
        getNowTicker(type, entityId, add);
    }

    public void getNowTicker(final int type, final int id) {
        getNowTicker(type, id, basePrecision);
    }

    /**
     * 当前成交价
     */
    public void getNowTicker(final int type, final int id, final String precision) {
        SocketBaseParam param = new SocketBaseParam(type, id);
        client.removeChannel(SocketKey.HangQingNewlyPriceReqType, BaseTradePresenter.this, param);

        WebSocketEntity<SocketEntrustParam> socketEntity = new WebSocketEntity<>();
        //3 usdk兑换 2 合约
        SocketEntrustParam socketParam = new SocketEntrustParam(type, id);
        if(!"-1".equals(precision)) {
            socketParam.setParam(precision);
        }
        socketEntity.setParam(socketParam);
        socketEntity.setReqType(SocketKey.HangQingNewlyPriceReqType);
        client.addChannel(socketEntity, BaseTradePresenter.this);
    }

    protected void onNextTicker(CurrentPriceBean map) {
        if (getView() != null) {
            getView().onRefreshTicker(map);
        }
    }


    public void getTimeLineDatas(final int type, final int id, final String period) {
        this.type = type;
        this.entityId = id;
        SocketMarketParam param = new SocketMarketParam(id, type, periodTypeSwitch(period));
        client.removeChannel(SocketKey.HangQingFenShiTuZheXianTuReqType, BaseTradePresenter.this, param);
        //jiang 1227 fix
        if(type == 2) {
            client.removeChannel(SocketKey.DELIVERY_TIME_CHANGED, BaseTradePresenter.this);
            client.removeChannel(SocketKey.POSITION_LINE, BaseTradePresenter.this);
        }
        final long currentTime = System.currentTimeMillis();
        String endTime = currentTime + "";
        String startTime = fetchSincTime(96, period, currentTime);
        Http.getMarketService().getTimeLineDatas(createPageParam(type, id, startTime, endTime,  periodTypeSwitch(period)))
                .compose(new CommonTransformer<MarketTimeLineBean>())
                .subscribe(new CommonSubscriber<MarketTimeLineBean>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(MarketTimeLineBean bean) {
                        if (getView() != null) {
                            // http之后开始订阅推送
                            socketSubsribe(bean, period);

                            FutureItemEntity future = new FutureItemEntity(bean.getName());
                            future.setEntityType(bean.getType());
                            future.setEntityId(bean.getId());
                            List<ChartLineEntity> datas = future.getDatas();
                            List<ChartLineEntity> spots = new ArrayList<>();

                            BeanChangeFactory.iterateKDataList(datas, spots, type, bean.getLine());

                            getInstance().resetAppTimeLineData(type, datas, spots);
                            getInstance().updateAppHoldingInfo(bean, 0);
                            getView().resetFreshTimelineData();
                            getView().setOverShowLoading(0, false);
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        if(getView() != null) {
                            getView().setOverShowLoading(0, false);
                            getView().onNoDataCallBack(0);
                        }
                    }
                });
    }

    //http之后开始订阅推送
    private void socketSubsribe(MarketTimeLineBean bean, String periodIn) {
        //time line 推送
        WebSocketEntity<SocketMarketParam> socketEntity = new WebSocketEntity<>();
        socketEntity.setParam(new SocketMarketParam(bean.getId(), type, periodTypeSwitch(periodIn)));
        if(bean.getType() ==2) {
            socketEntity.getParam().setAssetName(bean.getAssetName());
            socketEntity.getParam().setContractType(bean.getContractType());
        }
        socketEntity.setHandleType(2);
        socketEntity.setReqType(SocketKey.HangQingFenShiTuZheXianTuReqType);

        Log.i("nidongliang", "socket entity: " + socketEntity);
        client.addChannel(socketEntity, BaseTradePresenter.this);
        if(bean.getType() == 2) {
            //交割日期
            WebSocketEntity<SocketFutureParam> socketEntity1 = new WebSocketEntity<>();
            socketEntity1.setParam(new SocketFutureParam(bean.getContractType(), bean.getAssetName()));
            socketEntity1.setReqType(SocketKey.DELIVERY_TIME_CHANGED);
            client.addChannel(socketEntity1, BaseTradePresenter.this);
            //个人持仓 如果登录的话
            WebSocketEntity<SocketFutureParam> socketEntity2 = new WebSocketEntity<>();
            socketEntity2.setParam(new SocketFutureParam(bean.getContractType(), bean.getAssetName()));
            socketEntity2.setReqType(SocketKey.POSITION_LINE);
            client.addChannel(socketEntity2, BaseTradePresenter.this);
        }
    }

    public void getKlineDatas(final int type, final int id, final String period) {
        SocketMarketParam param = new SocketMarketParam(id, type, periodTypeSwitch(period));
        client.removeChannel(SocketKey.HangQingKlinePushReqType, BaseTradePresenter.this, param);
        //clear first
        getInstance().resetAppKLineData(type, null, null);

        final long currentTime = System.currentTimeMillis();
        final String endTime = currentTime + "";
        String startTime = fetchSincTime(500, period, currentTime);
        Http.getMarketService().getKlineDatas(createPageParam(type, id, startTime, endTime, this.period))
                .compose(new CommonTransformer<MarketKLineBean>())
                .subscribe(new CommonSubscriber<MarketKLineBean>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(MarketKLineBean bean) {
                        if (getView() != null) {
                            //委托 http之后开始订阅推送
                            //todo jiang transent comment
                            WebSocketEntity<SocketMarketParam> socketEntity = new WebSocketEntity<>();
                            socketEntity.setParam(new SocketMarketParam(id, type, periodTypeSwitch(period)));
                            if(bean.getType() ==2) {
                                socketEntity.getParam().setAssetName(bean.getAssetName());
                                socketEntity.getParam().setContractType(bean.getContractType());
                            }
                            socketEntity.setReqType(SocketKey.HangQingKlinePushReqType);
                            client.addChannel(socketEntity, BaseTradePresenter.this);

                            List<ChartLineEntity> datas = new ArrayList<>();
                            List<ChartLineEntity> spots = new ArrayList<>();

                            BeanChangeFactory.iterateKDataList(datas, spots, type, bean.getLine());

                            getInstance().resetAppKLineData(type, datas, spots);
//                            getInstance().updateAppHoldingInfo(bean);
                            getView().resetFreshKlineData();
                            getView().setOverShowLoading(1, false);
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        if(getView() != null) {
                            getView().setOverShowLoading(1, false);
                            getView().onNoDataCallBack(1);
                        }
                    }
                });
    }

    private String fetchSincTime(int max, String type, long currentTime) {
        String result = "0";

        switch (type) {
            case "1m"://1min
                result = (currentTime - max * 60 * 1000) + "";
                period = 1 + "";
                break;
//            case "5m"://5min
//                result = (currentTime - max * 5 * 60 * 1000) + "";
//                period = 5 + "";
//                break;
            case "15m"://15min
                result = (currentTime - max * 15 * 60 * 1000) + "";
                period = 2 + "";
                break;
//            case "30m"://30min
//                result = (currentTime - max * 30 * 60 * 1000) + "";
//                period = 30 + "";
//                break;
            case "1h"://1hour
                result = (currentTime - max * 60 * 60 * 1000) + "";
                period = 3 + "";
                break;
            case "1d"://day
                long temp = max * 24 * 60 * 60 * 1000L;
                result = (currentTime - temp) + "";
                period = "4";
                break;
            case "5m"://5min
                result = (currentTime - max * 5 * 60 * 1000) + "";
                period = "5";
                break;
            case "30m"://30min
                result = (currentTime - max * 1800000L) + "";
                period = "6";
                break;
            case "4h"://4hour
                result = (currentTime - max * 14400000L) + "";
                period = "7";
                break;
            case "6h"://6hour
                result = (currentTime - max * 21600000L) + "";
                period = "8";
                break;
            case "1w"://1week
                result = (currentTime - max * 604800000L) + "";
                period = "9";
                break;
            default://
                break;
        }

        return result;
    }

    @NonNull
    private BtbMap createPageParam(int type, int id, String startTime, String endTime, String resolution) {
        BtbMap paramsMap = new BtbMap();
        paramsMap.put("resolution", resolution);
        paramsMap.put("id", id + "");
        paramsMap.put("startTime", startTime);
        paramsMap.put("endTime", endTime);
        paramsMap.put("type", type + "");
        return paramsMap;
    }

    private String periodTypeSwitch(String type) {
        String result = "0";

        switch (type) {
            case "1m"://1min
                result = "1";
                break;
//            case "5m"://5min
//                result = "5";
//                break;
            case "15m"://15min
                result = "2";
                break;
//            case "30m"://30min
//                result = "30";
//                break;
            case "1h"://1hour
                result = "3";
                break;
            case "1d"://day
                result = "4";
                break;
            case "5m"://5min
                result = "5";
                break;
            case "30m"://30min
                result = "6";
                break;
            case "4h"://4hour
                result = "7";
                break;
            case "6h"://6hour
                result = "8";
                break;
            case "1w"://1week
                result = "9";
                break;
            default://
                break;
        }

        return result;
    }

    public boolean isSocketChaos(int reqType, String jsonString, SocketAdditionEntity additionEntity) {
        boolean result = false;
        switch (reqType) {
            case SocketKey.TradeWeiTuoReqType://盘口
            case SocketKey.HangQingNewlyPriceReqType://最新价
                SocketEntrustParam param = (SocketEntrustParam) additionEntity.getParam();
                if(type != param.getType() || entityId != param.getId() || isPrecisionNotEqual(param.getParam())) {
                    result = true;
                }
                break;
            case SocketKey.HangQingFenShiTuZheXianTuReqType://分时
                break;
            case SocketKey.HangQingKlinePushReqType://k线
                break;
        }
        return result;
    }

    private boolean isPrecisionNotEqual(String precision) {
        if(precision == null || "".equals(precision)) {
            return !"-1".equals(basePrecision);
        } else {
            return !precision.equals(basePrecision);
        }
    }
}
