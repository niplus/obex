package com.fota.android.moudles.futures.money;

import android.util.Log;

import com.fota.android.app.GsonSinglon;
import com.fota.android.app.SocketKey;
import com.fota.android.commonlib.http.BaseHttpPage;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BtbMap;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class FuturesMoneyPresenter extends BaseListPresenter<FuturesMoneyView> {

    public FuturesMoneyPresenter(FuturesMoneyView view) {
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
    protected void autoAddPageNo() {
        //super.aotoAddPageNo();
    }

    @Override
    protected void autoInitPageNo() {
        //super.aotoInitPageNo();
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);

        Log.i("nidongliang1111", "onLoadData");
        client.removeChannel(SocketKey.MinePositionReqType, this);
        WebSocketEntity<BtbMap> socketEntity = new WebSocketEntity<>();
        socketEntity.setReqType(SocketKey.MinePositionReqType);
        BtbMap map = new BtbMap();
        if (isLoadMore) {
            pageNo++;
        } else {
            pageNo = 1;
        }
        addPageInfotoMap(map);
        socketEntity.setParam(map);
        socketEntity.setHandleType(WebSocketEntity.SEARCH);
        client.addChannel(socketEntity, this);

        WebSocketEntity<BtbMap> bind = new WebSocketEntity<>();
        bind.setReqType(SocketKey.MinePositionReqType);
        BtbMap mapBind = new BtbMap();
        mapBind.p("pageNo", "1");
        mapBind.p("pageSize", pageSize * pageNo);
        bind.setParam(mapBind);
        bind.setHandleType(WebSocketEntity.BIND);
        client.addChannel(bind, this);

    }

    @Override
    public void onUpdateImplSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity) {
        Log.i("niddongliang", "socket: " + jsonString);
        super.onUpdateImplSocket(reqType, jsonString, additionEntity);
        if (getView() == null) {
            return;
        }
        switch (reqType) {
            case SocketKey.MinePositionReqType:
                if (additionEntity.getCode() != 0) {
                    getView().refreshComplete();
                    return;
                }
                Type type = new TypeToken<BaseHttpPage<FuturesMoneyBean>>() {
                }.getType();
                BaseHttpPage<FuturesMoneyBean> list = GsonSinglon.getInstance().fromJson(jsonString, type);
                if (list == null) {
                    getView().refreshComplete();
                    return;
                }
                if (Pub.getListSize(list.getItem()) >= 50) {
                    getView().replaceData(list.getItem());
                } else {
                    setData(list.getItem(), isLoadMore);
                }
                break;
        }
    }


    @Override
    public void detachView() {
        client.removeChannel(SocketKey.MinePositionReqType, this);
        super.detachView();
    }

}
