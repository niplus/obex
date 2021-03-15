package com.fota.android.widget.btbwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.fota.android.commonlib.utils.L;

public class ListenerTouchWindowView extends View {


    public ListenerTouchWindowView(Context context) {
        super(context);
    }

    public ListenerTouchWindowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ListenerTouchWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        L.e("onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        L.e("onDetachedFromWindow");
    }
}
