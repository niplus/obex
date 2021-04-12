package com.fota.android.moudles.exchange.index;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.ConstantsReq;
import com.fota.android.app.FotaApplication;
import com.fota.android.app.GsonSinglon;
import com.fota.android.app.SocketKey;
import com.fota.android.common.bean.exchange.CurrentPriceBean;
import com.fota.android.common.bean.exchange.ExchangeBody;
import com.fota.android.common.bean.exchange.ExchangeCurrency;
import com.fota.android.common.bean.exchange.ExchangeEntity;
import com.fota.android.common.bean.home.DepthBean;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.Http;
import com.fota.android.moudles.common.BaseTradePresenter;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;

import java.util.List;
import java.util.Map;

public class ExchangePresenter extends BaseTradePresenter<ExchangeTradeView> {
    protected static final String[] types = {"1m", "15m", "1h", "1d"};
    protected ExchangeEntity model;
    protected int currentPeriodIndex = 1;
    private String fromKey;

    public void setCurrentPeriodIndex(int currentPeriodIndex) {
        this.currentPeriodIndex = currentPeriodIndex;
    }

    public List<ExchangeCurrency> usdtList;
    //private int currencyIndex;

    public void setFromHq(String assetName) {
        fromKey = assetName;
    }

    /**
     * 从其他界面传入的币种
     */
    ExchangeCurrency selectItem;

    public ExchangePresenter(ExchangeTradeView view) {
        super(view);
        model = new ExchangeEntity();
    }

    @Override
    public void getExtras(Bundle bundle) {
        super.getExtras(bundle);
        if (bundle == null) {
            return;
        }
        if (bundle.containsKey(BundleKeys.MODEL)) {
            ExchangeCurrency selectItem = (ExchangeCurrency) bundle.getSerializable(BundleKeys.MODEL);
            if (selectItem != null) {
                setFromHq(selectItem.getAssetId());
            }
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        removeChannel();
    }

    @Override
    public void onHide() {
        super.onHide();
        removeChannel();
    }

    protected void removeChannel() {
        //委托
        client.removeChannel(SocketKey.TradeWeiTuoReqType, this);
        //time线
        client.removeChannel(SocketKey.HangQingFenShiTuZheXianTuReqType, this);
        //ticker
        client.removeChannel(SocketKey.HangQingNewlyPriceReqType, this);
        client.removeChannel(SocketKey.HangQingKlinePushReqType, this);
        removeChildren();
    }

    protected void removeChildren() {
        client.removeChannel(SocketKey.MineEntrustReqType_CONTRACT, this);
        client.removeChannel(SocketKey.MineAssetReqType, this);
    }


    /**
     * 依然要请求
     */
    public void reqUsdtList() {
        //在请求之前就有数据
        Http.getExchangeService().getUsdtList()
                .compose(new CommonTransformer<List<ExchangeCurrency>>())
                .subscribe(new CommonSubscriber<List<ExchangeCurrency>>() {
                    @Override
                    public void onNext(List<ExchangeCurrency> list) {
                        setUsdtList(list);
                        if (getView() != null) {
                            getView().refreshComplete();
                        }
                        //从传值过来的地方处理
                        if (fromKey != null) {
                            ExchangeCurrency parent = findAsset(fromKey, list);
                            if (parent != null) {
                                fromKey = null;
                                setSelectItem(parent);
                                return;
                            }
                        }

                        if (selectItem == null && Pub.isListExists(list)) {
                            setSelectItem(list.get(0));
                        }
                        //说明是从其他界面来 刷新界面 //jiang 0823 fanknows
                        if (selectItem != null) {
                            setSelectItem(selectItem);
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        if (getView() != null) {
                            getView().refreshComplete();
                        }
                    }
                });
    }


    /**
     * 确认下单
     *
     * @param fundCode
     */
    public void submit(String fundCode) {
        if (selectItem == null) {
            return;
        }
        model.setAssetId(String.valueOf(selectItem.getAssetId()));
        model.setAssetName(selectItem.getAssetName());
        ExchangeBody body = new ExchangeBody();
//        body.setTradeToken(fundCode);
        body.setObj(model);
        Http.getExchangeService().makeOrder(body)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {

                    @Override
                    public void onNext(BaseHttpEntity baseHttpEntity) {
                        if (getView() != null) {
                            getView().showTopInfo(getView().getXmlString(R.string.order_success));
                            getView().notifyFromPresenter(ExchangeFragment.ORDER_SUCCESS);
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        switch (e.code) {
                            //资金密码错误
                            case 101038:
                                if (getView() != null) {
                                    getView().notifyFromPresenter(ExchangeFragment.PASSWORD_TOKEN_ERROR);
                                }
                                break;
                            default:
                                super.onError(e);
                                break;
                        }
                    }

                });
    }

    public ExchangeEntity getModel() {
        return model;
    }

    public void setUsdtList(List<ExchangeCurrency> usdtList) {
        this.usdtList = usdtList;
    }

    public List<ExchangeCurrency> getUsdtList() {
        return usdtList;
    }


    public void setSelectItem(ExchangeCurrency selectItem) {
        //jiang 切换基准币种，需要重置精度
        if (!selectItem.equals(this.selectItem)) {
            setBasePrecision("-1");
        }
        this.selectItem = selectItem;
        updateCurrency();
    }

    /**
     * 更新数据
     */
    private void updateCurrency() {
        getView().refreshCurrency();
        //联动请求
        getDepthFive(getType(), Pub.GetInt(selectItem.getAssetId()));
        getNowTicker(getType(), Pub.GetInt(selectItem.getAssetId()));
        getTimeLineDatas(getType(), Pub.GetInt(selectItem.getAssetId()), "1m");
        //jiang chart loading
        if (getView() != null) {
            getView().setOverShowLoading(0, true);
            getView().setOverShowLoading(1, true);
            getKlineDatas(3, Pub.GetInt(selectItem.getAssetId()), types[currentPeriodIndex]);
        }
    }

    /**
     * Uset 兑换
     *
     * @return
     */
    protected int getType() {
        return ConstantsReq.TRADE_TYPE_EXCHAGE;
    }


    @Override
    public void onUpdateImplSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity) {
        if (getView() == null) {
            return;
        }
        if (isSocketChaos(reqType, jsonString, additionEntity)) {
            return;
        }
        switch (reqType) {
            case SocketKey.TradeWeiTuoReqType:
                if (selectItem == null) {
                    return;
                }
                Map<String, DepthBean> depthMap = FotaApplication.getInstance().getDepthMap();
                String key = "3-" + selectItem.getAssetId();
                DepthBean bean = depthMap.get(key);
                if (bean == null) {
                    return;
                }
                Log.i("onUpdateImplSocket", "jsonString: " + jsonString);
                onNextDepth(bean);
                break;
            case SocketKey.HangQingFenShiTuZheXianTuReqType:
                getView().onRefreshTimelineData();
                break;
            case SocketKey.HangQingNewlyPriceReqType:
                CurrentPriceBean newPrice = GsonSinglon.getInstance().fromJson(jsonString, CurrentPriceBean.class);
                onNextTicker(newPrice);
                break;
            case SocketKey.HangQingKlinePushReqType:
                getView().onRefreshKlineData("add".equals(jsonString) ? true : false);
                break;
            case SocketKey.TradeSuccessNotiification:
//                if (jsonString == null || getView() == null) {
//                    return;
//                }
//                ExchangeTopInfo model = GsonSinglon.getInstance().fromJson(jsonString, ExchangeTopInfo.class);
//                StringBuilder sb = new StringBuilder();
//                sb.append("  ");
//                sb.append(model.getAmount());
//                sb.append(" ");
//                sb.append(model.getSymbol());
//                sb.append(" ");
//                if (model.isBuy()) {
//                    //0-对手价，1-指定价
//                    sb.append(getView().getXmlString(R.string.common_weituo_buy));
//                } else {
//                    sb.append(getView().getXmlString(R.string.common_weituo_sell));
//                }
//                getView().showTopInfo(sb.toString());
                break;
        }

    }

    public ExchangeCurrency getSelectAssetItem() {
        return selectItem;
    }

    public void addTopInfoMessageLisenter() {
        //成交推送（导航栏）
        client.removeChannel(SocketKey.TradeSuccessNotiification, this);
        WebSocketEntity<BtbMap> socketEntity = new WebSocketEntity<>();
        socketEntity.setReqType(SocketKey.TradeSuccessNotiification);
        BtbMap map = new BtbMap();
        map.p("type", getType());
        socketEntity.setParam(map);
        client.addChannel(socketEntity, this);
    }

    @Nullable
    private ExchangeCurrency findAsset(String fromKey, List<ExchangeCurrency> list) {
        if (Pub.isListExists(list)) {
            for (ExchangeCurrency assetBean : list) {
                if (fromKey.equals(assetBean.getAssetId())) {
                    return assetBean;
                }
            }
        }
        return null;
    }
}
