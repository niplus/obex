package com.fota.android.core.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fota.android.widget.btbwidget.FotaButton;
import com.fota.android.widget.btbwidget.FotaTextWatch;
import com.fota.android.widget.popwin.WindowCloseListener;

public abstract class BaseDialog extends Dialog {

    private View[] mViews;
    private FotaButton btSure;

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStart() {
        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    public void bindValid(FotaButton btSure, View... views) {
        this.mViews = views;
        this.btSure = btSure;
        if (views == null && views.length == 0) {
            return;
        }
        for (int i = 0; i < views.length; i++) {
            if (views[i] instanceof TextView) {
                ((TextView) views[i]).addTextChangedListener(new FotaTextWatch() {
                    @Override
                    protected void onTextChanged(String s) {
                        valid();
                    }
                });
            }

            if (views[i] instanceof CheckBox) {
                ((CheckBox) views[i]).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        valid();
                    }
                });
            }
        }
        valid();
    }

    /**
     * 验证
     *
     * @return
     */
    final public void valid() {
        boolean SysValid = systemValid();
        boolean customerValid = customerValid();
        btSure.setBtbEnabled(SysValid && customerValid);
    }

    /**
     * 系统认证
     *
     * @return
     */
    private boolean systemValid() {
        if (mViews == null || mViews.length == 0) {
            return true;
        }
        for (int i = 0; i < mViews.length; i++) {
            //只有可见的 才验证
            if (mViews[i].getVisibility() == View.VISIBLE) {
                if (mViews[i] instanceof TextView && !(mViews[i] instanceof CheckBox)) {
                    String text = ((TextView) mViews[i]).getText().toString().trim();
                    if (TextUtils.isEmpty(text)) {
                        return false;
                    }
                }

                if (mViews[i] instanceof CheckBox) {
                    boolean checked = ((CheckBox) mViews[i]).isChecked();
                    if (!checked) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 用户补充认证
     *
     * @return
     */
    protected boolean customerValid() {
        return true;
    }


    WindowCloseListener closeListener;

    @Override
    public void dismiss() {
        if (closeListener != null) {
            closeListener.dismiss();
        }
        super.dismiss();
    }

    public void setCloseListener(WindowCloseListener closeListener) {
        this.closeListener = closeListener;
    }
}
