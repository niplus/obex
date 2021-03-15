package com.fota.android.widget.btbwidget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.Pub;

/**
 * Created by Dell on 2018/4/19.
 */

public class FotaTextView extends TextView {

    private float price;

    ValueAnimator valueAnimator;


    public FotaTextView(Context context) {
        super(context);
    }

    public FotaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public FotaTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.setHintTextColor(Pub.getColor(context, R.attr.font_color4));
    }

    /**
     * 判断现在是上涨 还是下跌
     *
     * @param isUp
     */
    public void setIsUp(boolean isUp) {
        this.setTextColor(AppConfigs.getColor(isUp));
    }

    public void setBtbEnable(boolean enabled) {
        setEnabled(enabled);
        if (enabled) {
            setTextColor(Pub.getColor(getContext(), R.attr.font_color));
        } else {
            setTextColor(Pub.getColor(getContext(), R.attr.font_color3));
        }
    }

    //动画时长
    private final int duration = 500;

    public void setNumberText(final float price, final String format) {
        //修改number属性，会调用setNumber方法
        valueAnimator = ValueAnimator.ofFloat(0, 1).
                setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fract = valueAnimator.getAnimatedFraction();
                setText(String.format(format, Pub.getPriceFormat(price * fract)));
            }
        });
        valueAnimator.start();
    }

    public void setNumberText(final float price) {
        //修改number属性，会调用setNumber方法
        valueAnimator = ValueAnimator.ofFloat(0, 1).
                setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fract = valueAnimator.getAnimatedFraction();
                setText(Pub.getPriceFormat(price * fract));
            }
        });
        valueAnimator.start();
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        setText(price + "");
        this.price = price;
    }

    public void cancel() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public void setRMBText(String text) {
        //999.00
        if (Pub.isStringEmpty(text)) {
            this.setText("");
        } else {
            float defaultPx = getTextSize();  //px
            double biggerSp = 0.7;
            //int firstPoint = text.indexOf(".");
            SpannableString spanStrContent = new SpannableString((AppConfigs.isChinaLanguage() ? "¥ " : "$ ") + text);
            spanStrContent.setSpan(new AbsoluteSizeSpan((int) (defaultPx * biggerSp)), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanStrContent.setSpan(new AbsoluteSizeSpan((int) (defaultPx)), 2, 2 + text.length(), Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
            /*if (firstPoint > 0) {
                //999   00  -> ¥  999  .00
                spanStrContent.setSpan(new AbsoluteSizeSpan((int) (defaultPx)), 2, 2 + firstPoint, Spanned
                        .SPAN_EXCLUSIVE_EXCLUSIVE);
                spanStrContent.setSpan(new AbsoluteSizeSpan((int) (defaultPx * biggerSp)), 2 + firstPoint, text.length() + 2,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                //999  ->¥  999
                spanStrContent.setSpan(new AbsoluteSizeSpan((int) (defaultPx)), 2, 2 + text.length(), Spanned
                        .SPAN_EXCLUSIVE_EXCLUSIVE);
            }*/
            this.setText(spanStrContent);
        }
    }
}