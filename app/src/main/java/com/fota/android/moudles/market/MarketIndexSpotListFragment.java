package com.fota.android.moudles.market;

import android.os.Bundle;

import com.fota.android.moudles.market.bean.FutureItemEntity;

public class MarketIndexSpotListFragment extends MarketListFragment {

    public static MarketIndexSpotListFragment newInstance() {
        Bundle args = new Bundle();
//        args.putString("symbol", symbol);
        MarketIndexSpotListFragment fragment = new MarketIndexSpotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    protected void refreshDataByGroup() {
        if (groupListDatas == null) {
            return;
        }
        if (groupListDatas.size() <= 0) {
            for (int i = 0; i < futureList.size(); i++) {
                FutureItemEntity each = futureList.get(i);
                if(i > 0) {
                    each.setShowTopMargin(false);
                } else if(i ==0) {
                    each.setShowTopMargin(true);
                }
                groupListDatas.add(each);
            }
        }
    }
}
