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
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.databinding.FragmentBindemailBinding;
import com.fota.android.http.Http;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.JsonObject;

/**
 * 绑定邮箱
 */
public class BindEmailFragment extends BaseFragment implements View.OnClickListener {
    FragmentBindemailBinding mBinding;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bindemail, container, false);

        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safesetting_email);
    }

    @Override
    protected boolean viewGroupFocused() {
        return false;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        bindValid(mBinding.btnSure, mBinding.edtAccount, mBinding.edtVcodeEmail, mBinding.edtVcodePhone);
        valid();
        edtFocus(mBinding.edtAccount, mBinding.viewAccount);
        edtFocus(mBinding.edtVcodeEmail, mBinding.viewCodeEmail);
        edtFocus(mBinding.edtVcodePhone, mBinding.viewCodePhone);
        if (TextUtils.isEmpty(UserLoginUtil.getPhone())) {
            mBinding.rlPhone.setVisibility(View.GONE);
        } else {
            mBinding.rlPhone.setVisibility(View.VISIBLE);
        }
        mBinding.tvGetvcodePhone.setOnClickListener(this);
        mBinding.tvGetvcodeEmail.setOnClickListener(this);
        mBinding.btnSure.setOnClickListener(this);
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
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        L.a("bindphone = " + object.toString());
                        mBinding.tvGetvcodePhone.startPhone();
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
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        L.a("bindphone = " + object.toString());
                        mBinding.tvGetvcodeEmail.startPhone();
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
     * 绑定手机号
     */
    private void bindPhone(String email, String emailCode, String smsCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("emailCode", emailCode);
        jsonObject.addProperty("smsCode", smsCode);
        Http.getHttpService().bindEmail(jsonObject)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        L.a("bindemail = " + object.toString());
                        showToast(mContext.getResources().getString(R.string.safe_bindemail_suc));
                        UserLoginUtil.setEmail(mBinding.edtAccount.getText().toString().replace(" ", ""));
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                finish();
                            }

                        }, 2000);

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("reset", "bindemail fail fail ---" + e.toString());
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_getvcode_email:
                String account_email = mBinding.edtAccount.getText().toString();
                if (TextUtils.isEmpty(account_email)) {
                    showToast(mContext.getResources().getString(R.string.email_notinput));
                    return;
                }

                if (!StringFormatUtils.checkEmail(account_email)){
                    showToast(R.string.common_email_notlegal);
                    return;
                }
                getEmailVcode(account_email);
                break;
            case R.id.tv_getvcode_phone:
                String account = UserLoginUtil.getPhone();

                getPhoneVcode("", account);

                break;
            case R.id.btn_sure:
                String account_bind = mBinding.edtAccount.getText().toString().replace(" ", "");
                String emailcode = mBinding.edtVcodeEmail.getText().toString().trim();
                String phonecode = mBinding.edtVcodePhone.getText().toString().trim();
                if (!StringFormatUtils.checkEmail(account_bind)){
                    showToast(R.string.common_email_notlegal);
                    return;
                }
                bindPhone(account_bind, emailcode, phonecode);
                break;
        }
    }
}
