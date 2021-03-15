package com.fota.android.moudles.wallet.history;

import android.os.Bundle;

import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.common.bean.wallet.WalletBean;
import com.fota.android.common.bean.wallet.WalletHistoryBean;
import com.fota.android.common.bean.wallet.WalletItem;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.BaseHttpPage;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BtbMap;
import com.fota.android.core.base.ft.KeyValue;
import com.fota.android.http.Http;

import java.util.ArrayList;
import java.util.List;

public class WithDrawHistoryPresenter extends BaseListPresenter<BaseListView> {

    int assetIndex;
    int typeIndex;
    List<WalletItem> assetList;
    List<KeyValue> typeList;
    private String fromKey;

    @Override
    public void getExtras(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (bundle.containsKey(BundleKeys.KEY)) {
            fromKey = bundle.getString(BundleKeys.KEY);
        }

    }


    public WithDrawHistoryPresenter(BaseListView view) {
        super(view);
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);
        if (!Pub.isListExists(getAssetList())) {
            return;
        }
        BtbMap map = new BtbMap();
        if (!Pub.isStringEmpty(getAssetValue())) {
            map.p("assetId", getAssetValue());
        }
        if (!Pub.isStringEmpty(getTypeValue())) {
            map.p("transferType", getTypeValue());
        }

        addPageInfotoMap(map);
        Http.getWalletService().getWitdrawtHistory(map)
                .compose(new CommonTransformer<BaseHttpPage<WalletHistoryBean>>())
                .subscribe(new CommonSubscriber<BaseHttpPage<WalletHistoryBean>>(getView()) {

                    @Override
                    public void onNext(BaseHttpPage<WalletHistoryBean> list) {
                        setData(list.getItem(), isLoadMore);
                    }
                });
    }

    public String getTypeValue() {
        return getTypeList().get(getTypeIndex()).getValue();
    }

    public String getAssetValue() {
        return getAssetList().get(getAssetIndex()).getAssetId();
    }


    public void initTypeList() {
        typeList = new ArrayList<>();
        typeList.add(new KeyValue(getView().getXmlString(R.string.common_all), ""));
        typeList.add(new KeyValue(getView().getXmlString(R.string.wallet_recharge_money), "1"));
        typeList.add(new KeyValue(getView().getXmlString(R.string.wallet_withdraw_money), "2"));
    }


    public void initBaseAssetList() {
        Http.getWalletService().getWallet()
                .compose(new CommonTransformer<WalletBean>())
                .subscribe(new CommonSubscriber<WalletBean>() {

                    @Override
                    public void onNext(WalletBean walletBean) {
                        List<WalletItem> list = walletBean.getItem();
                        if (list != null) {
                            list.add(0, new WalletItem("", getView().getXmlString(R.string.common_all)));
                        }
                        setAssetList(list);
                        int defauntKey = findIndexFromList(list);
                        setAssetIndex(defauntKey);
                        getView().notifyFromPresenter(0);
                    }
                });
    }

    private int findIndexFromList(List<WalletItem> list) {
        if (fromKey == null) {
            return 0;
        }
        if (!Pub.isListExists(list)) {
            return 0;
        }

        for (int i = 0; i < list.size(); i++) {
            if (fromKey.equals(list.get(i).getAssetId())) {
                return i;
            }
        }
        return 0;

    }

    public void setTypeList(List<KeyValue> typeList) {
        this.typeList = typeList;
    }

    public List<WalletItem> getAssetList() {
        return assetList;
    }

    public List<KeyValue> getTypeList() {
        return typeList;
    }

    public void setAssetList(List<WalletItem> assetList) {
        this.assetList = assetList;
    }

    public int getAssetIndex() {
        return assetIndex;
    }

    /**
     * 负载刷新
     *
     * @param assetIndex 币种内存
     */
    public void setAssetIndex(int assetIndex) {
        this.assetIndex = assetIndex;
        onRefresh();
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    /**
     * 负载刷新
     *
     * @param typeIndex 充币提币类型
     */
    public void setTypeIndex(int typeIndex) {
        this.typeIndex = typeIndex;
        onRefresh();
    }

    /**
     * 撤
     *
     * @param model
     */
    public void deleteItem(WalletHistoryBean model) {
        BtbMap map = new BtbMap();
        map.p("id", model.getId());
        Http.getWalletService().deleteWithdraw(map)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>() {

                    @Override
                    public void onNext(BaseHttpEntity baseHttpEntity) {
                        onRefresh();
                    }
                });
    }
}
