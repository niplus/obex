package com.fota.android.moudles.mine.tradehistory;

import android.text.TextUtils;

import com.fota.android.R;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BtbMap;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.core.base.ft.KeyValue;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.OptionCoinBean;
import com.fota.android.moudles.mine.bean.OptionHisBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 期权交易记录
 */
public class OptionHistoryPresenter extends BaseListPresenter<BaseListView> {
    private Long start_time = null;
    private Long end_time = null;
    private String currency;//选择币对

    public OptionHistoryPresenter(BaseListView view) {
        super(view);
    }

    @Override
    public void onLoadData(final boolean isLoadMore) {
        super.onLoadData(isLoadMore);
        BtbMap map = new BtbMap();
        if (start_time != null) {
            map.p("startTime", start_time);
        } else {
            map.p("startTime", "");
        }
        if (end_time != null) {
            map.p("endTime", end_time);
        } else {
            map.p("endTime", "");
        }
        if (TextUtils.isEmpty(currency)) {
            map.p("currency", "");
        } else {
            map.p("currency", currency);
        }

        addPageInfotoMap(map);
        Http.getHttpService().getOptionHisList(map)
                .compose(new CommonTransformer<OptionHisBean>())
                .subscribe(new CommonSubscriber<OptionHisBean>(getView()) {
                    @Override
                    public void onNext(OptionHisBean optionHisBean) {
                        if (getView() == null) {
                            return;
                        }
                        setData(optionHisBean.getItem(), isLoadMore);
                        getView().stopProgressDialog();

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        L.a("optionhis", "optionhis fail" + e);
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
    /**
     * 请求所有可下注币对
     */
    public void reqOptionCoins() {
        Http.getHttpService().getOptionAccountCoin()
                .compose(new CommonTransformer<List<OptionCoinBean>>())
                .subscribe(new CommonSubscriber<List<OptionCoinBean>>(getView()) {
                    @Override
                    public void onNext(List<OptionCoinBean> coins) {
                        if (getView() == null) {
                            return;
                        }
                        if (Pub.isListExists(coins)) {
                            originCoinsList = coins;
                            initList();
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                    }

                    @Override
                    protected boolean showLoading() {
                        return false;
                    }

                });
    }

    public void setCurrencyPosition(int position) {
        String varCurrencyId = "";
        if (position == 0) {//全部
            varCurrencyId = "";
        } else {
            int originIndex = position - 1;
            OptionCoinBean coinBean = originCoinsList.get(originIndex);
            if(coinBean != null) {
                varCurrencyId = coinBean.getId() + "";
            }
        }

//        else if (position == 1) {//BTC
//            varCurrencyId = "2";
//        } else if (position == 2) {//ETH
//            varCurrencyId = "3";
//        } else if (position == 3) {//FOTA
//            varCurrencyId = "4";
//        } else if (position == 4) {//DOGE
//            varCurrencyId = "9";
//        }else if (position == 5) {//DEMO
//            varCurrencyId = "999";
//        }
        seCurrency(varCurrencyId);
    }

    /**
     * 设置查询的币对
     *
     * @param currency
     */
    public void seCurrency(String currency) {
        this.currency = currency;
        start_time = null;
        end_time = null;
        resetPageNo();
        onLoadData(false);
    }

    List<FtKeyValue> typeList = new ArrayList<>();
    List<OptionCoinBean> originCoinsList = new ArrayList<>();

    public void initList() {
        typeList.clear();
        typeList.add(new KeyValue(getView().getXmlString(R.string.common_all), "1"));
        for(int i = 0;i<originCoinsList.size();i++) {
            int num = i+2;
            OptionCoinBean coinBean = originCoinsList.get(i);
            typeList.add(new KeyValue(coinBean.getName(), num + ""));
        }
    }

    public List<FtKeyValue> gettypeList() {
        return typeList;
    }
}
