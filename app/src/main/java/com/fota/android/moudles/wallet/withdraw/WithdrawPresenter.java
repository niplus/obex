package com.fota.android.moudles.wallet.withdraw;

import android.util.Log;

import com.fota.android.app.FotaApplication;
import com.fota.android.common.bean.wallet.RateBean;
import com.fota.android.common.bean.wallet.WalletBean;
import com.fota.android.common.bean.wallet.WalletItem;
import com.fota.android.common.bean.wallet.WithDrawEntity;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.Http;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 2018/4/19.
 */

public class WithdrawPresenter extends BasePresenter<WithdrawView> {


    WithDrawEntity model;

    public List<WalletItem> items;

    public WithdrawPresenter(WithdrawView view) {
        super(view);
    }

    public void setModel(WithDrawEntity model) {
        this.model = model;
    }

    public WithDrawEntity getModel() {
        return model;
    }

    public List<WalletItem> getItems() {
        return items;
    }

    public void getRate(String assetName, String net){
        Http.getWalletService().getRate(assetName, net)
                .compose(new CommonTransformer<RateBean>())
                .subscribe(new CommonSubscriber<RateBean>() {
                    @Override
                    public void onNext(RateBean rateBean) {

                        getView().setRate(rateBean);
                    }
                });
    }

    /**
     * 获取钱包
     */
    public void getWallet() {
        Http.getWalletService().getWallet()
                .compose(new CommonTransformer<WalletBean>())
                .subscribe(new CommonSubscriber<WalletBean>(getView()) {

                    @Override
                    public void onNext(WalletBean list) {
                        items = new ArrayList<>();
                        for (WalletItem bean : list.getItem()) {
//                            if ("USDT".equals(bean.getAssetName())) {
//                                WalletItem itemOmni = new WalletItem(bean);
//                                itemOmni.OMNI();
//                                WalletItem itemEth = new WalletItem(bean);
//                                itemEth.ETH();
//                                items.add(itemOmni);
//                                items.add(itemEth);
//                            } else {
                                items.add(bean);
//                            }
                        }
                        getView().setSelectItem(items.get(0));
                    }
                });
    }


    /**
     * 提交
     */
    public void submit(String net) {
        model.setNetWork(net);
        Http.getWalletService().withDraw(model)
                .compose(new CommonTransformer<BtbMap>())
                .subscribe(new CommonSubscriber<BtbMap>(FotaApplication.getInstance()) {

                    @Override
                    public void onNext(BtbMap map) {
                        if (getView() != null) {
                            getView().setSuccess(map);
                        }
                    }
                });
    }

    /**
     * 提交
     */
    public void withDrawCheck() {
        Http.getWalletService().withDrawCheck()
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {

                    @Override
                    public void onNext(BaseHttpEntity baseHttpEntity) {
                        if (getView() != null) {
                            getView().notifyFromPresenter(WithdrawActivity.WITHDRAW_PRE);
                        }
                    }
                });
    }
}
