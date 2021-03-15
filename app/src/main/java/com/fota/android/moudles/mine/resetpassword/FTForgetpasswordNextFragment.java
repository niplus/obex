package com.fota.android.moudles.mine.resetpassword;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.LogUtils;
import com.fota.android.R;
import com.fota.android.app.MD5Utils;
import com.fota.android.commonlib.base.BaseView;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentFtforgetpswnextBinding;
import com.fota.android.http.Http;
import com.fota.android.utils.StringFormatUtils;
import com.google.gson.JsonObject;

/**
 * 重置密码
 */
public class FTForgetpasswordNextFragment extends BaseFragment implements View.OnClickListener, BaseView {
    FragmentFtforgetpswnextBinding mBinding;
    String account = "";
    boolean isGoogleAuth = false;
    int verifyType = 3;//1-邮箱；2-手机号；3-谷歌

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_forget_next:
                String password = mBinding.edtPassword.getText().toString();
                String password2 = mBinding.edtPassword2.getText().toString();
                String vCode = mBinding.edtVcode.getText().toString().trim();
                if (!password.equals(password2)) {
                    showToast(mContext.getResources().getString(R.string.regist_psw2_notmatch));
                    return;
                }
                if (!StringFormatUtils.checkPswLength(password)) {
                    showToast(mContext.getResources().getString(R.string.regist_psw_lengtherror));
                    return;
                }

                password = MD5Utils.sha256Psw(password);
                password2 = MD5Utils.sha256Psw(password2);
                getResetPsw(password, password2, vCode);
                break;
            case R.id.tv_getvcode:
                int type = account.contains("@") ? 1 : 2;
                getVcode(type, null, 1, account);

                break;
        }
    }

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ftforgetpswnext, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected boolean viewGroupFocused() {
        return false;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        bindValid(mBinding.btnForgetNext, mBinding.edtPassword, mBinding.edtPassword2, mBinding.edtVcode);
        valid();
        edtFocus(mBinding.edtPassword, mBinding.viewPsw);
        edtFocus(mBinding.edtPassword2, mBinding.viewPsw2);
        edtFocus(mBinding.edtVcode, mBinding.viewCode);
        mBinding.btnForgetNext.setOnClickListener(this);
        mBinding.tvGetvcode.setOnClickListener(this);
        edtHide(mBinding.edtPassword, mBinding.cbEyePsw);
        edtHide(mBinding.edtPassword2, mBinding.cbEyePsw2);
        mBinding.edtPassword.addTextChangedListener(new PswTextWatcher());
        if (isGoogleAuth) {
            mBinding.edtVcode.setHint(R.string.safe_bindgoogle_hint);
            mBinding.tvGetvcode.setVisibility(View.GONE);
        } else if (account.contains("@")) {
            mBinding.edtVcode.setHint(R.string.regist_emailvcode_hint);
            mBinding.tvGetvcode.setVisibility(View.VISIBLE);
        } else {
            mBinding.edtVcode.setHint(R.string.regist_phonevcode_hint);
            mBinding.tvGetvcode.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        if (bundle != null) {
            account = bundle.getString("account");
            isGoogleAuth = bundle.getBoolean("isGoogleAuth");
        }
        if (isGoogleAuth) {
            verifyType = 3;
        } else {
            if (TextUtils.isEmpty(account))
                return;
            if (account.contains("@")) {
                verifyType = 1;
            } else {
                verifyType = 2;
            }
        }

    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.login_fotget);
    }

    private void getResetPsw(String psw, String psw2, String code) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("loginPwd", psw);
        jsonObject.addProperty("reLoginPwd", psw2);
        jsonObject.addProperty("verifyCode", code);
        jsonObject.addProperty("account", account);
        jsonObject.addProperty("verifyType", verifyType);
        jsonObject.addProperty("passwdSecurityLevel", StringFormatUtils.getStengthParam(psw, account));

        Http.getHttpService().resetPsw(jsonObject).compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
                    @Override
                    public void onNext(BaseHttpEntity resetPswBean) {
                        LogUtils.a("forget", "resetpsw " + resetPswBean.toString());
                        Event event = Event.create(R.id.event_forget_closepre);
                        EventWrapper.post(event);
//                        mBinding.llReset.setVisibility(View.GONE);
//                        mBinding.llSuc.setVisibility(View.VISIBLE);
                        showToast(mContext.getResources().getString(R.string.resetpsw_suc));
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                onLeftClick();
                            }

                        }, 2000);
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.a("forget", "resetpsw fail" + e);
//                        finish();

                    }
                });
    }

    /* verifyType
          Number
2
  验证类型 1-邮箱；2-手机号
          phoneCountryCode
  String
          CN
  手机号国家码，如果是手机会需要
          templateType
  Number
1
  短信或邮箱模版类型（测试统一 ：templateType = 1）
  account
          String
18858110927
  账号*/
    private void getVcode(int verifyType, String phoneCountryCode, Integer templateType, String account) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("verifyType", verifyType);
        jsonObject.addProperty("phoneCountryCode", phoneCountryCode);
        jsonObject.addProperty("templateType", templateType);
        jsonObject.addProperty("account", account);
        Http.getHttpService().getVcode(jsonObject)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        LogUtils.e("reset", " vcode suc ---");
                        showToast(mContext.getResources().getString(R.string.common_send_suc));
                        mBinding.tvGetvcode.startPhone();
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("reset", "vcode fail fail ---" + e.toString());
                    }
                });
    }

    /**
     * 密码输入框强弱提示
     */
    private class PswTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s)) {
                mBinding.tvPassType.setVisibility(View.GONE);
                return;
            }
            if (StringFormatUtils.checkStrengthISHeigh(s.toString(), account) == 0) {
                mBinding.tvPassType.setVisibility(View.VISIBLE);
                mBinding.tvPassType.setText(R.string.password_strength_low);
            } else if (StringFormatUtils.checkStrengthISHeigh(s.toString(), account) == 1) {
                mBinding.tvPassType.setVisibility(View.VISIBLE);
                mBinding.tvPassType.setText(R.string.password_strength_low2);
            } else if (StringFormatUtils.checkStrengthISHeigh(s.toString(), account) == 2) {
                mBinding.tvPassType.setVisibility(View.VISIBLE);
                mBinding.tvPassType.setText(R.string.password_strength_heigh2);
            } else if (StringFormatUtils.checkStrengthISHeigh(s.toString(), account) >= 3) {
                mBinding.tvPassType.setVisibility(View.VISIBLE);
                mBinding.tvPassType.setText(R.string.password_strength_heigh);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
