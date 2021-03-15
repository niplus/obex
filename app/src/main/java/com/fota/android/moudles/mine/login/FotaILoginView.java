package com.fota.android.moudles.mine.login;

import com.fota.android.commonlib.base.BaseView;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.moudles.mine.login.bean.LoginBean;

public interface FotaILoginView extends BaseView {
    void loginSuccess(LoginBean loginBean);

    void loginFail(ApiException e);

    void needGoogle();
}
