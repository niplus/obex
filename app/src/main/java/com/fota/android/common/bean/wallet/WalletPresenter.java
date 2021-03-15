package com.fota.android.common.bean.wallet;

import com.fota.android.app.GsonSinglon;
import com.fota.android.app.SocketKey;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.Http;
import com.fota.android.moudles.wallet.index.WalletFragment;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;

public class WalletPresenter extends BaseListPresenter<BaseListView> {

    boolean isSocket;

    public void setSocket(boolean socket) {
        isSocket = socket;
    }

    public WalletPresenter(BaseListView view) {
        super(view);
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);
        if(isSocket){
            client.removeChannel(SocketKey.MineAssetReqType, this);
            addChannel();
            return;
        }
        Http.getWalletService().getWallet()
                .compose(new CommonTransformer<WalletBean>())
                .subscribe(new CommonSubscriber<WalletBean>(getView()) {

                    @Override
                    public void onNext(WalletBean list) {
                        if (getView() != null) {
                            if (list != null) {
                                getView().notifyFromPresenter(WalletFragment.SET_All_MONEY_INFO, list.getTotalValuation());
                                getView().notifyFromPresenter(WalletFragment.SET_All_MONEY_INFO_USD, list.getTotalUsdValuation());
                                setData(list.getItem(), isLoadMore);
                            } else {
                                setData(null, isLoadMore);
                            }
                            //addChannel();
                        }
                    }
                });

//
    }

    private void addChannel() {
        WebSocketEntity<BtbMap> socketEntity = new WebSocketEntity<>();
        socketEntity.setReqType(SocketKey.MineAssetReqType);
        BtbMap map = new BtbMap();
        map.put("pageNo", "1");
        map.p("pageSize", pageSize);
        socketEntity.setParam(map);
        client.addChannel(socketEntity, this);
    }

    @Override
    public void onUpdateImplSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity) {
        if (getView() == null) {
            return;
        }
        switch (reqType) {
            case SocketKey.MineAssetReqType:
//                Type type = new TypeToken<BaseHttpResult<WalletBean>>() {
//                }.getType();
//                BaseHttpResult<WalletBean> list = GsonSinglon.getInstance().fromJson(jsonString, type);
                WalletBean list = GsonSinglon.getInstance().fromJson(jsonString, WalletBean.class);
                if (list == null) {
                    setData(null, false);
                } else {
//                    getView().notifyFromPresenter(WalletFragment.SET_All_MONEY_INFO, list.getTotalValuation());
//                    getView().notifyFromPresenter(WalletFragment.SET_All_MONEY_INFO_USD, list.getTotalUsdValuation());
                    setData(list.getItem(), false);
                }
                break;
        }
    }


    @Override
    public void detachView() {
        client.removeChannel(SocketKey.MineAssetReqType, this);
        super.detachView();
    }
}

