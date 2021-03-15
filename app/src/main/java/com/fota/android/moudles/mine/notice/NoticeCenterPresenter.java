package com.fota.android.moudles.mine.notice;

import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.NoticeCenterBean;

public class NoticeCenterPresenter extends BaseListPresenter<BaseListView> {
    public NoticeCenterPresenter(BaseListView view) {
        super(view);
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);
        BtbMap map = new BtbMap();

        addPageInfotoMap(map);
        Http.getHttpService(null, true).getNoticesList(map)
                .compose(new CommonTransformer<NoticeCenterBean>())
                .subscribe(new CommonSubscriber<NoticeCenterBean>(getView()) {
                    @Override
                    public void onNext(NoticeCenterBean noticeCenterBean) {
                        if (getView() == null) {
                            return;
                        }
                        setData(noticeCenterBean.getItem(), isLoadMore);
                        getView().stopProgressDialog();

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        L.a("getNoticesList", "getNoticesList fail" + e);
                        getView().stopProgressDialog();
                    }

                    @Override
                    protected boolean showLoading() {
                        return false;
                    }
                });
    }
}
