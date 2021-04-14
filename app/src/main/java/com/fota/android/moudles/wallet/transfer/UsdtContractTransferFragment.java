package com.fota.android.moudles.wallet.transfer;

import android.animation.ObjectAnimator;
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
import com.fota.android.common.bean.wallet.WalletBean;
import com.fota.android.common.bean.wallet.WalletItem;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.ToastUitl;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.anim.ViewWrapper;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.core.base.BtbMap;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.core.event.Event;
import com.fota.android.databinding.FragmentUsdtTranferBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.wallet.WithTransferFragment;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.widget.TitleLayout;
import com.fota.android.widget.btbwidget.FotaTextWatch;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * 划转界面
 */
public class UsdtContractTransferFragment extends WithTransferFragment implements View.OnClickListener {

    private FragmentUsdtTranferBinding mBinding;
    //可用合约账户余额
    private double availableContract;
    //可用我的钱包余额
    private double availableWallet;

    private String wallToContract;//合钱包最大可划转
    private String contractToWallet;//保证金最大可划转

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_trans:
                isContractToMyAccount = !isContractToMyAccount;
                setAvailable(isContractToMyAccount ? availableContract : availableWallet);
                setAvailabelString(isContractToMyAccount ? contractToWallet : wallToContract);
                refreshDirection();
                break;
            case R.id.bt_sure:
                submit();
                break;
            case R.id.txt_all_in:
                allIn();
                break;
            case R.id.ll_wallet:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.WalletFragment);
                break;
            case R.id.ll_bzjaccount:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.ContractAccountFragment);
                break;
        }
    }

    public void afterTransferDialog() {
        if (!isContractToMyAccount) {
            DialogUtils.showDialog(getContext(),
                    new DialogModel(getString(R.string.wallet_exchange_ok))
                            .setSureText(getXmlString(R.string.wallet_trade_right_now))
                            .setIcon(R.mipmap.icon_check_suc)
                            .setSureClickListen(new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.FuturesFragment);
                                }
                            })
                            .setCancelText(getXmlString(R.string.transfer_see_history))
                            .setCancelClickListen(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    addFragment(new TransferHistoryFragment());

                                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.TransferHistoryFragment, null);
                                }
                            }).setCanCancelOnTouchOutside(true)
            );
        } else {
            DialogUtils.showDialog(getContext(),
                    new DialogModel(getString(R.string.wallet_exchange_ok))
                            .setIcon(R.mipmap.icon_check_suc)
                            .setSureText(getXmlString(R.string.transfer_see_history))
                            .setSureClickListen(new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    addFragment(new TransferHistoryFragment());
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("fromContract", true);
                                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.TransferHistoryFragment, bundle);

                                }
                            })
                            .setCanCancelOnTouchOutside(true)
            );
        }

        getFreshData();
    }

    /**
     * 刷新划转方向相关
     */
    private void refreshDirection() {
        doViewAnim();
        String maxStr = isContractToMyAccount ? contractToWallet : wallToContract;
        String maxAvalible = maxStr;
        mBinding.txtMaxTransferTips.setText(maxAvalible);

        isMaxBalance(mBinding.editTransfer);
    }

    private void doViewAnim() {
//        int smallHeight = UIUtil.dip2px(getContext(), 21);
//        int allHeight = UIUtil.dip2px(getContext(), 62);
//        if (isContractToMyAccount) {
//            doAnimTransf(mBinding.contractAccountLayout, "height", smallHeight);
//            doAnimTransf(mBinding.myAccountLayout, "height", allHeight);
//            // AnimUtil.flipAnimatorXViewShow(mBinding.changeDirection1, mBinding.changeDirection2, 10);
//        } else {
//            doAnimTransf(mBinding.contractAccountLayout, "height", allHeight);
//            doAnimTransf(mBinding.myAccountLayout, "height", smallHeight);
//            // AnimUtil.flipAnimatorXViewShow(mBinding.changeDirection2, mBinding.changeDirection1, 10);
//        }
        if (isContractToMyAccount) {
            mBinding.tvTo.setText(mContext.getString(R.string.mine_wallet));
            mBinding.tvFrom.setText(mContext.getString(R.string.mine_heyueusdt));
        } else {
            mBinding.tvFrom.setText(mContext.getString(R.string.mine_wallet));
            mBinding.tvTo.setText(mContext.getString(R.string.mine_heyueusdt));
        }
    }

    void doAnimTransf(View view, String po, int value) {
        ViewWrapper viewWrapper = new ViewWrapper(view);
        ObjectAnimator.ofInt(viewWrapper, po, value).setDuration(20).start();
    }

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_usdt_tranfer, container, false);
        mBinding.setView(this);
        super.setBinding(mBinding);
        return mBinding.getRoot();
    }

    protected String setAppTitle() {
        return getString(R.string.mine_assetmanage);
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        mTitleLayout.setOnRightButtonClickListener(new TitleLayout.OnRightButtonClickListener() {
            @Override
            public void onRightButtonClick(View v) {
//                addFragment(new TransferHistoryFragment());
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.TransferHistoryFragment, null);

            }
        });
        mBinding.btSure.setBtbEnabled(false);
        mBinding.rlTrans.setOnClickListener(this);
        mBinding.llWallet.setOnClickListener(this);
        mBinding.llBzjaccount.setOnClickListener(this);
//        GradientDrawableUtils.setBgColor(mBinding.viewMyFuture, ContextCompat.getColor(getContext(), R.color.red_up));
//        GradientDrawableUtils.setBgColor(mBinding.viewMyWallet, ContextCompat.getColor(getContext(), R.color.check_color));

        mBinding.editTransfer.addTextChangedListener(new FotaTextWatch() {
            @Override
            protected void onTextChanged(String s) {
                try {
                    if (Pub.isTooLongCharacter(mBinding.editTransfer, 8)) {
                        return;
                    }
                    if (isMaxBalance(mBinding.editTransfer)) {
                        String netBalance = isContractToMyAccount ? contractToWallet : wallToContract;
                        String maxAvalible = String.format(getString(R.string.transfer_max_usdt), netBalance);
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
        //获取 可用usdt
        Http.getWalletService().getAailableUsdt(map).compose(new CommonTransformer<WalletBean>())
                .subscribe(new CommonSubscriber<WalletBean>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(WalletBean walletBean) {
                        if (walletBean == null) {
                            return;
                        }

                        List<WalletItem> walletItems = walletBean.getItem();
                        if (walletItems == null || walletItems.size() <= 0) {
                            return;
                        }
                        mBinding.tvWallet.setText(walletBean.getTotalValuation());

                        for (WalletItem each : walletItems) {
                            if ("USDT".equalsIgnoreCase(each.getAssetName())) {
                                wallToContract = each.getAmount();
                                availableWallet = Pub.GetDouble(each.getAmount());
                                setAvailable(isContractToMyAccount ? availableContract : availableWallet);
                                setAvailabelString(isContractToMyAccount ? contractToWallet : wallToContract);
                                if (!isContractToMyAccount) {
                                    String maxAvalible = each.getAmount();
                                    mBinding.txtMaxTransferTips.setText(maxAvalible);
                                }
                                break;
                            }
                        }
                        entry();
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        leave();
                    }
                });
        BtbMap temp = new BtbMap();
        //获取 合约账户可用保证金
        Http.getWalletService().getContractAccount(temp).compose(new CommonTransformer<ContractAccountBean>())
                .subscribe(new CommonSubscriber<ContractAccountBean>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(ContractAccountBean contract) {
                        if (contract == null) {
                            return;
                        }
                        contractAccountInfo = contract;
                        availableContract = Double.parseDouble(contract.getAvailable());
                        contractToWallet = contract.getAvailable();
                        entry();
                        if (isContractToMyAccount) {
                            String maxAvalible = contractToWallet;
                            mBinding.txtMaxTransferTips.setText(maxAvalible);
                        }
                        mBinding.tvHeyueusdt.setText(contract.getTotal());
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        leave();
                    }
                });
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
//        String wallet = bundle.getString("wallet");
//        String contract = bundle.getString("contract");
//
//        try {
//            availableContract = Double.parseDouble(contract);
//            availableWallet = Double.parseDouble(wallet);
//        } catch (NumberFormatException e) {
//            L.e(e.getMessage());
//        }
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

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public void onDestroy() {
        KeyBoardUtils.removeOnKeybroadListener(mBinding.getRoot());
        super.onDestroy();
    }
}
