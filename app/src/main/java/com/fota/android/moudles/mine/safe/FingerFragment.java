package com.fota.android.moudles.mine.safe;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.databinding.FragmentFingerBinding;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.widget.popwin.CommomDialog;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

/**
 * 指纹密码
 */
public class FingerFragment extends BaseFragment {
    FragmentFingerBinding mBinding;
    private FingerprintIdentify mFingerprintIdentify;

    private static final int MAX_AVAILABLE_TIMES = 3;
    CommomDialog dialog;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_finger, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safe_finger_title);
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        mFingerprintIdentify = new FingerprintIdentify(FotaApplication.getInstance() );

        mFingerprintIdentify.setExceptionListener(new BaseFingerprint.ExceptionListener() {
            @Override
            public void onCatchException(Throwable exception) {
                L.a("finger Exception：" + exception.getLocalizedMessage());
            }
        });

        initFinger();
        mBinding.cbFinger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    UserLoginUtil.setFingerOpen(false);
//                    showToast(mContext.getResources().getString(R.string.safe_finger_close_suc));
                } else {
                    if (UserLoginUtil.getFingerOpen())//已经设置了直接返回
                        return;
                    if (mFingerprintIdentify.isFingerprintEnable()) {
                        mBinding.cbFinger.setChecked(false);
                        dialog = DialogUtils.getDialog(getContext(), new DialogModel()
                                //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                                .setMessage(mContext.getResources().getString(R.string.safe_finger_check_msg))
                                .setCancelText(getString(R.string.cancel))
                                .setGravityCenter(Gravity.CENTER)
                                .setCancelClickListen(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.dismiss();
                                    }
                                })
                        );
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                mFingerprintIdentify.cancelIdentify();
                            }
                        });
                        dialog.show();
                        identity();
                    }
                }

            }
        });
    }

    private void identity() {
        mFingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, new BaseFingerprint.IdentifyListener() {
            @Override
            public void onSucceed() {
//                showToast(mContext.getResources().getString(R.string.safe_finger_set_suc));
                if (dialog != null) {
                    dialog.dismiss();
                }
                UserLoginUtil.setFingerOpen(true);
                mBinding.cbFinger.setChecked(true);
            }

            @Override
            public void onNotMatch(int availableTimes) {
                showToast(getString(R.string.safe_finger_check_fail));
            }

            @Override
            public void onFailed(boolean isDeviceLocked) {
//                if (isDeviceLocked)
                showToast(mContext.getResources().getString(R.string.safe_finger_check_fail));
                if (dialog != null)
                    dialog.dismiss();
            }

            @Override
            public void onStartFailedByDeviceLocked() {
                showToast(mContext.getResources().getString(R.string.safe_finger_device_lock));
                if (dialog != null)
                    dialog.dismiss();
            }
        });
    }

    /**
     * 初始化指纹提示内容
     */
    private void initFinger() {
        if (!mFingerprintIdentify.isHardwareEnable()) {//设备不支持
            DialogUtils.showDialog(getContext(), new DialogModel()
                            //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                            .setMessage(mContext.getResources().getString(R.string.safe_finger_device_notsupport))
                            .setSureText(getString(R.string.sure))
                            .setCancelable(false)
                            .setGravityCenter(Gravity.CENTER)
                            .setCanCancelOnTouchOutside(false)
                            .setSureClickListen(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                getPresenter().logOut();
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            })
            );
            mBinding.cbFinger.setChecked(false);
            mBinding.cbFinger.setEnabled(false);
            return;
        }
        if (!mFingerprintIdentify.isFingerprintEnable()) {//设备支持但没有录入指纹
            DialogUtils.showDialog(getContext(), new DialogModel()
                            //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                            .setMessage(mContext.getResources().getString(R.string.safe_finger_nofinger))
                            .setSureText(getString(R.string.sure))
                            .setCanCancelOnTouchOutside(false)
                            .setCancelable(false)
                            .setGravityCenter(Gravity.CENTER)
                            .setSureClickListen(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                getPresenter().logOut();
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            })
            );
            mBinding.cbFinger.setChecked(false);
            mBinding.cbFinger.setEnabled(false);
            return;
        }
        mBinding.cbFinger.setEnabled(true);
        if (UserLoginUtil.getFingerOpen()) {
            mBinding.cbFinger.setChecked(true);
        } else {
            mBinding.cbFinger.setChecked(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
