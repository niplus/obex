package com.fota.android;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.fota.android.common.bean.wallet.AddressEntity;
import com.fota.android.commonlib.app.delegate.FragmentManagerDelegate;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

public class TestFragment extends MvpListFragment {

    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_xml_with_button;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        onRefresh();
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void onRefresh() {
        //super.onRefresh();
    }

    @Override
    protected String setAppTitle() {
        return getXmlString(R.string.address_manager);
    }

    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<AddressEntity, ViewHolder>(getContext(), R.layout.item_address_list_fragment) {

            @Override
            public void convert(ViewHolder holder, final AddressEntity model, int position) {
                
            }
        };
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public FragmentManagerDelegate getChildFragmentManagerDelegate(Fragment fragment) {
        return null;
    }
}