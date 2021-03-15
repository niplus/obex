package com.fota.android.common.addressmanger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.common.bean.wallet.AddressEntity;
import com.fota.android.common.listener.IAddress;
import com.fota.android.commonlib.app.delegate.FragmentManagerDelegate;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.databinding.FragmentAddAddressBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.wallet.withdraw.WithdrawActivity;

/**
 * Created by fjw on 2018/4/19.
 */

public class AddAddressFragment extends BaseFragment implements View.OnClickListener {


    protected FragmentAddAddressBinding mBinding;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_address, container, false);
        mBinding.setView(this);
        return mBinding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        UIUtil.setRectangleBorderBg(mBinding.etAddressRemarkName, Pub.getColor(getContext(), R.attr.line_color));
        mBinding.addressIcon.setImageResource(AppConfigs.isWhiteTheme() ? R.mipmap.address_white : R.mipmap.address_black);
        bindValid(mBinding.btSure, mBinding.etAddressText, mBinding.etAddressRemarkName);
    }

    @Override
    protected boolean viewGroupFocused() {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_sure:
                submit();
                break;
            case R.id.scan:
                if (getHoldingActivity() instanceof WithdrawActivity) {
                    ((WithdrawActivity) getHoldingActivity()).scanQrCode(WithdrawActivity.REQUEST_CODE_SCAN_FRAGMENT
                            , mBinding.etAddressText);
                }
                break;
        }
    }

    @Override
    protected String setAppTitle() {
        return getXmlString(R.string.wallet_add_address);
    }

    /**
     * 添加地址信息到后台
     */
    protected void submit() {
        AddressEntity entity = new AddressEntity(mBinding.etAddressText.getText().toString(),
                mBinding.etAddressRemarkName.getText().toString());
        entity.setAssetId(((IAddress) getHoldingActivity()).getModel().getAssetId() + "");
        entity.setAssetName(((IAddress) getHoldingActivity()).getModel().getAssetName());
        Http.getWalletService().withDraw(entity)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {

                    @Override
                    public void onNext(BaseHttpEntity baseHttpEntity) {
                        AddAddressFragment.this.notify(R.id.event_notify_address_edit);
                        onLeftClick();
                    }
                });
    }


    @Override
    public FragmentManagerDelegate getChildFragmentManagerDelegate(Fragment fragment) {
        return null;
    }

    @Override
    public Context getContext() {
        return null;
    }
}
