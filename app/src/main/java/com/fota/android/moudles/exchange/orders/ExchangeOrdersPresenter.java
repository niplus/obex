package com.fota.android.moudles.exchange.orders;

import com.fota.android.R;
import com.fota.android.app.GsonSinglon;
import com.fota.android.app.SocketKey;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.BaseHttpPage;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.Http;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static com.fota.android.app.SocketKey.MineEntrustReqType;

public class ExchangeOrdersPresenter extends BaseListPresenter<BaseListView> {


    public ExchangeOrdersPresenter(BaseListView view) {
        super(view);
    }

    /**
     * socket模式加载列表
     *
     * @return
     */
    protected boolean socketLoadMode() {
        return true;
    }


    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);
        //取消订单
        client.removeChannel(MineEntrustReqType, this);

        WebSocketEntity<BtbMap> socketEntity = new WebSocketEntity<>();
        socketEntity.setReqType(SocketKey.MineEntrustReqType);
        BtbMap map = new BtbMap();
        addPageInfotoMap(map);
        map.put("type", "3");
        socketEntity.setParam(map);
        socketEntity.setHandleType(WebSocketEntity.SEARCH);
        client.addChannel(socketEntity, this);

        WebSocketEntity<BtbMap> bind = new WebSocketEntity<>();
        bind.setReqType(SocketKey.MineEntrustReqType);
        BtbMap mapBind = new BtbMap();
        mapBind.p("pageNo", "1");
        mapBind.p("pageSize", pageSize * pageNo);
        mapBind.put("type", "3");
        bind.setParam(mapBind);
        bind.setHandleType(WebSocketEntity.BIND);
        client.addChannel(bind, this);
    }

    @Override
    public void onUpdateImplSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity) {
        super.onUpdateImplSocket(reqType, jsonString, additionEntity);
        if (getView() == null) {
            return;
        }
        switch (reqType) {
            case MineEntrustReqType:
                if (additionEntity.getCode() != 0) {
                    getView().refreshComplete();
                    return;
                }
                Type type = new TypeToken<BaseHttpPage<ExchangeOrderBean>>() {
                }.getType();
                BaseHttpPage<ExchangeOrderBean> list = GsonSinglon.getInstance().fromJson(jsonString, type);
                if (list == null) {
                    getView().refreshComplete();
                    return;
                }
                setData(list.getItem(), isLoadMore);
                break;
        }
    }


    public void deleteOrder(String id) {
        BtbMap map = new BtbMap();
        map.p("id", id);
        Http.getExchangeService().deleteOrder(map)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>() {

                    @Override
                    public void onNext(BaseHttpEntity baseHttpEntity) {
                        getView().showToast(R.string.exchange_order_cancel);
                    }
                });
    }

    @Override
    public void detachView() {
        client.removeChannel(MineEntrustReqType, this);
        super.detachView();
    }
}
