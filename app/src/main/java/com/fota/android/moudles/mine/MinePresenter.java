package com.fota.android.moudles.mine;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.fota.android.app.SocketKey;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.MineBean;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.utils.apputils.MineInfoUtil;

/**
 * 我的功能
 */
public class MinePresenter extends BasePresenter<IMineView> {
    public MinePresenter(IMineView view) {
        super(view);
    }


    /**
     * 获取我的数据
     */
    public void getMindeMsg() {

        if (!UserLoginUtil.havaUser()) {
            getView().mineDataFail();
            return;
        }

        Http.getHttpService().getMineData()
                .compose(new CommonTransformer<MineBean>())
                .subscribe(new CommonSubscriber<MineBean>(getView()) {
                    @Override
                    public void onNext(MineBean mineBean) {
                        if (getView() == null) {
                            return;
                        }

                        LogUtils.a("mine", "mine result " + mineBean.toString());
                        getView().mineDataSuccess(mineBean);
                        MineInfoUtil.saveMine(mineBean);


                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        getView().mineDataFail();
                        LogUtils.a("mine", "mine result fail" + e);
                        MineBean mineBean = MineInfoUtil.getDiskMine();
                        if (mineBean != null) {
                            getView().mineDataSuccess(mineBean);
                        }
//                        socketSubscribe();
                    }

                    @Override
                    protected boolean showLoading() {
                        return false;
                    }

                });
    }


    /**
     * @param reqType
     * @param jsonString 处理推送过来的数据，更新视图
     * @param additionEntity
     */
    @Override
    public void onUpdateImplSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity) {
        L.a("minesocket jsonString = " + jsonString);
        if (TextUtils.isEmpty(jsonString))
            return;

        if (getView() != null) {
            getView().setNoticeView(jsonString);
        }
    }

    /**
     * sodket订阅
     */
    public void socketSubscribe() {
        //初始化的时候就订阅 概览热门合约
        WebSocketEntity entity = new WebSocketEntity();
        entity.setReqType(SocketKey.MineMsgCenterHasUnreadPushReqType);
        entity.setToken(UserLoginUtil.getToken());
        client.addChannel(entity, this);
    }


}
