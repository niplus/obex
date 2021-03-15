package com.fota.android.widget.popwin;

import android.content.Context;
import android.widget.PopupWindow;

public class BasePopWindow extends PopupWindow {

    WindowCloseListener closeListener;

    public BasePopWindow(Context context) {
        super(context);
    }

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
