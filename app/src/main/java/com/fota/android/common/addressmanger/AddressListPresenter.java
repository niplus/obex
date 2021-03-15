package com.fota.android.common.addressmanger;

import com.fota.android.common.bean.wallet.AddressEntity;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.Http;

import java.util.List;

/**
 * Created by fjw on 2018/4/20.
 */

public class AddressListPresenter extends BaseListPresenter<BaseListView> {


    private String assetId;

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public AddressListPresenter(BaseListView view) {
        super(view);
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);
        BtbMap map = new BtbMap();
        map.p("assetId", assetId);
        Http.getWalletService().getAddressList(map)
                .compose(new CommonTransformer<List<AddressEntity>>())
                .subscribe(new CommonSubscriber<List<AddressEntity>>(getView()) {

                    @Override
                    public void onNext(List<AddressEntity> list) {
                        setData(list, isLoadMore);
                    }
                });
    }


    /**
     * 删除用户的提币地址
     *
     * @param id
     */
    public void deleteAddress(int id) {
        BtbMap map = new BtbMap();
        map.p("id", id);
        Http.getWalletService().deleteAddress(map)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>() {

                    @Override
                    public void onNext(BaseHttpEntity baseHttpEntity) {
                        onRefresh();
                    }
                });
    }


}
