package com.fota.android.moudles.wallet.transfer;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.fota.android.R;
import com.fota.android.core.base.FotaViewPagerFragment;

import java.util.ArrayList;
import java.util.List;

public class TransferHistoryFragment extends FotaViewPagerFragment {
    boolean fromContract = false;

    @Override
    protected List<String> getTitles() {
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.transfer_from_wallet));
        titles.add(getString(R.string.transfer_from_contract));
        return titles;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        setTitleBg(R.attr.bg_color2);
        if (fromContract) {
            setCurrentItem(1);
        } else {
            setCurrentItem(0);
        }
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        fromContract = bundle.getBoolean("fromContract");
    }

    @Override
    protected List<Fragment> getFragment() {
        List<Fragment> fragments = new ArrayList<>();
        TransferListFragment fromWallet = new TransferListFragment();
        fromWallet.setFromContract(false);
        fragments.add(fromWallet);
        TransferListFragment fromContract = new TransferListFragment();
        fromContract.setFromContract(true);
        fragments.add(fromContract);
        return fragments;
    }

    @Override
    public String setAppTitle() {
        return getString(R.string.wallet_his_transfer);
    }


}
