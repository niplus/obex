package com.fota.android.moudles.exchange.complete;

import com.fota.android.app.GsonSinglon;
import com.fota.android.app.SocketKey;
import com.fota.android.commonlib.http.BaseHttpPage;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BtbMap;
import com.fota.android.moudles.mine.bean.XianhuoChengjiaoBean;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static com.fota.android.app.SocketKey.MineDealReqType;

public class ExchangeCompletePresenter extends BaseListPresenter<BaseListView> {
    /**
     * socket模式加载列表
     *
     * @return
     */
    protected boolean socketLoadMode() {
        return true;
    }

    public ExchangeCompletePresenter(BaseListView view) {
        super(view);
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);
        //取消订单
        client.removeChannel(MineDealReqType, this);

        WebSocketEntity<BtbMap> socketEntity = new WebSocketEntity<>();
        socketEntity.setReqType(SocketKey.MineDealReqType);
        BtbMap map = new BtbMap();
        addPageInfotoMap(map);
        socketEntity.setParam(map);
        socketEntity.setHandleType(WebSocketEntity.SEARCH);
        client.addChannel(socketEntity, this);

        WebSocketEntity<BtbMap> bind = new WebSocketEntity<>();
        bind.setReqType(SocketKey.MineDealReqType);
        //成交 只订阅第一条
        BtbMap mapBind = new BtbMap();
        mapBind.p("pageNo", "1");
        mapBind.p("pageSize", pageSize);
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
            case SocketKey.MineDealReqType:
                if (additionEntity.getCode() != 0) {
                    getView().refreshComplete();
                    return;
                }
                Type type = new TypeToken<BaseHttpPage<XianhuoChengjiaoBean.XianhuoChengjiaoBeanItem>>() {
                }.getType();
                BaseHttpPage<XianhuoChengjiaoBean.XianhuoChengjiaoBeanItem> list
                        = GsonSinglon.getInstance().fromJson(jsonString, type);
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
        client.removeChannel(SocketKey.MineDealReqType, this);
        super.detachView();
    }

}
