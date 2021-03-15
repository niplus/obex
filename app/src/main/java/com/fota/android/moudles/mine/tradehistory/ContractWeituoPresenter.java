package com.fota.android.moudles.mine.tradehistory;

import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.ContractWeituoBean;

public class ContractWeituoPresenter extends BaseListPresenter<BaseListView> {
    private Long start_time = null;
    private Long end_time = null;


    public ContractWeituoPresenter(BaseListView view) {
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
        Http.getHttpService().getHYTradeList(map)
                .compose(new CommonTransformer<ContractWeituoBean>())
                .subscribe(new CommonSubscriber<ContractWeituoBean>(getView()) {
                    @Override
                    public void onNext(ContractWeituoBean chengjiaoBean) {
                        if (getView() == null) {
                            return;
                        }
                        setData(chengjiaoBean.getItem(), isLoadMore);
                        getView().stopProgressDialog();

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        L.a("heyueweituo", "heyueweuoit fail" + e);
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



    public void setTime(Long start, Long end) {
        getView().startProgressDialog();
        start_time = start;
        end_time = end;
        resetPageNo();
        onLoadData(false);
    }

    public void refresh() {
        resetPageNo();
        onRefresh();
        getView().startProgressDialog();
    }

    @Override
    public void detachView() {
//        super.detachView();
    }
}
