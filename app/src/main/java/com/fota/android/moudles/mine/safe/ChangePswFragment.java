package com.fota.android.moudles.mine.safe;

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
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentChangepswBinding;
import com.fota.android.http.Http;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.utils.apputils.MineInfoUtil;
import com.google.gson.JsonObject;

/**
 * 修改登录密码
 */
public class ChangePswFragment extends BaseFragment implements View.OnClickListener {

    FragmentChangepswBinding mBinding;
    int verifyType = 3;//1-邮箱；2-手机号；3-谷歌

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_changepsw, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safesetting_password_reset);
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        bindValid(mBinding.btnSure, mBinding.edtPassword, mBinding.edtRepassword, mBinding.edtPaswordOld, mBinding.edtCode);
        valid();
        edtFocus(mBinding.edtPassword, mBinding.viewNewpsw);
        edtFocus(mBinding.edtRepassword, mBinding.viewNewpsw2);
        edtFocus(mBinding.edtPaswordOld, mBinding.viewOldpsw);
        edtFocus(mBinding.edtCode, mBinding.viewCode);
        edtHide(mBinding.edtPaswordOld, mBinding.cbEyePswOld);
        edtHide(mBinding.edtPassword, mBinding.cbEyePsw);
        edtHide(mBinding.edtRepassword, mBinding.cbEyePsw2);

        mBinding.btnSure.setOnClickListener(this);
        mBinding.tvGetvcode.setOnClickListener(this);
        if (MineInfoUtil.haveBindGooglel()) {
            verifyType = 3;
            mBinding.edtCode.setHint(R.string.safe_bindgoogle_hint);
            mBinding.tvGetvcode.setVisibility(View.GONE);
        } else if (MineInfoUtil.haveBindPhone()) {
            verifyType = 2;
            mBinding.edtCode.setHint(R.string.regist_phonevcode_hint);
            mBinding.tvGetvcode.setVisibility(View.VISIBLE);
        } else {
            verifyType = 1;
            mBinding.edtCode.setHint(R.string.regist_emailvcode_hint);
            mBinding.tvGetvcode.setVisibility(View.VISIBLE);
        }
        mBinding.edtPassword.addTextChangedListener(new PswTextWatcher());
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                String oldpwd = mBinding.edtPaswordOld.getText().toString();
                String pwd = mBinding.edtPassword.getText().toString();
                String repwd = mBinding.edtRepassword.getText().toString();
                String code = mBinding.edtCode.getText().toString();
                if (!pwd.equals(repwd)) {
                    showToast(mContext.getResources().getString(R.string.regist_psw2_notmatch));
                    return;
                }
                if (!StringFormatUtils.checkPswLength(pwd)) {
                    showToast(mContext.getResources().getString(R.string.regist_psw_lengtherror));
                    return;
                }
                oldpwd = MD5Utils.sha256Psw(oldpwd);
                pwd = MD5Utils.sha256Psw(pwd);
                repwd = MD5Utils.sha256Psw(repwd);
                resetCapital(oldpwd, pwd, repwd, code);
                break;
            case R.id.tv_getvcode:
                int type = verifyType;
                String account = verifyType == 2 ? UserLoginUtil.getPhone() : UserLoginUtil.getEmail();
                getVcode(type, null, 1, account);
                break;
        }
    }

    /**
     * 重置资金密码
     */
    private void resetCapital(String oldPwd, final String pwd, String repwd, String verifyCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("oldLoginPwd", oldPwd);
        jsonObject.addProperty("loginPwd", pwd);
        jsonObject.addProperty("reLoginPwd", repwd);
        jsonObject.addProperty("verifyCode", verifyCode);
        jsonObject.addProperty("verifyType", verifyType);
        String account = verifyType == 2 ? UserLoginUtil.getPhone() : UserLoginUtil.getEmail();
        jsonObject.addProperty("passwdSecurityLevel", StringFormatUtils.getStengthParam(pwd, account));


        Http.getHttpService().changePsw(jsonObject)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        L.a("changePsw = " + object.toString());
                        showToast(mContext.getResources().getString(R.string.resetpsw_suc));
                        UserLoginUtil.clearCapital();
//                        mBinding.llEdt.setVisibility(View.GONE);
//                        mBinding.llSuc.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                changeSuccess();
                            }

                        }, 2000);

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("changePsw", "changePsw fail fail ---" + e.toString());
                        stopProgressDialog();
                    }
                });
    }

    protected void changeSuccess() {
        EventWrapper.post(Event.create(R.id.safe_finish));
        UserLoginUtil.delUser();
        FtRounts.toQuickLogin(mContext);
        onLeftClick();
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
            String account = verifyType == 2 ? UserLoginUtil.getPhone() : UserLoginUtil.getEmail();
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
}

