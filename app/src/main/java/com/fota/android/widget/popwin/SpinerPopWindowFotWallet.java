package com.fota.android.widget.popwin;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.Pub;

/**
 * Created by Administrator on 2018/4/19.
 */

public class SpinerPopWindowFotWallet extends SpinerPopWindow {

    public SpinerPopWindowFotWallet(Context context) {
        super(context);
    }

    @Override
    protected void setRootView(View rootView) {
        super.setRootView(rootView);
        rootView.setBackgroundColor(AppConfigs.isWhiteTheme() ? Pub.getColor(getContext(), R.attr.bg_color) :
                Pub.getColor(getContext(), R.attr.bg_color2));
    }

    @Override
    protected void setItemTvStyle(boolean isCheck, TextView tv) {
        tv.setTextColor(Pub.getColor(getContext(), R.attr.font_color));
        tv.setBackground(null);
    }
}