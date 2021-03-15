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
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.databinding.FragmentCapitalresetBinding;
import com.fota.android.http.Http;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.JsonObject;

/**
 * 重置资金密码
 */
public class CapitalResetFragment extends BaseFragment implements View.OnClickListener {
    FragmentCapitalresetBinding mBinding;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_capitalreset, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safe_capitalreset_title);
    }

    @Override
    protected boolean viewGroupFocused() {
        return true;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        bindValid(mBinding.btnSure, mBinding.edtPassword, mBinding.edtRepassword, mBinding.edtPaswordOld, mBinding.edtGoogle);
        valid();
        edtFocus(mBinding.edtPassword, mBinding.viewNewpsw);
        edtFocus(mBinding.edtRepassword, mBinding.viewNewpsw2);
        edtFocus(mBinding.edtPaswordOld, mBinding.viewOldpsw);
        edtFocus(mBinding.edtGoogle, mBinding.viewCode);
        mBinding.btnSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                String oldpwd = mBinding.edtPaswordOld.getText().toString();
                String pwd = mBinding.edtPassword.getText().toString();
                String repwd = mBinding.edtRepassword.getText().toString();
                String googleCode = mBinding.edtGoogle.getText().toString();
                if (!pwd.equals(repwd)) {
                    showToast(mContext.getResources().getString(R.string.regist_psw2_notmatch));
                    return;
                }
                if (!StringFormatUtils.checkPswLength(pwd)) {
                    showToast(mContext.getResources().getString(R.string.regist_psw_lengtherror));
                    return;
                }
                oldpwd = MD5Utils.sha256Capital(oldpwd);
                pwd = MD5Utils.sha256Capital(pwd);
                repwd = MD5Utils.sha256Capital(repwd);
                resetCapital(oldpwd, pwd, repwd, googleCode);
                break;
        }
    }

    /**
     * 重置资金密码
     */
    private void resetCapital(String oldPwd, final String pwd, String repwd, String googleCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("oldPwd", oldPwd);
        jsonObject.addProperty("pwd", pwd);
        jsonObject.addProperty("repwd", repwd);
        jsonObject.addProperty("googleCode", googleCode);
        if (!TextUtils.isEmpty(UserLoginUtil.getCapital())) {
            jsonObject.addProperty("oldTradeToken", UserLoginUtil.getCapital());
        }
        Http.getHttpService().resetCapital(jsonObject)
                .compose(new CommonTransformer<String>())
                .subscribe(new CommonSubscriber<String>(this) {
                    @Override
                    public void onNext(String object) {
                        L.a("resetCapital = " + object.toString());
                        if (!TextUtils.isEmpty(object)) {
                            UserLoginUtil.saveCapital(object);
                        }
                        showToast(mContext.getResources().getString(R.string.safe_capitalreset_suc));
//                        mBinding.llEdt.setVisibility(View.GONE);
//                        mBinding.llSuc.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                onLeftClick();
                            }

                        }, 2000);

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("reset", "resetCapital fail fail ---" + e.toString());
                        stopProgressDialog();
                    }
                });
    }
}
