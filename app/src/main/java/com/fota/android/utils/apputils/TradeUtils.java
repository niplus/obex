package com.fota.android.utils.apputils;

import android.content.Context;
import android.content.DialogInterface;

import com.fota.android.R;
import com.fota.android.http.Http;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.MD5Utils;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.core.base.BaseActivity;
import com.fota.android.core.base.BoolEnum;
import com.fota.android.core.base.BtbMap;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.UserLoginUtil;

public class TradeUtils {


    static TradeUtils instance;


    private TradeUtils() {
    }

    public static TradeUtils getInstance() {
        if (instance == null) {
            return new TradeUtils();
        }
        return instance;
    }

    public void changePasswordToToken(final Context context, String fundCode, final ChangePassWordListener listener) {
        BtbMap map = new BtbMap();
        map.put("password", MD5Utils.sha256Capital(fundCode));//MD5Utils.getMD5(fundCode)
        startProgressDialog(context);
        Http.getExchangeService().changePasswordToToken(map)
                .compose(new CommonTransformer<String>())
                .subscribe(new CommonSubscriber<String>() {
                    @Override
                    public void onNext(String map) {
                        stopProgressDialog(context);
                        UserLoginUtil.saveCapital(map);
                        listener.setPasswordToken(map);
                    }

                    @Override
                    protected void login() {
                        stopProgressDialog(context);
                        FtRounts.toQuickLogin(context);
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        stopProgressDialog(context);
                    }
                });
    }

    private void startProgressDialog(Context context) {
        if (context == null) {
            return;
        }
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).startProgressDialog();
        }
    }


    private void stopProgressDialog(Context context) {
        if (context == null) {
            return;
        }
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).stopProgressDialog();
        }
    }

    public interface ValidPasswordListener {

        void showPasswordDialog();
    }

    public interface ExchangePasswordListener extends ValidPasswordListener {
        void noPassword();
    }

    public interface ChangePassWordListener {
        void setPasswordToken(String token);
    }


    public void validPassword(final Context context, final String mRequestCode, final ValidPasswordListener listener) {
        if (context == null) {
            return;
        }
        //jiang
        if (!MineInfoUtil.haveSetCapital()) {
            DialogUtils.showDialog(context,
                    new DialogModel(CommonUtils.getResouceString(context, R.string.code_101080))
                            .setSureText(CommonUtils.getResouceString(context, R.string.common_goto_ret))
                            .setCancelText(CommonUtils.getResouceString(context, R.string.cancel))
                            .setSureClickListen(new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SimpleFragmentActivity.gotoFragmentActivity(context, ConstantsPage.SafeSettingFragment);
                                }
                            })
            );
            return;
        }
        //还没设置手势或指纹或本地token为空
        if (!FtRounts.isQuickCapitalNoJumper(context)) {
            if (listener instanceof ExchangePasswordListener) {
                //如果是交易 可以免密 继续往下走
            } else {
                listener.showPasswordDialog();
                return;
            }
        }
        //本地token有效
        BtbMap map = new BtbMap();
        map.put("tradeToken", UserLoginUtil.getCapital());
        startProgressDialog(context);
        Http.getExchangeService().verifyPassword(map)
                .compose(new CommonTransformer<BtbMap>())
                .subscribe(new CommonSubscriber<BtbMap>() {
                    @Override
                    public void onNext(BtbMap map) {
                        //需要密码
                        stopProgressDialog(context);
                        boolean tokenStatus = BoolEnum.isTrue(map.get("tokenStatus"));
                        //服务器token有效
                        if (tokenStatus) {
                            if (listener instanceof ExchangePasswordListener) {
                                ((ExchangePasswordListener) listener).noPassword();
                            } else {
                                FtRounts.toQuickCapital(context, mRequestCode);
                            }
                        } else {
                            listener.showPasswordDialog();
                        }

                    }

                    @Override
                    protected void onError(ApiException e) {
                        stopProgressDialog(context);
                        listener.showPasswordDialog();
                    }

                    @Override
                    protected void login() {
                        stopProgressDialog(context);
                        FtRounts.toQuickLogin(context);
                    }
                });
    }

}

