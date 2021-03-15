package com.fota.android.moudles.mine.safe;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.LogUtils;
import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentBindgoogleBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.GoogleBean;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.utils.ZXingUtils;
import com.google.gson.JsonObject;

/**
 * 绑定谷歌
 */
public class BindGoogleFragment extends BaseFragment implements View.OnClickListener {
    FragmentBindgoogleBinding mBinding;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bindgoogle, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safe_bindgoogle_title);
    }

    @Override
    protected boolean viewGroupFocused() {
        return false;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);

        mBinding.tvGetvcodeEmail.setOnClickListener(this);
        mBinding.tvGetvcodePhone.setOnClickListener(this);
        mBinding.tvGooglekeyCopy.setOnClickListener(this);
        mBinding.btnSure.setOnClickListener(this);
        mBinding.tvGooglecheckDownload.setOnClickListener(this);
        getGoogle();

        if (TextUtils.isEmpty(UserLoginUtil.getPhone())) {
            mBinding.rlEmail.setVisibility(View.VISIBLE);
            mBinding.rlPhone.setVisibility(View.GONE);
            mBinding.edtVcodePhone.setVisibility(View.GONE);
        } else {
            mBinding.rlEmail.setVisibility(View.GONE);
            mBinding.rlPhone.setVisibility(View.VISIBLE);
            mBinding.edtVcodeEmail.setVisibility(View.GONE);
        }
        bindValid(mBinding.btnSure, mBinding.edtGoogle, mBinding.edtVcodeEmail, mBinding.edtVcodePhone);
        valid();
        edtFocus(mBinding.edtGoogle, mBinding.viewCodeGoogle);
        edtFocus(mBinding.edtVcodeEmail, mBinding.viewCodeEmail);
        edtFocus(mBinding.edtVcodePhone, mBinding.viewCodePhone);
        if (AppConfigs.getTheme() == 0) {
            mBinding.tvMsg.setTextColor(getColor(R.color.googletips_black));
        } else {
            mBinding.tvMsg.setTextColor(getColor(R.color.googletips_white));
        }

    }

    /**
     * 获取谷歌验证信息
     */
    public void getGoogle() {
        Http.getHttpService().getGoogle()
                .compose(new CommonTransformer<GoogleBean>())
                .subscribe(new CommonSubscriber<GoogleBean>(this) {
                    @Override
                    public void onNext(GoogleBean googleBean) {
                        L.a("google = " + googleBean.toString());
                        if (googleBean != null) {
                            if (!TextUtils.isEmpty(googleBean.getSecretkey()))
                                mBinding.tvGooglekey.setText(googleBean.getSecretkey());
                            if (!TextUtils.isEmpty(googleBean.getQrCodeData())) {
                                Bitmap bitmap = null;

                                bitmap = ZXingUtils.Create2DCode(googleBean.getQrCodeData(), 150, 150);
                                mBinding.imvQrcode.setImageBitmap(bitmap);
                            }
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);

                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_getvcode_email:
                getEmailVcode(UserLoginUtil.getEmail());
                break;
            case R.id.tv_getvcode_phone:
                getPhoneVcode("", UserLoginUtil.getPhone());
                break;
            case R.id.tv_googlekey_copy:
                String key = mBinding.tvGooglekey.getText().toString().trim();
                if (TextUtils.isEmpty(key)) {
                    return;
                }
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(key);
                mBinding.tvGooglekeyCopy.setText(R.string.copy_success);
                mBinding.tvGooglekeyCopy.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.tvGooglekeyCopy.setText(R.string.safe_bindgoogle_copy);
                    }
                }, 3000);
                break;
            case R.id.btn_sure:
                String googleCode = mBinding.edtGoogle.getText().toString().trim();
                String smsCode = mBinding.edtVcodePhone.getText().toString().trim();
                String emailCode = mBinding.edtVcodeEmail.getText().toString().trim();
                bindGoogle(googleCode, smsCode, emailCode);
                break;
            case R.id.tv_googlecheck_download:
                opengoogleWeb();

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

    /**
     * 绑定谷歌
     */
    private void bindGoogle(String googleCode, String smsCode, String emailCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("googleCode", googleCode);
        if (!TextUtils.isEmpty(smsCode))
            jsonObject.addProperty("smsCode", smsCode);
        if (!TextUtils.isEmpty(emailCode))
            jsonObject.addProperty("emailCode", emailCode);
        Http.getHttpService().bindGoogle(jsonObject)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        L.a("bindgoogle = " + object.toString());
                        showToast(mContext.getResources().getString(R.string.safe_bindgoogle_suc));
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
                        LogUtils.e("reset", "bindgoogle fail fail ---" + e.toString());
                    }
                });
    }

    /**
     * 浏览器中打开google验证器下载页面
     */
    private void opengoogleWeb() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("http://shouji.baidu.com/software/22417419.html"));//Url 就是你要打开的网址
        intent.setAction(Intent.ACTION_VIEW);
        this.startActivity(intent); //启动浏览器
    }
}
