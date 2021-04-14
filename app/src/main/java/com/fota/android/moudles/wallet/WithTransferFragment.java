package com.fota.android.moudles.wallet;

import android.content.DialogInterface;
import android.text.Editable;
import android.text.Selection;
import android.widget.EditText;

import androidx.databinding.ViewDataBinding;

import com.blankj.utilcode.util.StringUtils;
import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.common.bean.wallet.ContractAccountBean;
import com.fota.android.common.bean.wallet.TransferBean;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.databinding.FragmentContractBinding;
import com.fota.android.databinding.FragmentUsdtTranferBinding;
import com.fota.android.databinding.WalletFragmentHeadBinding;
import com.fota.android.http.Http;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.widget.btbwidget.ClearEdittext;
import com.fota.android.widget.btbwidget.FotaButton;

/**
 * 带有划转功能模块的虚类Fragment
 * 划转功能模块的Fragment需要从此继承
 */
public abstract class WithTransferFragment<P extends BasePresenter> extends MvpListFragment<P> implements TransferInterface {
    private ClearEdittext editTransfer;
    private FotaButton btSure;

    public void setBinding(ViewDataBinding binding) {
        if (binding instanceof WalletFragmentHeadBinding) {
//            editTransfer = ((WalletFragmentHeadBinding)binding).editTransfer;
//            btSure = ((WalletFragmentHeadBinding)binding).btSure;
        } else if (binding instanceof FragmentContractBinding) {
            editTransfer = ((FragmentContractBinding) binding).editTransfer;
            btSure = ((FragmentContractBinding) binding).btSure;
        } else if (binding instanceof FragmentUsdtTranferBinding) {
            editTransfer = ((FragmentUsdtTranferBinding) binding).editTransfer;
            btSure = ((FragmentUsdtTranferBinding) binding).btSure;
        } else {
            editTransfer = new ClearEdittext(getContext());
            btSure = new FotaButton(getContext());
        }
    }

    public void setView(ClearEdittext edittext, FotaButton button) {
        editTransfer = edittext;
        btSure = button;
    }

    //可用余额 //要看划转方向的
    protected double availableValue;
    protected String availabelString;

    public void setAvailabelString(String availabelString) {
        this.availabelString = availabelString;
    }

    protected boolean isContractToMyAccount;
    protected ContractAccountBean contractAccountInfo;

    public void setAvailable(double available) {
        this.availableValue = available;
    }

    public void allIn() {
        editTransfer.setText(availabelString);
    }

    public void submit() {
        boolean tips = false;
        if (isContractToMyAccount) {
            if (contractAccountInfo != null) {
                if (!StringUtils.isEmpty(contractAccountInfo.getLockedAmount())) {
                    double locked = Double.parseDouble(contractAccountInfo.getLockedAmount());
                    if (locked > 0) {
                        tips = true;
                        showContractDialog();
                    }
                }
                if (!tips && !StringUtils.isEmpty(contractAccountInfo.getMargin())) {
                    double margin = Double.parseDouble(contractAccountInfo.getMargin());
                    if (margin > 0) {
                        tips = true;
                        showContractDialog();
                    }
                }
            }
        }
        if (!tips) {
            onConfirm();
        }
    }

    /**
     * 转出合约保证金，需要提示
     */
    private void showContractDialog() {
        DialogUtils.showDialog(getContext(), new DialogModel()
                .setMessage(getString(R.string.transfer_bom_account))
                .setCancelText(getXmlString(R.string.cancel))
                .setSureText(getString(R.string.transfer_continue))
                .setSureClickListen(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onConfirm();
                    }
                })
        );
    }

    //1205 jiang 改为同交易下单逻辑，两小时内输过密码，可以不用继续输入
    public void onConfirm() {
        finalDo();
//        Http.getWalletService().transferCheck()
//                .compose(new NothingTransformer<BaseHttpEntity>())
//                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {
//
//                    @Override
//                    public void onNext(BaseHttpEntity baseHttpEntity) {
//                        if (getView() != null) {
//                            //验证密码是否
//                            TradeUtils.getInstance().validPassword(getContext(), mRequestCode, new TradeUtils.ExchangePasswordListener() {
//
//                                @Override
//                                public void noPassword() {
//
//                                }
//
//                                @Override
//                                public void showPasswordDialog() {
//                                    pwdDialogShow();
//                                }
//                            });
//                        }
//                    }
//                });
    }

    /**
     * 弹出密码框
     */
    public void pwdDialogShow() {
        if (getHoldingActivity().isFinishing()) {
            return;
        }

        finalDo();
//        PasswordDialog dialog = new PasswordDialog(getContext());
//        dialog.setListener(new PasswordDialog.OnSureClickListener() {
//            @Override
//            public void onClick(String fundCode) {
//                TradeUtils.getInstance().changePasswordToToken(getContext(), fundCode,
//                        new TradeUtils.ChangePassWordListener() {
//                            @Override
//                            public void setPasswordToken(String token) {
//
//                            }
//                        });
//            }
//        });
//        dialog.show();
    }

    public void finalDo() {
        TransferBean body = new TransferBean();
        body.setFromType(isContractToMyAccount ? 2 : 1);
        body.setToType(isContractToMyAccount ? 1 : 2);
        body.setAmount(editTransfer.getText().toString());
//        body.setTradeToken(fundCode);
        Http.getWalletService().transfer(body).compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(BaseHttpEntity baseHttpEntity) {
//                        showToast(getString(R.string.wallet_exchange_ok));
                        reset();
                        afterTransferDialog();
                        KeyBoardUtils.closeKeybord(mContext);
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                    }
                });
    }

    protected boolean isMaxBalance(EditText editText) {
        Editable editable = editText.getText();
        double netBalance = availableValue;

        try {
            double input = Double.parseDouble(editable.toString());
            if (input > netBalance) {
                editText.setText(availabelString);
                //设置新光标所在的位置
                editable = editText.getText();
                Selection.setSelection(editable, editable.length());
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    //进入 执行控件可用
    public void entry() {
        editTransfer.setEnabled(true);
        btSure.setBtbEnabled(false);
    }

    //离开 执行控件不可用
    public void leave() {
        btSure.setBtbEnabled(false);
        editTransfer.setEnabled(false);
    }

    //重置
    public void reset() {
        editTransfer.setText("");
        editTransfer.setEnabled(true);
        btSure.setBtbEnabled(false);
    }

}
