package com.fota.android.utils.apputils;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.fota.android.http.Http;
import com.fota.android.app.FotaApplication;
import com.fota.android.app.GsonSinglon;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.moudles.mine.bean.MineBean;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MineInfoUtil {
    public final static String MINEBEAN_JSON = "minebean_json";

    public static void getMindeMsg() {
        if (!UserLoginUtil.havaUser()) {
            return;
        }

        Http.getHttpService().getMineData()
                .compose(new CommonTransformer<MineBean>())
                .subscribe(new CommonSubscriber<MineBean>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(MineBean mineBean) {

                        LogUtils.a("mine", "mine result " + mineBean.toString());
                        saveMine(mineBean);


                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.a("mine", "mine result fail" + e);
                    }

                });
    }

    /**
     * 我的数据缓存
     */
    public static void saveMine(final MineBean mineBean) {
        if (TextUtils.isEmpty(UserLoginUtil.getId()) || mineBean == null)
            return;
        //线程执行即可
        new Thread(new Runnable() {
            @Override
            public void run() {
                String listJson = GsonSinglon.getInstance().toJson(mineBean);
                SPUtils.getInstance().put(MINEBEAN_JSON + UserLoginUtil.getId(), listJson);
            }
        }).start();
    }

    /**
     * 从sp获取我的缓存
     */
    public static MineBean getDiskMine() {
        if (TextUtils.isEmpty(UserLoginUtil.getId()))
            return null;
        String minejson = SPUtils.getInstance().getString(MINEBEAN_JSON + UserLoginUtil.getId());
        if (TextUtils.isEmpty(minejson)) {
            return null;
        }
        Gson gson = GsonSinglon.getInstance();
        return gson.fromJson(minejson, new TypeToken<MineBean>() {
        }.getType());
    }

    /**
     * 是否设置了资金密码
     *
     * @return
     */
    public static boolean haveSetCapital() {
        MineBean mineBean = getDiskMine();
        if (mineBean == null)
            return false;
        if (mineBean.getUserSecurity() != null && mineBean.getUserSecurity().isFundPwdSet()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否绑定phone
     *
     * @return
     */
    public static boolean haveBindPhone() {
        MineBean mineBean = getDiskMine();
        if (mineBean == null)
            return false;
        if (mineBean.getUserSecurity() != null && mineBean.getUserSecurity().isPhoneAuth()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否绑定email
     *
     * @return
     */
    public static boolean haveBindEmail() {
        MineBean mineBean = getDiskMine();
        if (mineBean == null)
            return false;
        if (mineBean.getUserSecurity() != null && mineBean.getUserSecurity().isEmailAuth()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否绑定google
     *
     * @return
     */
    public static boolean haveBindGooglel() {
        MineBean mineBean = getDiskMine();
        if (mineBean == null)
            return false;
        if (mineBean.getUserSecurity() != null && mineBean.getUserSecurity().isGoogleAuth()) {
            return true;
        } else {
            return false;
        }
    }
}
