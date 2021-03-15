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
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.BtbMap;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentUnbindgoogleBinding;
import com.fota.android.http.Http;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.JsonObject;

/**
 * 解绑谷歌
 */
public class UNBindGoogleFragment extends BaseFragment implements View.OnClickListener {
    FragmentUnbindgoogleBinding mBinding;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_unbindgoogle, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safe_unbindgoogle_title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_getvcode_phone:
                getPhoneVcode("", UserLoginUtil.getPhone());
                break;
            case R.id.tv_getvcode_email:
                getEmailVcode(UserLoginUtil.getEmail());
                break;
            case R.id.btn_sure:
                String vcode_google = mBinding.edtGoogle.getText().toString().trim();
                String vcode_phone = mBinding.edtVcodePhone.getText().toString().trim();
                String vcode_email = mBinding.edtVcodeEmail.getText().toString();
                unbindGoogle(vcode_google, vcode_phone, vcode_email);
                break;
        }

    }

    @Override
    protected boolean viewGroupFocused() {
        return true;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);

        mBinding.tvGetvcodePhone.setOnClickListener(this);
        mBinding.btnSure.setOnClickListener(this);
        mBinding.tvGetvcodeEmail.setOnClickListener(this);
        if (TextUtils.isEmpty(UserLoginUtil.getPhone())) {
            mBinding.rlEmail.setVisibility(View.VISIBLE);
            mBinding.rlPhone.setVisibility(View.GONE);
            mBinding.edtVcodePhone.setVisibility(View.GONE);
        } else {
            mBinding.rlEmail.setVisibility(View.GONE);
            mBinding.rlPhone.setVisibility(View.VISIBLE);
            mBinding.edtVcodeEmail.setVisibility(View.GONE);
        }

        bindValid(mBinding.btnSure, mBinding.edtGoogle, mBinding.edtVcodePhone, mBinding.edtVcodeEmail);
        valid();
        edtFocus(mBinding.edtGoogle, mBinding.viewGooglecode);
        edtFocus(mBinding.edtVcodeEmail, mBinding.viewCodeEmail);
        edtFocus(mBinding.edtVcodePhone, mBinding.viewPhone);
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
     * 解绑谷歌
     */
    private void unbindGoogle(String googleCode, String smsCode, String emailCode) {
        BtbMap map = new BtbMap();
        map.p("googleCode", googleCode);
        if (!TextUtils.isEmpty(smsCode))
            map.p("smsCode", smsCode);
        if (!TextUtils.isEmpty(emailCode))
            map.p("emailCode", emailCode);
        Http.getHttpService().unbindGoogle(map)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        L.a("unbindGoogle = " + object.toString());
                        showToast(R.string.safe_unbindgoogle_suc);
                        EventWrapper.post(Event.create(R.id.mine_refresh));//通知我的页面更新
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                finish();
                            }
                        }, 2000);

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("reset", "unbindGoogle fail fail ---" + e.toString());
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
                        L.a("getEmailVcode = " + object.toString());
                        mBinding.tvGetvcodeEmail.startPhone();
                        showToast(mContext.getResources().getString(R.string.common_send_suc));

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("reset", "getEmailVcode fail fail ---" + e.toString());
                    }
                });
    }
}
