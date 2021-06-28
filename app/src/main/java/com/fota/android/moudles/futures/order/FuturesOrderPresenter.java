package com.fota.android.moudles.futures.order;

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

public class FuturesOrderPresenter extends BaseListPresenter<BaseListView> {


    public FuturesOrderPresenter(BaseListView view) {
        super(view);
    }

    @Override
    protected boolean socketLoadMode() {
        return true;
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);
        //client.removeChannel(SocketKey.MineEntrustReqType_CONTRACT, this);
        WebSocketEntity<BtbMap> socketEntity = new WebSocketEntity<>();
        socketEntity.setReqType(SocketKey.MineEntrustReqType_CONTRACT);
        BtbMap map = new BtbMap();
        addPageInfotoMap(map);
        map.put("type", "2");
        socketEntity.setParam(map);
        socketEntity.setHandleType(WebSocketEntity.SEARCH);
        //client.addChannel(socketEntity, this);

        WebSocketEntity<BtbMap> bind = new WebSocketEntity<>();
        bind.setReqType(SocketKey.MineEntrustReqType_CONTRACT);
        BtbMap mapBind = new BtbMap();
        mapBind.p("pageNo", "1");
        mapBind.p("pageSize", pageSize * pageNo);
        mapBind.put("type", "2");
        bind.setParam(mapBind);
        bind.setHandleType(WebSocketEntity.BIND);
        //client.addChannel(bind, this);
    }

    @Override
    public void onUpdateImplSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity) {
        super.onUpdateImplSocket(reqType, jsonString, additionEntity);
        if (getView() == null) {
            return;
        }
        switch (reqType) {
            case SocketKey.MineEntrustReqType_CONTRACT:
                if (additionEntity.getCode() != 0) {
                    getView().refreshComplete();
                    return;
                }
                Type type = new TypeToken<BaseHttpPage<FuturesOrderBean>>() {
                }.getType();
                BaseHttpPage<FuturesOrderBean> list = GsonSinglon.getInstance().fromJson(jsonString, type);
                if (list == null) {
                    getView().refreshComplete();
                    return;
                }
                setData(list.getItem(), isLoadMore);
                break;
        }
    }


    public void deleteOrder(long id) {
        BtbMap map = new BtbMap();
        map.p("id", id);
        Http.getExchangeService().deleteContractOrder(map)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>() {

                    @Override
                    public void onNext(BaseHttpEntity baseHttpEntity) {
                        onRefresh();
                    }
                });
    }


    @Override
    public void detachView() {
        //client.removeChannel(SocketKey.MineEntrustReqType_CONTRACT, this);
        super.detachView();
    }
}
