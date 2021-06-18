package com.fota.android.core.anim;

import android.view.View;

public  class ViewWrapper {

    private View rView;
    private View mRootView;

    public ViewWrapper(View target, View rootView) {
        rView = target;
        mRootView = rootView;
    }

    public ViewWrapper(View target){
        rView = target;
    }

    public int getWidth() {
        return rView.getLayoutParams().width;
    }

    public void setWidth(int width) {
        rView.getLayoutParams().width = width;
//        rView.invalidate();
        if (mRootView == null) rView.requestLayout();
        else mRootView.requestLayout();
    }

    public int getHeight() {
        return rView.getLayoutParams().height;
    }

    public void setHeight(int height) {
        rView.getLayoutParams().height = height;
        if (mRootView == null) rView.requestLayout();
        else mRootView.requestLayout();
    }
}