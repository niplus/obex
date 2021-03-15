package com.fota.android.utils.apputils;

import android.text.TextUtils;

import com.fota.android.http.Http;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.SharedPreferencesUtil;

public class DiffTimeUtils {

    public static void getTime() {
        Http.getHttpService().getServiceTIme()
                .compose(new CommonTransformer<String>())
                .subscribe(new CommonSubscriber<String>() {

                    @Override
                    public void onNext(String timeStr) {
                        long timestemp = System.currentTimeMillis();
                        if (TextUtils.isEmpty(timeStr))
                            return;
                        try {
                            Long time = Long.valueOf(timeStr).longValue();
                            if (time != null && time > 0) {
                                Long diff = timestemp - time;
                                SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.TIME_DIFF, diff);
                            }
                        } catch (Exception e) {
                        }

                    }

                });
    }

}
