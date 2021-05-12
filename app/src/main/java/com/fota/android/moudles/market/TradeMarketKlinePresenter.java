package com.fota.android.moudles.market;

import android.text.TextUtils;

import com.fota.android.app.FotaApplication;
import com.fota.android.app.GsonSinglon;
import com.fota.android.app.SocketKey;
import com.fota.android.common.bean.exchange.CurrentPriceBean;
import com.fota.android.common.bean.home.DealBean;
import com.fota.android.common.bean.home.DepthBean;
import com.fota.android.moudles.common.BaseTradePresenter;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.fota.android.moudles.market.bean.MarketIndexBean;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;
import com.fota.android.socket.params.SocketBaseParam;
import com.fota.android.socket.params.SocketEntrustParam;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fota.android.app.SocketKey.DELIVERY_TIME_CHANGED;
import static com.fota.android.app.SocketKey.HangQingFenShiTuZheXianTuReqType;
import static com.fota.android.app.SocketKey.HangQingKlinePushReqType;
import static com.fota.android.app.SocketKey.HangQingNewlyPriceReqType;
import static com.fota.android.app.SocketKey.HangQingTradeDetailReqType;
import static com.fota.android.app.SocketKey.MARKET_SPOTINDEX;
import static com.fota.android.app.SocketKey.POSITION_LINE;
import static com.fota.android.app.SocketKey.TradeWeiTuoReqType;

/**
 * Created by jiang on 2018/08/02.
 */

public class TradeMarketKlinePresenter extends BaseTradePresenter<TradeMarketKlineViewInterface> {
    private int dealNums;

    public void setDealNums(int dealNums) {
        this.dealNums = dealNums;
    }

    private FutureItemEntity futureBean;

    public void setBean(FutureItemEntity bean) {
        this.futureBean = bean;
    }

    public FutureItemEntity getFutureBean() {
        return futureBean;
    }

    public TradeMarketKlinePresenter(TradeMarketKlineViewInterface view) {
        super(view);
    }

    @Override
    public void finishView() {
        super.finishView();
        onHide();
    }

    @Override
    public void onHide() {
        //time线
        client.removeChannel(HangQingFenShiTuZheXianTuReqType, this);
        //委托
        client.removeChannel(TradeWeiTuoReqType, this);
        client.removeChannel(HangQingNewlyPriceReqType, this);
        client.removeChannel(HangQingTradeDetailReqType, this);
        client.removeChannel(HangQingKlinePushReqType, this);
        client.removeChannel(DELIVERY_TIME_CHANGED, this);
        client.removeChannel(POSITION_LINE, this);
        if(futureBean.getEntityType() == 1) {
            client.removeChannel(SocketKey.MARKET_SPOTINDEX, this);
        }
        //此页面销毁，清空 app的k线和分时数据，以免影响 exchange和trade Fragment界面
        Map map = FotaApplication.getInstance().getMarketsTimesMap();
        map.clear();
    }

    /**
     * 指数的deal数据请求
     */
    public void getAdditonalSpot() {
        WebSocketEntity<SocketEntrustParam> socketEntity = new WebSocketEntity<>();
        SocketEntrustParam param = new SocketEntrustParam(futureBean.getAssetName());
        socketEntity.setParam(param);
        socketEntity.setReqType(MARKET_SPOTINDEX);
        client.addChannel(socketEntity, TradeMarketKlinePresenter.this);
    }

    private void onNextDeal(List<DealBean> deals) {
        if (getView() != null) {
            if(deals == null) {
//                getView().onRefreshSpot(null);
                getView().onRefreshDeal(null);
                return;
            }
            List<DealBean> temp = new ArrayList<>();
            int length = deals.size();
            if(futureBean.getEntityType() == 1) {
//                if (length >= dealNums*2) {
//                    temp = deals.subList(0, dealNums);
//                    getView().onRefreshSpot(temp);
//                    temp = deals.subList(dealNums, dealNums*2);
//                    getView().onRefreshDeal(temp);
//                } else if(length >= dealNums && length <dealNums*2) {
//                    temp = deals.subList(0, dealNums);
//                    getView().onRefreshSpot(temp);
//                    temp = deals.subList(dealNums, length);
//                    getView().onRefreshDeal(temp);
//                } else {
//                    temp = deals;
//                    getView().onRefreshDeal(null);
//                    getView().onRefreshSpot(temp);
//                }
            } else {
                if (length >= dealNums) {
                    temp = deals.subList(0, dealNums);
                } else {
                    temp = deals;
                }
                getView().onRefreshDeal(temp);
            }
        }
    }

    /**
     * all deal
     */
    public void getDeal() {
        WebSocketEntity<SocketBaseParam> socketEntity = new WebSocketEntity<>();
        socketEntity.setParam(new SocketBaseParam(futureBean.getEntityType(), futureBean.getEntityId()));
        socketEntity.setReqType(HangQingTradeDetailReqType);
        client.addChannel(socketEntity, TradeMarketKlinePresenter.this);
    }

    @Override
    public void onUpdateImplSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity) {
        if (TextUtils.isEmpty(jsonString))
            return;
        if(getView() != null) {
            if(isSocketChaos(reqType, jsonString, additionEntity)) {
                return;
            }
            if(reqType == TradeWeiTuoReqType) {//depth
                Map<String, DepthBean> depthMap = FotaApplication.getInstance().getDepthMap();
                String key = futureBean.getEntityType() + "-" + futureBean.getEntityId();
                DepthBean bean = depthMap.get(key);
                onNextDepth(bean);
            } else if(reqType == HangQingFenShiTuZheXianTuReqType) {//timeline
                getView().onRefreshTimelineData();
            } else if(reqType == HangQingNewlyPriceReqType) {//new price
                CurrentPriceBean newPrice = GsonSinglon.getInstance().fromJson(jsonString, CurrentPriceBean.class);
                onNextTicker(newPrice);
            } else if(reqType == HangQingTradeDetailReqType) {//成交列表
                if(additionEntity.getParam() != null) {
                    SocketBaseParam param = (SocketBaseParam) additionEntity.getParam();
                    if (param.getId() != futureBean.getEntityId() || param.getType() != futureBean.getEntityType())
                        return;
                }
                JsonArray dealJsonArray = new JsonParser().parse(jsonString).getAsJsonArray();
                List<DealBean> deals = new ArrayList<>();
                for (final JsonElement elem : dealJsonArray) {
                    //循环遍历把对象添加到集合
                    deals.add(GsonSinglon.getInstance().fromJson(elem, DealBean.class));
                }
                if (dealJsonArray != null) {// 应该是增量吧
                    //todo jiang 目前先按全量处理
                    onNextDeal(deals);
                }
            } else if(reqType == HangQingKlinePushReqType) {//k line
                getView().onRefreshKlineData("add".equals(jsonString)?true:false);
            } else if(reqType == DELIVERY_TIME_CHANGED || reqType == POSITION_LINE) {
                if(futureBean.getEntityType() == 2) {
                    getView().onRefreshDeliveryOrHold();
                }
            } else if(reqType == MARKET_SPOTINDEX) {
                if(futureBean.getEntityType() == 1) {
                    MarketIndexBean indexBean = GsonSinglon.getInstance().fromJson(jsonString, MarketIndexBean.class);
                    getView().onRefreshSpot(indexBean);
                }
            }
        }
    }
}
