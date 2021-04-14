package com.fota.android.moudles.wallet.transfer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.common.bean.wallet.ContractAccountBean;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.ToastUitl;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.core.base.BtbMap;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.core.event.Event;
import com.fota.android.databinding.FragmentContractBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.wallet.WithTransferFragment;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.widget.TitleLayout;
import com.fota.android.widget.btbwidget.FotaTextWatch;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 合约账户
 */
public class ContractAccountFragment extends WithTransferFragment implements View.OnClickListener {

    @Override
    protected String setAppTitle() {
        return getString(R.string.wallet_contact_account);
    }

    private FragmentContractBinding mBinding;
    //可用合约账户余额
    private double availableContract;
    private String availableContractStr;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contract, container, false);
        mBinding.setView(this);
        super.setBinding(mBinding);
        return mBinding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        mTitleLayout.setOnRightButtonClickListener(new TitleLayout.OnRightButtonClickListener() {
            @Override
            public void onRightButtonClick(View v) {
//                addFragment(new TransferHistoryFragment());
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromContract", true);
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.TransferHistoryFragment, bundle);
            }
        });

        mBinding.editTransfer.addTextChangedListener(new FotaTextWatch() {
            @Override
            protected void onTextChanged(String s) {
                try {
                    if (Pub.isTooLongCharacter(mBinding.editTransfer, 8)) {
                        return;
                    }
                    if (isMaxBalance(mBinding.editTransfer)) {
                        String maxAvalible = String.format(getString(R.string.transfer_max_usdt), availableContractStr);
                        ToastUitl.showShort(maxAvalible);
                        return;
                    }

                    double inputDouble = Pub.GetDouble(s.toString());
                    if (inputDouble >= 0.00000001) {
                        mBinding.btSure.setBtbEnabled(true);
                    } else
                        mBinding.btSure.setBtbEnabled(false);
                } catch (Exception e) {
                    mBinding.btSure.setBtbEnabled(false);
                }
            }
        });
        KeyBoardUtils.addOnKeyboardListener(mBinding.getRoot(), new KeyBoardUtils.OnKeyboardListener() {
            @Override
            public void changed(boolean isOpened) {
                if (!isOpened && !TextUtils.isEmpty(mBinding.editTransfer.getText())) {
                    double input = Pub.GetDouble(mBinding.editTransfer.getText().toString(), 0);
                    if (input < 0.00000001) {
                        //showMinToast();
                        showToast(R.string.transfer_min_usdt);
                    }
                }
            }
        });

        getFreshData();
        if (AppConfigs.getTheme() == 0) {
            mTitleLayout.setRightIcon(R.mipmap.trans_his_black);
        } else {
            mTitleLayout.setRightIcon(R.mipmap.trans_his_white);
        }
        mTitleLayout.setRightIconSize(UIUtil.dip2px(mContext, 18), UIUtil.dip2px(mContext, 19.5));
    }

    private void getFreshData() {
        Http.getWalletService().transferCheck()
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {

                    @Override
                    public void onNext(BaseHttpEntity baseHttpEntity) {
                    }
                });
        BtbMap map = new BtbMap();
        //获取 合约账户可用保证金
        Http.getWalletService().getContractAccount(map).compose(new CommonTransformer<ContractAccountBean>())
                .subscribe(new CommonSubscriber<ContractAccountBean>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(ContractAccountBean contract) {
                        if (contract == null) {
                            return;
                        }
                        contractAccountInfo = contract;
                        availableContract = Pub.GetDouble(contract.getAvailable());
                        availableContractStr = contract.getAvailable();
                        setAvailable(availableContract);
                        setAvailabelString(availableContractStr);
                        isContractToMyAccount = true;
                        String maxAvalible = contract.getAvailable();
                        mBinding.txtMaxTransferTips.setText(maxAvalible);
                        entry();
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        reset();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sure:
                submit();
                break;
            case R.id.txt_all_in:
                allIn();
                break;
        }
    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_capital_quickcheck_suc:
                if (!TextUtils.equals(mRequestCode, event.getParam(String.class))) {
                    return;
                }
                finalDo();
                break;
            case R.id.event_capital_quickcheck_fail:
                if (!TextUtils.equals(mRequestCode, event.getParam(String.class))) {
                    return;
                }
                pwdDialogShow();
                break;
        }
    }

    public void afterTransferDialog() {
        DialogUtils.showDialog(getContext(),
                new DialogModel(getString(R.string.wallet_exchange_ok))
                        .setSureText(getXmlString(R.string.transfer_see_history))
                        .setIcon(R.mipmap.icon_check_suc)
                        .setSureClickListen(new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                addFragment(new TransferHistoryFragment());
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("fromContract", true);
                                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.TransferHistoryFragment, bundle);
                            }
                        })
                        .setCanCancelOnTouchOutside(true)
        );
        getFreshData();
    }

    //进入 执行控件可用
    public void entry() {
        mBinding.usdtMoney.setText(contractAccountInfo.getTotal());
        mBinding.usdMoney.setText(" ≈ " + contractAccountInfo.getTotalValuation());
        mBinding.txtContractAll.setText(contractAccountInfo.getTotalValuation());
        mBinding.txtContractRemains.setText(contractAccountInfo.getAvailable());
        mBinding.txtContractUsed.setText(contractAccountInfo.getMargin());
        mBinding.txtContractLock.setText(contractAccountInfo.getLockedAmount());
        mBinding.txtContractFloating.setText(contractAccountInfo.getFloatProfit());

        super.entry();
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }
}
