package com.fota.android.moudles.market;


import androidx.annotation.NonNull;

import com.fota.android.app.FotaApplication;
import com.fota.android.app.SocketKey;
import com.fota.android.common.bean.BeanChangeFactory;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.Http;
import com.fota.android.moudles.market.bean.ChartLineEntity;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.fota.android.moudles.market.bean.MarketKLineBean;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;
import com.fota.android.socket.params.SocketFutureParam;
import com.fota.android.socket.params.SocketMarketParam;

import java.util.ArrayList;
import java.util.List;

import static com.fota.android.app.FotaApplication.getInstance;

/**
 * Created by jiang on 2018/08/14.
 */

public class FullScreenKlinePresenter extends BasePresenter<FullScreenKlineViewInterface> {

    private String period;
    private FutureItemEntity futureBean;

    public void setBean(FutureItemEntity bean) {
        this.futureBean = bean;
    }

    public FullScreenKlinePresenter(FullScreenKlineViewInterface view) {
        super(view);
    }

    @Override
    public void detachView() {
        super.detachView();
        //k线
        client.removeChannel(SocketKey.HangQingKlinePushReqType, this);
        client.removeChannel(SocketKey.DELIVERY_TIME_CHANGED, this);
        client.removeChannel(SocketKey.POSITION_LINE, this);
    }

    public void getFullScreenKlineDatas(final int type, final int id, final String period) {
        SocketMarketParam param = new SocketMarketParam(id, type, periodTypeSwitch(period));
        client.removeChannel(SocketKey.HangQingKlinePushReqType, FullScreenKlinePresenter.this, param);
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
                            WebSocketEntity<SocketMarketParam> socketEntity = new WebSocketEntity<>();
                            socketEntity.setParam(new SocketMarketParam(id, type, periodTypeSwitch(period)));
                            if(bean.getType() ==2) {
                                socketEntity.getParam().setAssetName(bean.getAssetName());
                                socketEntity.getParam().setContractType(bean.getContractType());
                            }
                            socketEntity.setReqType(SocketKey.HangQingKlinePushReqType);
                            client.addChannel(socketEntity, FullScreenKlinePresenter.this);
                            if(bean.getType() == 2) {
                                //交割日期
                                WebSocketEntity<SocketFutureParam> socketEntity1 = new WebSocketEntity<>();
                                socketEntity1.setParam(new SocketFutureParam(bean.getContractType(), bean.getAssetName()));
                                socketEntity1.setReqType(SocketKey.DELIVERY_TIME_CHANGED);
                                client.addChannel(socketEntity1, FullScreenKlinePresenter.this);
                                //个人持仓 如果登录的话
                                WebSocketEntity<SocketFutureParam> socketEntity2 = new WebSocketEntity<>();
                                socketEntity2.setParam(new SocketFutureParam(bean.getContractType(), bean.getAssetName()));
                                socketEntity2.setReqType(SocketKey.POSITION_LINE);
                                client.addChannel(socketEntity2, FullScreenKlinePresenter.this);
                            }

                            List<ChartLineEntity> datas = new ArrayList<>();
                            List<ChartLineEntity> spots = new ArrayList<>();

                            BeanChangeFactory.iterateKDataList(datas, spots, type, bean.getLine());
                            FotaApplication.getInstance().resetAppKLineData(type, datas, spots);
                            getView().resetFreshKlineData();
                            getView().setOverShowLoading(false);
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        if(getView() != null) {
                            getView().setOverShowLoading(false);
                            getView().onNoDataCallBack();
                        }
                    }
                });
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

    @Override
    public void onUpdateImplSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity) {
        if(getView() != null) {
            if(reqType == SocketKey.HangQingKlinePushReqType) {
                getView().onRefreshKlineData("add".equals(jsonString)?true:false);
            } else if(reqType == SocketKey.DELIVERY_TIME_CHANGED || reqType == SocketKey.POSITION_LINE) {
                if(futureBean.getEntityType() == 2) {
                    getView().onRefreshDeliveryOrHold();
                }
            }
        }
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
}
