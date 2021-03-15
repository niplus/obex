package com.fota.android.widget.btbwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;
import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.http.Http;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.JsonObject;

/**
 * Created by sunchao
 */

@SuppressLint("AppCompatCustomView")
public class VCodeButton extends TextView {

    CountDownTimer timer;

    public VCodeButton(Context context) {
        super(context);
        init(context);
    }

    public VCodeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VCodeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.setGravity(Gravity.CENTER);
        setFocusable(true);
        setClickable(true);
        setAllCaps(false);
//        setBackgroundResource(R.drawable.ft_round_use_able);
        setBtbEnabled(true);
    }


    public void setBtbEnabled(boolean enabled) {
        setEnabled(enabled);
//        setButtonStyle(enabled);
    }

    public void setButtonStyle(boolean enabled) {
        if (enabled) {
            setTextColor(getColor(R.color.login_regist_tv));
//            setBackgroundResource(R.drawable.ft_round_use_able);
        } else {
            setTextColor(Pub.getColor(getContext(), R.attr.font_color));
//            setBackgroundResource(R.drawable.ft_round_disable);
        }
    }


    /**
     * ems
     * 接口返回的时候
     * 开始 验证码 验证码倒计时
     */
    public void startPhone() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setText(String.format(getResources().getString(R.string.common_sending),
                        String.valueOf(millisUntilFinished / 1000)));
                setEmsEnable(false);
            }

            @Override
            public void onFinish() {
                phoneReset();
            }
        };
        timer.start();
    }

    /**
     * ems
     * 这个不用调用 重置
     */
    public void phoneReset() {
        setEmsEnable(true);
        setText(getContext().getResources().getString(R.string.common_resendcode));
    /*    setEnabled(true);
        setTextColor(getColor(R.color.check_color));
        setBackgroundResource(R.drawable.bg_button_blue_color);
        setText(getContext().getResources().getString(R.string.resend));*/
    }

    private void setEmsEnable(boolean enabled) {
        setEnabled(enabled);
        if (enabled) {
            setTextColor(Pub.getColor(getContext(), R.attr.font_color));
        } else {
            setTextColor(getColor(R.color.login_regist_tv));
        }
    }

    private int getColor(@ColorRes int id) {
        return ContextCompat.getColor(getContext(), id);
    }


    /**
     * ems
     * 结束的时候要调用
     */
    public void cancal() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    public void sendReq() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("verifyType", 2);
        jsonObject.addProperty("phoneCountryCode", "");
        jsonObject.addProperty("templateType", 1);
        jsonObject.addProperty("account", UserLoginUtil.getPhone());
        Http.getHttpService().getVcode(jsonObject)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        L.a("bindphone = " + object.toString());
                        startPhone();
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("reset", "vcode fail fail ---" + e.toString());
                    }
                });
    }

}
