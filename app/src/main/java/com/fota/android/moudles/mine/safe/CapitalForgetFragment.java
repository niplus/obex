package com.fota.android.moudles.mine.safe;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentCapitalforgetBinding;
import com.fota.android.http.Http;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.JsonObject;

/**
 * 忘记资金密码
 */
public class CapitalForgetFragment extends BaseFragment implements View.OnClickListener {
    FragmentCapitalforgetBinding mBinding;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_capitalforget, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safe_capitalforget_title);
    }

    @Override
    protected boolean viewGroupFocused() {
        return true;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        bindValid(mBinding.btnSure, mBinding.edtPasword, mBinding.edtRepassword, mBinding.edtVcode, mBinding.edtGoogle);
        valid();
        edtFocus(mBinding.edtPasword, mBinding.viewPsw);
        edtFocus(mBinding.edtRepassword, mBinding.viewPsw2);
        edtFocus(mBinding.edtVcode, mBinding.viewCode);
        edtFocus(mBinding.edtGoogle, mBinding.viewGooglecode);
        mBinding.tvGetvcode.setOnClickListener(this);
        mBinding.btnSure.setOnClickListener(this);
        if (TextUtils.isEmpty(UserLoginUtil.getPhone())) {
            mBinding.edtVcode.setHint(R.string.regist_emailvcode_hint);
        } else {
            mBinding.edtVcode.setHint(R.string.regist_phonevcode_hint);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                String psw1 = mBinding.edtPasword.getText().toString();
                String psw2 = mBinding.edtRepassword.getText().toString();
                String vcode = mBinding.edtVcode.getText().toString().trim();
                String googleCode = mBinding.edtGoogle.getText().toString().trim();
                if (!psw1.equals(psw2)) {
                    showToast(mContext.getResources().getString(R.string.regist_psw2_notmatch));
                    return;
                }
                if (!StringFormatUtils.checkPswLength(psw1)) {
                    showToast(mContext.getResources().getString(R.string.regist_psw_lengtherror));
                    return;
                }
                psw1 = MD5Utils.sha256Capital(psw1);
                psw2 = MD5Utils.sha256Capital(psw2);
                setCapital(psw1, psw2, vcode, googleCode);
                break;
            case R.id.tv_getvcode:
                if (TextUtils.isEmpty(UserLoginUtil.getPhone())) {
                    getEmailVcode(UserLoginUtil.getEmail());
                } else {
                    getPhoneVcode("", UserLoginUtil.getPhone());
                }
                break;
        }
    }

    /**
     * 手机验证码
     *
     * @param phoneCountryCode
     * @param account
     */
    private void getPhoneVcode(String phoneCountryCode, String account) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("verifyType", 2);
        jsonObject.addProperty("phoneCountryCode", phoneCountryCode);
        jsonObject.addProperty("templateType", 1);
        jsonObject.addProperty("account", account);
        Http.getHttpService().getVcode(jsonObject)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        L.a("bindphone = " + object.toString());
                        mBinding.tvGetvcode.startPhone();
                        showToast(mContext.getResources().getString(R.string.common_send_suc));
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("reset", "vcode fail fail ---" + e.toString());
                    }
                });
    }

    /**
     * 邮箱验证码
     *
     * @param account
     */
    private void getEmailVcode(String account) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("verifyType", 1);
        jsonObject.addProperty("templateType", 1);
        jsonObject.addProperty("account", account);
        Http.getHttpService().getVcode(jsonObject)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        L.a("bindphone = " + object.toString());
                        mBinding.tvGetvcode.startPhone();
                        showToast(mContext.getResources().getString(R.string.common_send_suc));

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("reset", "vcode fail fail ---" + e.toString());
                    }
                });
    }

    /**
     * 忘记忘记资金密码
     */
    private void setCapital(final String pwd, String repwd, String verifyCode, String googleCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pwd", pwd);
        jsonObject.addProperty("repwd", repwd);
        jsonObject.addProperty("verifyCode", verifyCode);
        jsonObject.addProperty("googleCode", googleCode);
        if (!TextUtils.isEmpty(UserLoginUtil.getCapital())) {
            jsonObject.addProperty("oldTradeToken", UserLoginUtil.getCapital());
        }
        Http.getHttpService().forgetCapital(jsonObject)
                .compose(new CommonTransformer<String>())
                .subscribe(new CommonSubscriber<String>(this) {
                    @Override
                    public void onNext(String object) {
                        L.a("setcapital = " + object);
                        showToast(mContext.getResources().getString(R.string.safe_capitalforget_suc));
                        if (!TextUtils.isEmpty(object))
                            UserLoginUtil.saveCapital(object);
                        EventWrapper.post(Event.create(R.id.mine_refresh));//通知我的页面更新
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                onLeftClick();
                            }

                        }, 2000);

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("reset", "setcapital fail fail ---" + e.toString());
                    }
                });
    }
}
