package com.fota.android.moudles.mine.safe;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.ConstantsPage;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.core.event.Event;
import com.fota.android.databinding.FragmentMineSafeBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.MineBean;
import com.fota.android.utils.UserLoginUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 安全设置
 */
public class SafeSettingFragment extends BaseFragment implements View.OnClickListener {
    FragmentMineSafeBinding mBinding;
    private MineBean.UserSecurity userSecurity;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_bindphone:
                if (TextUtils.isEmpty(UserLoginUtil.getPhone())) {
                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.BindPhoneFragment);
                } else {
                    if (userSecurity == null)
                        return;
                    if (userSecurity.isGoogleAuth()) {
                        SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.BindPhoneFragment);
                    } else {
                        DialogUtils.showDialog(getContext(), new DialogModel()
                                //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                                .setMessage(getString(R.string.safesetting_google))
                                .setSureText(getString(R.string.safesetting_unbind))
                                .setCancelText(getString(R.string.cancel))
                                .setSureClickListen(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.BindGoogleFragment);
                                        dialogInterface.dismiss();
                                    }
                                }).setCancelClickListen(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.dismiss();
                                    }
                                })
                        );
                    }

                }
                break;
            case R.id.rl_bindemail:
                if (TextUtils.isEmpty(UserLoginUtil.getEmail()))
                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.BindEmailFragment);
                break;
            case R.id.rl_nick_name:
                if (userSecurity == null)
                    return;
                Bundle bundle = new Bundle();
                bundle.putString(BundleKeys.KEY, userSecurity.getUserName());
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.NickNameFragment, bundle);
                break;
            case R.id.rl_bindgoogle:
                if (userSecurity == null)
                    return;
                if (userSecurity.isGoogleAuth()) {
                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.UNBindGoogleFragment);
                } else {
                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.BindGoogleFragment);
                }
                break;
            case R.id.rl_shoushi:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.GestureFragment);
                break;
            case R.id.rl_password:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.ChangePswFragment);
                break;
            case R.id.rl_zijin:
                if (userSecurity == null)
                    return;
                if (!userSecurity.isGoogleAuth()) {
                    DialogUtils.showDialog(getContext(), new DialogModel()
                            //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                            .setMessage(mContext.getResources().getString(R.string.first_bind_google))
                            .setSureText(getString(R.string.safesetting_unbind))
                            .setCancelText(getString(R.string.cancel))
                            .setCancelable(true)
                            .setSureClickListen(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.BindGoogleFragment);
                                    dialogInterface.dismiss();
                                }
                            }).setCancelClickListen(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                }
                            })
                    );
                    return;
                }
                if (userSecurity.isFundPwdSet()) {
                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.CapitalResetFragment);
                } else {
                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.CapitalSetFragment);
                }
////                SimpleFragmentActivity.gotoFragmentActivity(getContext(), CapitalSetFragment);
//                SimpleFragmentActivity.gotoFragmentActivity(getContext(), CapitalResetFragment);
                break;
            case R.id.rl_zhiwen:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.FingerFragment);
                break;
            default:
                break;
        }

    }

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine_safe, container, false);
        mBinding.rlBindemail.setOnClickListener(this);
        mBinding.rlPassword.setOnClickListener(this);
        mBinding.rlBindphone.setOnClickListener(this);
        mBinding.rlBindgoogle.setOnClickListener(this);
        mBinding.rlShoushi.setOnClickListener(this);
        mBinding.rlZhiwen.setOnClickListener(this);
        mBinding.rlZijin.setOnClickListener(this);
        mBinding.rlNickName.setOnClickListener(this);
        return mBinding.getRoot();
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        userSecurity = (MineBean.UserSecurity) bundle.getSerializable("security");
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
//        setStatus();

    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safesetting_title);
    }

    /**
     * 各项状态设置
     */
    private void setStatus() {
        Drawable drawableRight = getResources().getDrawable(Pub.getThemeResource(mContext, R.attr.icon_right));
        Drawable drawableleft_unset = getResources().getDrawable(
                R.mipmap.safe_icon_insetted);
        Drawable drawableleft_set = getResources().getDrawable(
                R.mipmap.safe_icon_setted);


        if (!TextUtils.isEmpty(UserLoginUtil.getPhone())) {
            mBinding.tvBindphone.setText(UserLoginUtil.getPhone());
            mBinding.tvBindphone.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);

        } else {
            mBinding.tvBindphone.setText(R.string.safesetting_unbind);
            mBinding.tvBindphone.setCompoundDrawablesWithIntrinsicBounds(drawableleft_unset, null, drawableRight, null);
        }


        if (!TextUtils.isEmpty(UserLoginUtil.getEmail())) {
            mBinding.tvEmail.setText(UserLoginUtil.getEmail());
            mBinding.tvEmail.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
        } else {
            mBinding.tvEmail.setText(R.string.safesetting_unbind);
            mBinding.tvEmail.setCompoundDrawablesWithIntrinsicBounds(drawableleft_unset, null, drawableRight, null);
        }

        if (userSecurity == null)
            return;

        if (!TextUtils.isEmpty(userSecurity.getUserName())) {
            mBinding.tvNickName.setText(userSecurity.getUserName());
            mBinding.tvNickName.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
        } else {
            mBinding.tvNickName.setText(R.string.goto_set_quicklog);
            mBinding.tvNickName.setCompoundDrawablesWithIntrinsicBounds(drawableleft_unset, null, drawableRight, null);
        }


        if (userSecurity.isGoogleAuth()) {
            mBinding.tvGoogle.setText(R.string.safesetting_setted);
            mBinding.tvGoogle.setCompoundDrawablesWithIntrinsicBounds(drawableleft_set, null, drawableRight, null);
        } else {
            mBinding.tvGoogle.setText(R.string.safesetting_unsetting);
            mBinding.tvGoogle.setCompoundDrawablesWithIntrinsicBounds(drawableleft_unset, null, drawableRight, null);
        }

        if (userSecurity.isFundPwdSet()) {
            mBinding.tvZijin.setText(R.string.safe_capitalreset_change);
            mBinding.tvZijin.setCompoundDrawablesWithIntrinsicBounds(drawableleft_set, null, drawableRight, null);
        } else {
            mBinding.tvZijin.setText(R.string.safesetting_unsetting);
            mBinding.tvZijin.setCompoundDrawablesWithIntrinsicBounds(drawableleft_unset, null, drawableRight, null);
        }

        if (UserLoginUtil.getFingerOpen()) {
            mBinding.tvZhiwen.setText(R.string.safesetting_setted);
            mBinding.tvZhiwen.setCompoundDrawablesWithIntrinsicBounds(drawableleft_set, null, drawableRight, null);
        } else {
            mBinding.tvZhiwen.setText(R.string.safesetting_unsetting);
            mBinding.tvZhiwen.setCompoundDrawablesWithIntrinsicBounds(drawableleft_unset, null, drawableRight, null);
        }

        if (!TextUtils.isEmpty(UserLoginUtil.getLoginedGesture())) {
            mBinding.tvShoushi.setCompoundDrawablesWithIntrinsicBounds(drawableleft_set, null, drawableRight, null);

            mBinding.tvShoushi.setText(R.string.safesetting_setted);
        } else {
            mBinding.tvShoushi.setCompoundDrawablesWithIntrinsicBounds(drawableleft_unset, null, drawableRight, null);

            mBinding.tvShoushi.setText(R.string.safesetting_unsetting);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!UserLoginUtil.havaUser())
            return;
        setStatus();
        getMindeMsg();
    }


    @Override
    public boolean eventEnable() {
        return true;
    }

    /**
     * 获取我的数据
     */
    public void getMindeMsg() {
        Http.getHttpService().getMineData()
                .compose(new CommonTransformer<MineBean>())
                .subscribe(new CommonSubscriber<MineBean>(this) {
                    @Override
                    public void onNext(MineBean mineBean) {
                        if (getView() == null) {
                            return;
                        }

                        if (mineBean != null && mineBean.getUserSecurity() != null) {
                            userSecurity = mineBean.getUserSecurity();
                            setStatus();
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                    }

                    @Override
                    protected boolean showLoading() {
                        return false;
                    }

                });
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.safe_refresh://更新安全设置信息
                MineBean.UserSecurity userSecurity = event.getParam(MineBean.UserSecurity.class);
                if (userSecurity != null) {
                    this.userSecurity = userSecurity;
                    setStatus();
                }
                break;
            case R.id.safe_finish://
                getActivity().finish();
                break;
        }
    }
}
