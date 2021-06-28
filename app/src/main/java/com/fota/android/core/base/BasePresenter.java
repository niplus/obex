package com.fota.android.core.base;


import android.os.Bundle;
import android.os.Handler;

import com.fota.android.commonlib.base.BaseView;
import com.fota.android.commonlib.base.INetWork;
import com.fota.android.socket.IWebSocketObserver;
import com.fota.android.socket.SocketAdditionEntity;

/**
 * Created by stone on 2016/7/25.
 */
public class BasePresenter<V extends BaseView> implements Presenter<V>, IWebSocketObserver {

    protected V mvpView;

    protected INetWork mINetWork;

//    protected IWebSocketSubject client;
    protected static final Handler handler = new Handler();

    public BasePresenter(V view) {
//        client = FotaApplication.getInstance().getClient();
        attachView(view);
    }

    public BasePresenter(V view, INetWork mINetWork) {
//        client = FotaApplication.getInstance().getClient();
        attachView(view);
        this.mINetWork = mINetWork;
    }

    @Override
    public void attachView(V view) {
        this.mvpView = view;
    }

    @Override
    public void detachView() {
        this.mvpView = null;
    }

    public V getView() {
        return mvpView;
    }

    public void getExtras(Bundle extras) {

    }

    @Override
    public void updateWebSocket(final int reqType, final String jsonString, final SocketAdditionEntity additionEntity) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onUpdateImplSocket(reqType, jsonString, additionEntity);
            }
        });
    }

    public void onUpdateImplSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity) {

    }

    @Override
    public void onHide() {

    }

    @Override
    public void finishView() {

    }

    public void onRefresh() {
        if (getView() != null) {
            getView().onRefresh();
        }
    }
}
