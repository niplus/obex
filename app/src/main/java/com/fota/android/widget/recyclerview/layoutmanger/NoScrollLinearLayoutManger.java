package com.fota.android.widget.recyclerview.layoutmanger;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 控制滑动速度的LinearLayoutManager
 */
public class NoScrollLinearLayoutManger extends LinearLayoutManager {

    private boolean isScrollEnabled = false;

    public NoScrollLinearLayoutManger(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}