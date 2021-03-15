package com.fota.android.moudles.mine.resetpassword;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.LogUtils;
import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.commonlib.base.BaseView;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.BitmapUtils;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.event.Event;
import com.fota.android.databinding.FragmentForgetpswFtBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.resetpassword.bean.PicCheckBean;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/*
忘记密码
 */
public class FTForgetpasswordFragment extends BaseFragment implements View.OnClickListener, BaseView {
    private FragmentForgetpswFtBinding mBinding;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_forget_next:
                String account = mBinding.edtAccount.getText().toString().replace(" ", "");
                String picCode = mBinding.edtVcode.getText().toString().trim();
                getCHeckPicCode(picCode, account);
                break;
            case R.id.imv_refresh:
                getPicCode();
                break;
            default:
                break;
        }

    }

    @Override
    protected boolean viewGroupFocused() {
        return false;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        bindValid(mBinding.btnForgetNext, mBinding.edtAccount, mBinding.edtVcode);
        valid();
        edtFocus(mBinding.edtAccount, mBinding.viewAccount);
        edtFocus(mBinding.edtVcode, mBinding.viewCode);
        getPicCode();
    }

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgetpsw_ft, container, false);
        mBinding.btnForgetNext.setOnClickListener(this);
        mBinding.imvRefresh.setOnClickListener(this);
//        mBinding.tvRegistTypechange.setOnClickListener(this);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.login_fotget);
    }

    /**
     * 获取图片验证码
     */
    private void getPicCode() {
        Http.getHttpService().getPicCode().compose(new CommonTransformer<String>())
                .subscribe(new CommonSubscriber<String>(this) {
                    @Override
                    public void onNext(String picStr) {
                        LogUtils.a("forget", "piccode " + picStr);
                        if (!TextUtils.isEmpty(picStr)) {
                            Bitmap picture = BitmapUtils.stringtoBitmap(picStr);
                            if (picture != null) {
                                mBinding.imvVcode.setImageDrawable(new BitmapDrawable(picture));
                            }
                        }

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.a("forget", "piccode fail" + e);

                    }
                });
    }

    /**
     * 获取图片验证码
     * captchaCode:验证码
     * userSymbol用户名
     */
    private void getCHeckPicCode(String captchaCode, String userSymbol) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("captchaCode", captchaCode);
        jsonObject.addProperty("userSymbol", userSymbol);
        Http.getHttpService().getCheckPicCode(jsonObject).compose(new CommonTransformer<PicCheckBean>())
                .subscribe(new CommonSubscriber<PicCheckBean>(this) {
                    @Override
                    public void onNext(PicCheckBean picCheckBean) {
                        LogUtils.a("forget", "checkpiccode " + picCheckBean.toString());
                        Bundle bundle = new Bundle();
                        bundle.putString("account", mBinding.edtAccount.getText().toString().replace(" ", ""));
                        bundle.putBoolean("isGoogleAuth", picCheckBean.isGoogleAuth());
                        SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.FTForgetpasswordNextFragment, bundle);
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.a("forget", "checkpiccode fail" + e);

                    }
                });
    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_forget_closepre:
                getActivity().finish();
                break;
        }
    }
}
