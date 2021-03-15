package com.fota.android.moudles.wallet.index;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.ConstantsPage;
import com.fota.android.common.bean.wallet.WalletItem;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.databinding.FragmentWalletDatailsBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.MineBean;
import com.fota.android.moudles.wallet.history.WithDrawHistoryFragment;
import com.fota.android.moudles.wallet.recharge.RechargeMoneyFragment;
import com.fota.android.utils.FtRounts;
import com.fota.android.widget.TitleLayout;

public class WalletDetailsFragment extends BaseFragment implements View.OnClickListener {

    private FragmentWalletDatailsBinding mBinding;

    WalletItem model;

    public MineBean meData;

    public static WalletDetailsFragment newInstance(WalletItem model) {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.MODEL, model);
        WalletDetailsFragment fragment = new WalletDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet_datails, container, false);
        mBinding.setView(this);
        return mBinding.getRoot();
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        model = (WalletItem) bundle.getSerializable(BundleKeys.MODEL);
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        mTitleLayout.setRightText(getXmlString(R.string.wallet_withdraw_history));
        mTitleLayout.setRightTextColor(Pub.getColor(mContext, R.attr.login_regist_tv));
        mTitleLayout.setOnRightButtonClickListener(new TitleLayout.OnRightButtonClickListener() {
            @Override
            public void onRightButtonClick(View v) {
                addFragment(WithDrawHistoryFragment.newInstance(model.getAssetId()));
            }
        });
        initInfos();
        initBottomButton();
        setIsUsdt();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        Http.getHttpService().getMineData()
                .compose(new CommonTransformer<MineBean>())
                .subscribe(new CommonSubscriber<MineBean>(this) {

                    @Override
                    public void onNext(MineBean mineBean) {
                        meData = mineBean;
                    }

                    //jiang com.fota.android.commonlib.http.exception.ApiException:
                    // retrofit2.adapter.rxjava.HttpException: HTTP 500
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
                });
    }

    /**
     * usdt底部界面不一样
     */
    private void setIsUsdt() {
        boolean isUsdt = "BTC".equals(model.getAssetName());
        boolean isFota = "FOTA".equals(model.getAssetName());

        //UIUtil.setVisibility(mBinding.btSure, isUsdt);
//        UIUtil.setVisibility(mBinding.usdtLayout, isUsdt);
//        UIUtil.setVisibility(mBinding.usdtBlock, isUsdt);
        UIUtil.setVisibility(mBinding.valuation, !isUsdt);

        UIUtil.setVisibility(mBinding.valuationLockedAccountLayout, !isUsdt);
        UIUtil.setVisibility(mBinding.lockedAccountAmountLayout, isFota);
    }

    @Override
    protected boolean viewGroupFocused() {
        return true;
    }

    /**
     * 初始化信息
     */
    private void initInfos() {
        mBinding.assetName.setText(model.getAssetName());
        mBinding.amount.setText(model.getAmount() + model.getAssetName());
        mBinding.lockedAmount.setText(model.getLockedAmount() + model.getAssetName());
        mBinding.lockedAccountAmount.setText(model.getLockAccountAmount() + model.getAssetName());
        mBinding.valuation.setText(model.getValuation() + "BTC");
    }

    private void initBottomButton() {
        mBinding.rechargeButton.setButtonStyle(true);
        mBinding.withdrawButton.setButtonStyle(false);
    }

    @Override
    protected String setAppTitle() {
        return model.getAssetName();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recharge_button:
                addFragment(RechargeMoneyFragment.newInstance(model));
                //充币
                break;
            case R.id.withdraw_button:
                //提币
                if (meData == null) {
                    return;
                }
                if (meData.getUserSecurity() == null) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                if (!meData.getUserSecurity().isPhoneAuth()) {
                    sb.append(getString(R.string.first_bind_phone));
                    sb.append(" ");
                }
                if (!meData.getUserSecurity().isGoogleAuth()) {
                    sb.append(getString(R.string.first_bind_google));
                    sb.append(" ");
                }
                if (!meData.getUserSecurity().isFundPwdSet()) {
                    sb.append(getString(R.string.first_bind_asset));
                    sb.append(" ");
                }
                if (sb.length() > 0) {
                    String needInfo = getXmlString(R.string.wallet_withdraw_need);
                    DialogUtils.showDialog(getContext(),
                            new DialogModel(sb.toString())
                                    .setTitle(needInfo)
                                    .setSureText(getXmlString(R.string.common_goto_ret))
                                    .setSureClickListen(new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SimpleFragmentActivity.gotoFragmentActivity(getContext(),
                                                    ConstantsPage.SafeSettingFragment
                                            );
                                        }

                                    })
                                    .setCancelText(getXmlString(R.string.cancel))
                    );
                    return;
                }
                FtRounts.toWithdrawActivity(getContext(), model);
                break;
            default:
                break;
        }
    }


    @Override
    public boolean eventEnable() {
        return true;
    }


}
