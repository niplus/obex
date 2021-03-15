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
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentCapitalsetBinding;
import com.fota.android.http.Http;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.JsonObject;

/**
 * 设置资金密码
 */
public class CapitalSetFragment extends BaseFragment implements View.OnClickListener {
    FragmentCapitalsetBinding mBinding;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_capitalset, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safe_capitalset_title);
    }

    @Override
    protected boolean viewGroupFocused() {
        return true;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        bindValid(mBinding.btnSure, mBinding.edtPasword, mBinding.edtRepassword, mBinding.edtGoogle);
        valid();
        edtFocus(mBinding.edtPasword, mBinding.viewPsw);
        edtFocus(mBinding.edtRepassword, mBinding.viewPsw2);
        edtFocus(mBinding.edtGoogle, mBinding.viewCode);
        mBinding.btnSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                String psw1 = mBinding.edtPasword.getText().toString();
                String psw2 = mBinding.edtRepassword.getText().toString();
                String vcode = mBinding.edtGoogle.getText().toString().trim();
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
                setCapital(psw1, psw2, vcode);
                break;
        }
    }

    /**
     * 设置资金密码
     */
    private void setCapital(final String pwd, String repwd, String googleCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pwd", pwd);
        jsonObject.addProperty("repwd", repwd);
        jsonObject.addProperty("googleCode", googleCode);
        Http.getHttpService().setCapital(jsonObject)
                .compose(new CommonTransformer<String>())
                .subscribe(new CommonSubscriber<String>(this) {
                    @Override
                    public void onNext(String object) {
                        L.a("setcapital = " + object.toString());
                        showToast(mContext.getResources().getString(R.string.safe_capitalset_suc));
                        if (!TextUtils.isEmpty(object)){
                            UserLoginUtil.saveCapital(object);
                        }
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
