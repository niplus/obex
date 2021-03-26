package com.fota.android.moudles.futures;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.ConstantsReq;
import com.fota.android.app.FotaApplication;
import com.fota.android.app.GsonSinglon;
import com.fota.android.app.SocketKey;
import com.fota.android.common.bean.exchange.ExchangeBody;
import com.fota.android.common.bean.exchange.ExchangeEntity;
import com.fota.android.common.bean.home.DepthBean;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.ContractAssetBean;
import com.fota.android.http.Http;
import com.fota.android.moudles.exchange.index.ExchangeFragment;
import com.fota.android.moudles.exchange.index.ExchangePresenter;
import com.fota.android.moudles.exchange.index.ExchangeTradeView;
import com.fota.android.moudles.futures.money.FuturesMoneyBean;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

public class FuturesPresenter extends ExchangePresenter {


    /**
     * 选择的币种的对应列表的选中项
     */
    private FutureContractBean selectContact;
    private ContractAssetBean selectParent;


    //private String key;
    private List<ContractAssetBean> allList;

    private FutureContractBean fromContact;

    private String fromKey;

    public FuturesPresenter(ExchangeTradeView view) {
        super(view);
    }


    @Override
    protected void removeChannel() {
        super.removeChannel();
        client.removeChannel(SocketKey.MineEntrustReqType, this);
        client.removeChannel(SocketKey.DELIVERY_TIME_CHANGED, this);
        client.removeChannel(SocketKey.POSITION_LINE, this);
        client.removeChannel(SocketKey.TradeWeiTuoReqType, this);
        client.removeChannel(SocketKey.FUTURE_TOP, this);
    }

    protected void removeChildren() {
        client.removeChannel(SocketKey.MinePositionReqType, this);
        client.removeChannel(SocketKey.MineEntrustReqType_CONTRACT, this);
        client.removeChannel(SocketKey.TradeDealReqType, this);
    }

    @Override
    public void getExtras(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (bundle.containsKey(BundleKeys.MODEL)) {
            fromContact = (FutureContractBean) bundle.getSerializable(BundleKeys.MODEL);
        }
        if (bundle.containsKey(BundleKeys.KEY)) {
            fromKey = bundle.getString(BundleKeys.KEY);
        }

    }

    @Override
    public void submit(String fundCode) {
        if (selectContact == null) {
            return;
        }
        model.setContractId(selectContact.getContractId());
        model.setContractName(selectContact.getContractName());
        model.setLever(selectContact.getLever());
        ExchangeBody body = new ExchangeBody();
        body.setTradeToken(fundCode);
        body.setObj(model);
        Http.getExchangeService().makeContractOrder(body)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {

                    @Override
                    public void onNext(BaseHttpEntity baseHttpEntity) {
                        if (getView() != null) {
                            showTopInfo(model);
                            getView().notifyFromPresenter(ExchangeFragment.ORDER_SUCCESS);
                        }
                    }

                });
    }


    public void preciseMargin() {
        if (selectContact == null) {
            return;
        }
        model.setContractId(selectContact.getContractId());
        model.setContractName(selectContact.getContractName());
        ExchangeBody body = new ExchangeBody();
        body.setObj(model);
        Http.getExchangeService().preciseMargin(body)
                .compose(new CommonTransformer<BtbMap>())
                .subscribe(new CommonSubscriber<BtbMap>() {
                    @Override
                    public void onNext(BtbMap map) {
                        getView().setPreciseMargin(map);
                    }

                    @Override
                    protected void onError(ApiException e) {
                        //super.onError(e);
                    }
                });
    }


    /**
     * 下单成功
     */
    protected void showTopInfo(ExchangeEntity model) {
        getView().showTopInfo(getView().getXmlString(R.string.order_success));
    }

    public void removeMoney(final FuturesMoneyBean model, String fundCode) {
        final ExchangeEntity modelPost = new ExchangeEntity();
        modelPost.setPriceType(2);
        modelPost.setTotalAmount(model.getAmount());
        modelPost.setIsBuy(!model.isBuy());
        modelPost.setContractId(model.getContractId());
        modelPost.setContractName(model.getContractName());
        final ExchangeBody body = new ExchangeBody();
        body.setTradeToken(fundCode);
        body.setObj(modelPost);
        Http.getExchangeService().makeContractOrder(body)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {

                    @Override
                    public void onNext(BaseHttpEntity baseHttpEntity) {
                        if (getView() != null) {
                            model.setCanceled(true);
                            showTopInfo(modelPost);
                        }
                    }
                });
    }


    @Override
    public void reqUsdtList() {

    }

    public void getContactList() {
        Http.getExchangeService().getContractTree()
                .compose(new CommonTransformer<List<ContractAssetBean>>())
                .subscribe(new CommonSubscriber<List<ContractAssetBean>>() {

                    @Override
                    public void onNext(List<ContractAssetBean> list) {
                        if (getView() != null) {
                            getView().refreshComplete();
                        }
                        setAllList(list);
                        //从传值过来的地方处理
                        if (fromKey != null && fromContact != null) {
                            ContractAssetBean parent = findContractParent(fromKey, list);
                            if (parent != null) {
                                FutureContractBean item = findContractItem(fromContact, parent);
                                if (item != null) {
                                    fromKey = null;
                                    fromContact = null;
                                    setSelectContact(parent, item);
                                    return;
                                }
                            }
                        }
                        //选中上次选中的
                        if (selectParent != null && selectContact != null) {
                            ContractAssetBean parent = findContractParent(selectParent.getName(), list);
                            if (parent != null) {
                                FutureContractBean item = findContractItem(selectContact, parent);
                                if (item != null) {
                                    setSelectContact(parent, item);
                                    return;
                                }
                            }
                        }
                        //都没有选中第一个
                        if (Pub.isListExists(list)) {
                            setSelectContact(list.get(0), list.get(0).getContent().get(0));
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

    private FutureContractBean findContractItem(FutureContractBean fromContact, ContractAssetBean parent) {
        if (parent != null && fromContact != null) {
            if (Pub.isListExists(parent.getContent())) {
                for (FutureContractBean item : parent.getContent()) {
                    if (fromContact.equals(item)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private ContractAssetBean findContractParent(String fromKey, List<ContractAssetBean> list) {
        if (Pub.isListExists(list)) {
            for (ContractAssetBean assetBean : list) {
                if (fromKey.equals(assetBean.getName())) {
                    return assetBean;
                }
            }
        }
        return null;
    }


    public void setSelectContact(ContractAssetBean selectParent, FutureContractBean selectContact) {
        //jiang 切换基准币种，需要重置精度
        if (!selectParent.equals(this.selectParent)) {
            setBasePrecision("-1");
        }
        this.selectParent = selectParent;
        this.selectContact = selectContact;
        getView().refreshCurrency();
        //jiang 0818
//        getContractDelivery();
        getContractAccount(selectContact.getContractId());
        getDepthFive(getType(), Pub.GetInt(selectContact.getContractId()));
        getNowTicker(getType(), Pub.GetInt(selectContact.getContractId()));
        getTimeLineDatas(getType(), Pub.GetInt(selectContact.getContractId()), "1m");
        //jiang chart loading
        if (getView() != null) {
            getView().setOverShowLoading(0, true);
            getView().setOverShowLoading(1, true);
            getKlineDatas(2, Pub.GetInt(selectContact.getContractId()), types[currentPeriodIndex]);
        }
    }

    @Override
    protected int getType() {
        return ConstantsReq.TRADE_TYPE_CONTACT;
    }

    /**
     * 权益、保证金、保证金率
     */
    public void getContractAccount(String contactId) {
        BtbMap map = new BtbMap();
        map.p("contractId", contactId);
        client.removeChannel(SocketKey.FUTURE_TOP, this);
        addChannel();
//        Http.getExchangeService().getContractAccount(map)
//                .compose(new CommonTransformer<FutureTopInfoBean>())
//                .subscribe(new CommonSubscriber<FutureTopInfoBean>() {
//                    @Override
//                    public void onNext(FutureTopInfoBean map) {
//                        getView().setContractAccount(map);
//                        addChannel();
//                    }
//                });
    }

    private void addChannel() {
        WebSocketEntity<BtbMap> socketEntity = new WebSocketEntity<>();
        socketEntity.setReqType(SocketKey.FUTURE_TOP);
        client.addChannel(socketEntity, this);
    }

    @Override
    public FutureTradeView getView() {
        return (FutureTradeView) super.getView();
    }

    /**
     * 现货指数
     */
//    public void getContractDelivery() {
//        if (selectParent == null) {
//            return;
//        }
//        BtbMap map = new BtbMap();
//        map.p("symbol", getSymbolByKey(selectParent.getName()));
//        Http.getExchangeService().getContractDelivery(map)
//                .compose(new CommonTransformer<BtbMap>())
//                .subscribe(new CommonSubscriber<BtbMap>() {
//                    @Override
//                    public void onNext(BtbMap map) {
//                        getView().setContractDelivery(map);
//                    }
//                });
//    }

    /**
     * 0-BTC指数，1-ETH指数，2-EOS指数，3-BCH指数，4-ETC指数，5-LTC指数
     * 这种转换最好在后端
     *
     * @param key
     * @return
     */
    private int getSymbolByKey(String key) {
        if ("BTC".equals(key)) {
            return 0;
        }
        if ("ETH".equals(key)) {
            return 1;
        }
        if ("EOS".equals(key)) {
            return 2;
        }
        if ("BCH".equals(key)) {
            return 3;
        }
        if ("ETC".equals(key)) {
            return 4;
        }
        if ("LTC".equals(key)) {
            return 5;
        }
        return 0;
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
                if (selectContact == null) {
                    return;
                }
                Map<String, DepthBean> depthMap = FotaApplication.getInstance().getDepthMap();
                String key = "2-" + selectContact.getContractId();
                DepthBean bean = depthMap.get(key);
                if (bean == null) {
                    return;
                }
                onNextDepth(bean);
                break;
            case SocketKey.TradeSuccessNotiification:
//                if (jsonString == null || getView() == null) {
//                    return;
//                }
//                ExchangeTopInfo model = GsonSinglon.getInstance().fromJson(jsonString, ExchangeTopInfo.class);
//                StringBuilder sb = new StringBuilder();
//                sb.append(model.getSymbol());
//                sb.append(" ");
//                sb.append(model.getPrice());
//                sb.append(" ");
//                if (model.isBuy()) {
//                    //0-对手价，1-指定价
//                    sb.append(getView().getXmlString(R.string.future_duo_filled));
//                } else {
//                    sb.append(getView().getXmlString(R.string.future_kong_filled));
//                }
//                sb.append(" ");
//                sb.append(model.getAmount());
//                sb.append(" BTC");
//                getView().showTopInfo(sb.toString());
                break;
            case SocketKey.DELIVERY_TIME_CHANGED:
            case SocketKey.POSITION_LINE:
                getView().onRefreshDeliveryOrHold();
                break;
            case SocketKey.FUTURE_TOP:
                if (jsonString == null || getView() == null) {
                    return;
                }
                FutureTopInfoBean map = GsonSinglon.getInstance().fromJson(jsonString, FutureTopInfoBean.class);
                getView().setContractAccount(map);
                break;
            default:
                super.onUpdateImplSocket(reqType, jsonString, additionEntity);
                return;
        }
    }


    public FutureContractBean getSelectContact() {
        return selectContact;
    }

    public List<ContractAssetBean> getAllList() {
        return allList;
    }

    public void setAllList(List<ContractAssetBean> allList) {
        this.allList = allList;
    }


    public ContractAssetBean getSelectParent() {
        return selectParent;
    }

    public void setFromHq(String assetName, FutureContractBean model) {
        fromKey = assetName;
        fromContact = model;
    }


    @Override
    public void detachView() {
        super.detachView();
        removeChannel();
    }

    @Override
    public void onHide() {
        removeChannel();
    }

    /**
     * 设置杠杆
     *
     * @param assetId
     * @param assetName
     * @param lever
     */
    public void setLever(int assetId, String assetName, int lever) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("assetId", assetId);
        jsonObject.addProperty("assetName", assetName);
        jsonObject.addProperty("lever", lever);
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("data", "[" + jsonObject.toString() + "]");
        Http.getHttpService().setContractLever(jsonObject2)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        if (getView() == null) {
                            return;
                        }
                        onRefresh();

                        getView().onLeverChange();
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);

                    }
                });
    }

}
