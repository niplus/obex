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
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.event.Event;
import com.fota.android.databinding.FragmentBindphoneBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.login.bean.CounrtyAreasBean;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 绑定手机号
 */
public class BindPhoneFragment extends BaseFragment implements View.OnClickListener {
    FragmentBindphoneBinding mBinding;
    CounrtyAreasBean.Area countryArea = null;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bindphone, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safe_bindphone_title);
    }

    @Override
    protected boolean viewGroupFocused() {
        return false;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        bindValid(mBinding.btnSure, mBinding.edtAccount, mBinding.edtVcodeEmail, mBinding.edtVcodePhone, mBinding.edtGoogle);
        valid();
        edtFocus(mBinding.edtAccount, mBinding.viewAccount);
        edtFocus(mBinding.edtVcodeEmail, mBinding.viewCodeEmail);
        edtFocus(mBinding.edtVcodePhone, mBinding.viewCodePhone);
        edtFocus(mBinding.edtGoogle, mBinding.viewGooglecode);
        if (TextUtils.isEmpty(UserLoginUtil.getPhone())) {
//            mBinding.rlEmail.setVisibility(View.VISIBLE);
            setTitle(getString(R.string.safe_bindphone_title));
            mBinding.edtVcodeEmail.setHint(R.string.regist_emailvcode_hint);
            mBinding.rlEmail.setVisibility(View.VISIBLE);
            mBinding.rlGoogle.setVisibility(View.GONE);
            mBinding.edtGoogle.setVisibility(View.GONE);
            mBinding.edtVcodeEmail.setVisibility(View.VISIBLE);
        } else {
//            mBinding.rlEmail.setVisibility(View.INVISIBLE);
            setTitle(getString(R.string.safe_bindnewphone_title));
            mBinding.edtVcodeEmail.setHint(R.string.safe_bindgoogle_hint);
            mBinding.rlEmail.setVisibility(View.GONE);
            mBinding.rlGoogle.setVisibility(View.VISIBLE);
            mBinding.edtGoogle.setVisibility(View.VISIBLE);
            mBinding.edtVcodeEmail.setVisibility(View.GONE);
        }
        mBinding.tvCountryPhone.setOnClickListener(this);
        mBinding.tvGetvcodePhone.setOnClickListener(this);
        mBinding.tvGetvcodeEmail.setOnClickListener(this);
        mBinding.btnSure.setOnClickListener(this);
        countryArea = new CounrtyAreasBean.Area();
        countryArea.setCode("86");
        countryArea.setName_en("China");
        countryArea.setName_zh(mContext.getResources().getString(R.string.china));
        countryArea.setKey("CN");
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
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
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
    private void bindPhone(String phoneCountryCode, String phoneCountryKey, String phone, String emailCode, String smsCode, String googleCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phoneCountryCode", phoneCountryCode);
        jsonObject.addProperty("phoneCountryKey", phoneCountryKey);
        jsonObject.addProperty("phone", phone);
        if (!TextUtils.isEmpty(emailCode))
            jsonObject.addProperty("emailCode", emailCode);
        if (!TextUtils.isEmpty(googleCode))
            jsonObject.addProperty("googleCode", googleCode);
        jsonObject.addProperty("smsCode", smsCode);
        Http.getHttpService().bindPhone(jsonObject)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        L.a("bindphone = " + object.toString());
                        showToast(R.string.safe_bindphone_suc);
//                        EventWrapper.post(Event.create(R.id.mine_refresh));//通知我的页面更新
                        UserLoginUtil.setPhone(mBinding.edtAccount.getText().toString().trim());
                        if (!TextUtils.isEmpty(UserLoginUtil.getLoginedAccount()) && !UserLoginUtil.getLoginedAccount().equals("@")) {
                            UserLoginUtil.saveLoginedAccount(mBinding.edtAccount.getText().toString().trim());
                        }
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                finish();
                            }

                        }, 2000);

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("reset", "bindphone fail fail ---" + e.toString());
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_country_phone:
                Bundle bundle = new Bundle();
                bundle.putString("from", "bindphone");
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.CheckCountryFragment, bundle);
                break;
            case R.id.tv_getvcode_email:
                String account_email = UserLoginUtil.getEmail();
                getEmailVcode(account_email);
                break;
            case R.id.tv_getvcode_phone:
                String account = mBinding.edtAccount.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    showToast(mContext.getResources().getString(R.string.phone_notinput));
                    return;
                }
                getPhoneVcode(countryArea.getCode(), account);

                break;
            case R.id.btn_sure:
                String account_bind = mBinding.edtAccount.getText().toString().trim();
                String emailcode = mBinding.edtVcodeEmail.getText().toString().trim();
                String phonecode = mBinding.edtVcodePhone.getText().toString().trim();
                String googleCode = mBinding.edtGoogle.getText().toString().trim();
                bindPhone(countryArea.getCode(), countryArea.getKey(), account_bind, emailcode, phonecode, googleCode);
                break;
        }
    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_bindphone_countrycheck:
                CounrtyAreasBean.Area a = event.getParam(CounrtyAreasBean.Area.class);
                if (a != null) {
                    countryArea = a;
                }
                mBinding.tvCountryPhone.setText("+" + countryArea.getCode());
                break;
        }
    }
}
