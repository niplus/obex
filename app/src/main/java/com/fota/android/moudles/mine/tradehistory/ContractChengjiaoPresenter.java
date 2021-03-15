package com.fota.android.moudles.mine.tradehistory;

import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.ContractChengjiaoBean;

public class ContractChengjiaoPresenter extends BaseListPresenter<BaseListView> {
    private Long start_time = null;
    private Long end_time = null;

    public ContractChengjiaoPresenter(BaseListView view) {
        super(view);
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);
        BtbMap map = new BtbMap();
        if (start_time != null)
            map.p("startTime", start_time);
        if (end_time != null)
            map.p("endTime", end_time);
        addPageInfotoMap(map);
        Http.getHttpService().getHYChengjiaoList(map)
                .compose(new CommonTransformer<ContractChengjiaoBean>())
                .subscribe(new CommonSubscriber<ContractChengjiaoBean>(getView()) {
                    @Override
                    public void onNext(ContractChengjiaoBean chengjiaoBean) {
                        if (getView() == null) {
                            return;
                        }
                        setData(chengjiaoBean.getItem(), isLoadMore);
                        getView().stopProgressDialog();

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        L.a("heyuechengjiao", "chengjiao fail" + e);
                        getView().stopProgressDialog();
                    }

                    @Override
                    protected boolean showLoading() {
                        return false;
                    }
                });


    }

    public void resetTime() {
        start_time = null;
        end_time = null;
        getView().startProgressDialog();
    }

    public void refresh() {
        resetPageNo();
        onRefresh();
        getView().startProgressDialog();
    }

    public void setTime(Long start, Long end) {
        getView().startProgressDialog();
        start_time = start;
        end_time = end;
        resetPageNo();
        onLoadData(false);
    }

    @Override
    public void detachView() {
//        super.detachView();
    }
}
