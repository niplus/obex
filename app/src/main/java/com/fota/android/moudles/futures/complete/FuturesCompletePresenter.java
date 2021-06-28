package com.fota.android.moudles.futures.complete;

import android.util.Log;

import com.fota.android.app.GsonSinglon;
import com.fota.android.app.SocketKey;
import com.fota.android.commonlib.http.BaseHttpPage;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BtbMap;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class FuturesCompletePresenter extends BaseListPresenter<BaseListView> {

    /**
     * socket模式加载列表
     *
     * @return
     */
    protected boolean socketLoadMode() {
        return true;
    }

    public FuturesCompletePresenter(BaseListView view) {
        super(view);
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);
        //取消订单
        //client.removeChannel(TradeDealReqType, this);

        WebSocketEntity<BtbMap> socketEntity = new WebSocketEntity<>();
        socketEntity.setReqType(SocketKey.TradeDealReqType);
        BtbMap map = new BtbMap();
        addPageInfotoMap(map);
        socketEntity.setParam(map);
        socketEntity.setHandleType(WebSocketEntity.SEARCH);
        //client.addChannel(socketEntity, this);

        WebSocketEntity<BtbMap> bind = new WebSocketEntity<>();
        bind.setReqType(SocketKey.TradeDealReqType);
        //成交 只订阅第一条
        BtbMap mapBind = new BtbMap();
        mapBind.p("pageNo", "1");
        mapBind.p("pageSize", pageSize);
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
            case SocketKey.TradeDealReqType:
                Log.i("nidongliang111", "jsonString: " + jsonString);
                if (additionEntity.getCode() != 0) {
                    getView().refreshComplete();
                    return;
                }
                Type type = new TypeToken<BaseHttpPage<FuturesCompleteBean>>() {
                }.getType();
                BaseHttpPage<FuturesCompleteBean> list = GsonSinglon.getInstance().fromJson(jsonString, type);
                if (list == null) {
                    getView().refreshComplete();
                    return;
                }
                setData(list.getItem(), isLoadMore);
                break;
        }
    }

    @Override
    public void detachView() {
        //client.removeChannel(SocketKey.TradeDealReqType, this);
        super.detachView();
    }


}
