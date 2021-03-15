package com.fota.android.widget.btbwidget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;

/**
 * Created by Dell on 2018/4/23.
 */

public class FotaButton extends androidx.appcompat.widget.AppCompatTextView {

    CountDownTimer timer;

    public FotaButton(Context context) {
        super(context);
        init(context);
    }

    public FotaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FotaButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.setGravity(Gravity.CENTER);
        setFocusable(true);
        setClickable(true);
        setAllCaps(false);
        //setBackground(getResources().getDrawable(R.drawable.bg_btn));
        setBtbEnabled(true);
    }


    public void setBtbEnabled(boolean enabled) {
        setEnabled(enabled);
        setButtonStyle(enabled);
    }

    public void setButtonStyle(boolean enabled) {
        if (enabled) {
//            setTextColor(0xFFE1E5ED);
            //jiang
            setTextColor(0xFFFFFFFF);
            UIUtil.setRoundCornerBg(this, Pub.getColor(getContext(), R.attr.main_color));
        } else {
            setTextColor(Pub.getColor(getContext(), R.attr.font_color5));
            UIUtil.setRoundCornerBg(this, Pub.getColor(getContext(), R.attr.line_color));
        }
    }

    public void setCircleButtonStyle(boolean enabled) {
        if (enabled) {
//            setTextColor(0xFFE1E5ED);
            //jiang
            setTextColor(0xFFFFFFFF);
            UIUtil.setRoundBg(this, Pub.getColor(getContext(), R.attr.main_color));
        } else {
            setTextColor(Pub.getColor(getContext(), R.attr.font_color2));
            UIUtil.setRoundBg(this, Pub.getColor(getContext(), R.attr.line_color));
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
        setText(getContext().getResources().getString(R.string.regist_getcode));
    /*    setEnabled(true);
        setTextColor(getColor(R.color.check_color));
        setBackgroundResource(R.drawable.bg_button_blue_color);
        setText(getContext().getResources().getString(R.string.resend));*/
    }

    private void setEmsEnable(boolean enabled) {
        setEnabled(enabled);
        if (enabled) {
            setTextColor(Pub.getColor(getContext(), R.attr.main_color));
        } else {
            setTextColor(Pub.getColor(getContext(), R.attr.font_color4));
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

}
