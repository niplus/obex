package com.fota.android.moudles.mine.login;

import android.text.TextUtils;

import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.login.bean.LoginBean;
import com.fota.android.utils.StringFormatUtils;
import com.google.gson.JsonObject;

public class FtLoginPresenter extends BasePresenter<FotaILoginView> {

    public FtLoginPresenter(FotaILoginView view) {
        super(view);
    }

    public void login(String account, String psw, String googleCode) {
        JsonObject jsonObject = new JsonObject();

        if (StringFormatUtils.isNumeric(account)) {
            jsonObject.addProperty("type", 0);
            jsonObject.addProperty("phone", account);
        } else {
            jsonObject.addProperty("type", 1);
            jsonObject.addProperty("email", account);
        }

        jsonObject.addProperty("pwd", psw);
        if (!TextUtils.isEmpty(googleCode)) {
            jsonObject.addProperty("googleCode", googleCode);
        }
        Http.getHttpService().login(jsonObject)
                .compose(new CommonTransformer<LoginBean>())
                .subscribe(new CommonSubscriber<LoginBean>(getView()) {
                    @Override
                    public void onNext(LoginBean loginBean) {
                        if (mvpView == null)
                            return;
                        mvpView.loginSuccess(loginBean);
                    }

                    @Override
                    protected void onError(ApiException e) {
                        if (mvpView == null)
                            return;
                        mvpView.stopProgressDialog();
                        if (e.code == 101026) {
                            mvpView.needGoogle();
                        } else {
                            super.onError(e);
                            mvpView.loginFail(e);
                        }

                    }
                });
    }
}
