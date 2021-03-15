package com.fota.android.moudles.mine.tradehistory;

import com.fota.android.R;
import com.fota.android.commonlib.base.BaseView;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.core.base.ft.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class TradeHistoryPresenter extends BasePresenter {
    List<FtKeyValue> nameList;
    List<FtKeyValue> typeList;

    public TradeHistoryPresenter(BaseView view) {
        super(view);
    }


    public void initList() {
        typeList = new ArrayList<>();
        typeList.add(new KeyValue(getView().getXmlString(R.string.exchange_order), "1"));
        typeList.add(new KeyValue(getView().getXmlString(R.string.exchange_complete), "2"));
        nameList = new ArrayList<>();
        nameList.add(new KeyValue(getView().getXmlString(R.string.tradehis_contract), "1"));
        nameList.add(new KeyValue(getView().getXmlString(R.string.tradehis_spot), "2"));
    }

    public List<FtKeyValue> gettypeList() {
        return typeList;
    }

    public List<FtKeyValue> getnameList() {
        return nameList;
    }
}
