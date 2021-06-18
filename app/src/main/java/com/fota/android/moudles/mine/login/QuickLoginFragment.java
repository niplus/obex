package com.fota.android.moudles.mine.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentQuickloginBinding;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.utils.apputils.MineInfoUtil;
import com.fota.android.widget.popwin.CommomDialog;
import com.takwolf.android.lock9.Lock9View;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

/**
 * 指纹或手势登录
 */
public class QuickLoginFragment extends BaseFragment implements View.OnClickListener {
    FragmentQuickloginBinding mBinding;
    private FingerprintIdentify mFingerprintIdentify;
    private static final int MAX_AVAILABLE_TIMES = 3;
    CommomDialog dialog;
    String loginTo = "";//标记跳转到的页面
    int gestureerrorTimes = 0;


    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_quicklogin, container, false);
        return mBinding.getRoot();
    }

    /**
     * 初始状态设置
     * 1 手势
     * 2 指纹
     * 3 都设置了
     */
    private void initStatus(int status) {
        if (1 == status) {
            mBinding.tvLoginPsw1.setVisibility(View.VISIBLE);
            mBinding.lock.setVisibility(View.VISIBLE);
            mBinding.imvFinger.setVisibility(View.GONE);
            mBinding.llFinger.setVisibility(View.GONE);
        } else if (2 == status) {
            mBinding.tvLoginPsw1.setVisibility(View.VISIBLE);
            mBinding.lock.setVisibility(View.GONE);
            mBinding.imvFinger.setVisibility(View.VISIBLE);
            mBinding.llFinger.setVisibility(View.GONE);
        } else if (3 == status) {
            mBinding.tvLoginPsw1.setVisibility(View.GONE);
            mBinding.lock.setVisibility(View.VISIBLE);
            mBinding.imvFinger.setVisibility(View.GONE);
            mBinding.llFinger.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected String setAppTitle() {
        return "";
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        setJustWhiteBarTxt();
        mBinding.imvFinger2.setOnClickListener(this);
        mBinding.tvLoginPsw1.setOnClickListener(this);
        mBinding.tvLoginPsw2.setOnClickListener(this);
        mBinding.tvFinger.setOnClickListener(this);
        mTitleLayout.setAppBackground(getColor(R.color.transparent));
        mTitleLayout.setLeftIcon(R.mipmap.icon_back);


        if (!TextUtils.isEmpty(UserLoginUtil.getPhone())) {
            mBinding.account.setText(hidePhone(UserLoginUtil.getPhone()));
        } else if (!TextUtils.isEmpty(UserLoginUtil.getEmail())) {
            mBinding.account.setText(hideEmaile(UserLoginUtil.getEmail()));
        }
        if (UserLoginUtil.getFingerOpen()) {


            mFingerprintIdentify = new FingerprintIdentify(FotaApplication.getInstance());
            mFingerprintIdentify.setExceptionListener(new BaseFingerprint.ExceptionListener() {
                @Override
                public void onCatchException(Throwable exception) {
                    L.a("finger Exception：" + exception.getLocalizedMessage());
                }
            });
            mFingerprintIdentify.init();
            initFinger();
            if (TextUtils.isEmpty(UserLoginUtil.getLoginedGesture())) {
                initStatus(2);
                return;
            } else {
                initStatus(3);
            }

        }
        if (!TextUtils.isEmpty(UserLoginUtil.getLoginedGesture())) {
            mBinding.lock.setGestureCallback(new Lock9View.GestureCallback() {

                @Override
                public void onNodeConnected(@NonNull int[] numbers) {
//                ToastUtils.with(NormalActivity.this).show("+ " + numbers[numbers.length - 1]);
                }

                @Override
                public void onGestureFinished(@NonNull int[] numbers) {
                    if (numbers.length >= 4) {
                        mBinding.lock.clearStatus();
                        String savedGesture = UserLoginUtil.getLoginedGesture();
                        StringBuilder builder = new StringBuilder();
                        for (int number : numbers) {
                            builder.append(number);
                        }
                        String gesture = builder.toString();
                        if (gesture.equals(savedGesture)) {
                            FotaApplication.setLoginStatus(true);
                            SimpleFragmentActivity.gotoFragmentActivity(getContext(), loginTo);
                            AppConfigs.notifyDataChanged();
//                            EventWrapper.post(Event.create(R.id.event_main_changelanguage));
                            EventWrapper.post(Event.create(R.id.mine_refresh));//通知我的页面更新
                            MineInfoUtil.getMindeMsg();
                            finish();
                        } else {
                            gestureerrorTimes++;
                            if (gestureerrorTimes < 5) {
                                showToast(getString(R.string.safe_gesture_check_error, 5 - gestureerrorTimes));
                                mBinding.tvGesturetime.setVisibility(View.VISIBLE);
                                mBinding.tvGesturetime.setText(getString(R.string.safe_gesture_check_error, 5 - gestureerrorTimes));
                                Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(mContext,
                                        R.anim.gesture_notmatch);
                                mBinding.tvGesturetime.startAnimation(hyperspaceJumpAnimation);
                            } else {
                                mBinding.tvGesturetime.setVisibility(View.GONE);
                                DialogUtils.showDialog(getContext(), new DialogModel()
                                        //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                                        .setMessage(mContext.getResources().getString(R.string.safe_gesture_check_error5times))
                                        .setSureText(getString(R.string.sure))
                                        .setCancelable(false)
                                        .setGravityCenter(Gravity.CENTER)
                                        .setCanCancelOnTouchOutside(false)
                                        .setSureClickListen(new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                FtRounts.toLogin(mContext, loginTo);
                                                finish();
                                            }
                                        })
                                );
                            }
                        }
                    } else {
                        showToast(mContext.getResources().getString(R.string.safe_gesture_short));
                        mBinding.lock.clearStatus();
                    }

                }

            });
            if (!UserLoginUtil.getFingerOpen())
                initStatus(1);
        }
        gestureerrorTimes = 0;

    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        if (bundle != null)
            loginTo = bundle.getString(BundleKeys.LOGIN_JUMPKEY, "");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login_psw1:
            case R.id.tv_login_psw2:
                FtRounts.toLogin(mContext, loginTo);
                finish();
                break;
            case R.id.tv_finger:
            case R.id.imv_finger2:
                initFinger();
                break;
        }
    }

    private String hidePhone(String str) {
        return StringFormatUtils.getHidePhone(str);
    }

    private String hideEmaile(String str) {
        return StringFormatUtils.getHideEmail(str);
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
                            .setGravityCenter(Gravity.CENTER)
                            .setSureClickListen(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                getPresenter().logOut();
                                    dialogInterface.dismiss();
                                }
                            })
            );
            return;
        }
        if (!mFingerprintIdentify.isFingerprintEnable()) {//设备支持但没有录入指纹
            DialogUtils.showDialog(getContext(), new DialogModel()
                            //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                            .setMessage(mContext.getResources().getString(R.string.safe_finger_nofinger))
                            .setSureText(getString(R.string.sure))
                            .setGravityCenter(Gravity.CENTER)
                            .setCanCancelOnTouchOutside(false)
                            .setSureClickListen(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                getPresenter().logOut();
                                    dialogInterface.dismiss();
                                }
                            })
            );
            return;
        }
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

    private void identity() {
        mFingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, new BaseFingerprint.IdentifyListener() {
            @Override
            public void onSucceed() {
                FotaApplication.setLoginStatus(true);
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), loginTo);
//                ToastUtils.showShort("登录成功");
                AppConfigs.notifyDataChanged();
//                EventWrapper.post(Event.create(R.id.event_main_changelanguage));
                EventWrapper.post(Event.create(R.id.mine_refresh));//通知我的页面更新
//                showToast("登录成功");
                MineInfoUtil.getMindeMsg();
                finish();
            }

            @Override
            public void onNotMatch(int availableTimes) {
                showToast(getString(R.string.safe_finger_check_fail));
            }

            @Override
            public void onFailed(boolean isDeviceLocked) {
//                if (isDeviceLocked) {
                showToast(mContext.getResources().getString(R.string.safe_finger_check_fail));
                if (dialog != null)
                    dialog.dismiss();
//                    Skip.toLogin(mContext);
//                    finish();
//                }

            }

            @Override
            public void onStartFailedByDeviceLocked() {
                showToast(mContext.getResources().getString(R.string.safe_finger_device_lock));
                if (dialog != null)
                    dialog.dismiss();
//                Skip.toLogin(mContext);
//                finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onDestroyView();
    }
}
