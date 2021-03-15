package com.fota.android.moudles.wallet.transfer;

import com.fota.android.http.Http;
import com.fota.android.common.bean.wallet.TransferListItemBean;
import com.fota.android.commonlib.http.BaseHttpPage;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BtbMap;

public class TransferListPresenter extends BaseListPresenter<BaseListView> {
    private boolean isFromContract;

    public TransferListPresenter(BaseListView view) {
        super(view);
        setPageSize(10);
    }

    public void setFromContract(boolean fromContract) {
        isFromContract = fromContract;
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);

        BtbMap map = new BtbMap();
        map.p("fromType", isFromContract ? 2 : 1);
        map.p("toType", isFromContract ? 1 : 2);
//        map.p("startTime", "");
//        map.p("endTime", "");
        addPageInfotoMap(map);
        Http.getWalletService().getTransferList(map)
                .compose(new CommonTransformer<BaseHttpPage<TransferListItemBean>>())
                .subscribe(new CommonSubscriber<BaseHttpPage<TransferListItemBean>>(getView()) {

                    @Override
                    public void onNext(BaseHttpPage<TransferListItemBean> list) {
                        if(getView() != null) {
                            setData(list.getItem(), isLoadMore);
                        }
                    }
                });

    }

}
