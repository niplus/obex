package com.fota.android.moudles.mine.tradeset;

import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.ContractLevelBean;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 交易设置
 */
public class TradeLeverPresenter extends BaseListPresenter<BaseListView> {
    public TradeLeverPresenter(BaseListView view) {
        super(view);
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);
        Http.getHttpService().getContractLevels()
                .compose(new CommonTransformer<List<ContractLevelBean>>())
                .subscribe(new CommonSubscriber<List<ContractLevelBean>>(getView()) {
                    @Override
                    public void onNext(List<ContractLevelBean> hotcontactBean) {
                        L.a("ContractLevelBean " + hotcontactBean.toString());
                        if (getView() == null) {
                            return;
                        }
                        setDataList(hotcontactBean);

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        L.a("ContractLevelBean fail" + e);
                    }
                });
    }

    /**
     * 设置杠杆
     *
     * @param assetId
     * @param assetName
     * @param lever
     */
    public void setLever(int assetId, String assetName, int lever) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("assetId", assetId);
        jsonObject.addProperty("assetName", assetName);
        jsonObject.addProperty("lever", lever);
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("data", "[" + jsonObject.toString() + "]");
        Http.getHttpService().setContractLever(jsonObject2)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        if (getView() == null) {
                            return;
                        }
                        L.a("setLever = " + object.toString());
//                        getView().showToast(R.string.tradeset_lever_suc);
                        onRefresh();

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        L.a("reset", "setLever fail fail ---" + e.toString());

                    }
                });
    }

}
