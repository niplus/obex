package com.fota.android.core;

import android.content.Context;

import com.fota.android.R;
import com.fota.android.commonlib.base.BaseView;
import com.fota.android.commonlib.base.MyActivityManager;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;

public abstract class AttexSubscriber<T> extends CommonSubscriber<T> {

    public AttexSubscriber(Context context) {
        super(context);
    }

    public AttexSubscriber(BaseView baseView) {
        super(baseView);
    }

    @Override
    protected void onError(ApiException e) {
        switch (e.code) {
            case 101306:
                e.message = MyActivityManager.getInstance().getCurrentActivity().getString(R.string.code_101306);
                break;
        }
        super.onError(e);
    }
}
