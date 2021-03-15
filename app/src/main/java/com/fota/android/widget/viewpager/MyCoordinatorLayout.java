package com.fota.android.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class MyCoordinatorLayout extends CoordinatorLayout {

    /**
     * 本模块有数据
     * 不拦截任何事件
     */
    boolean hasData;
    /**
     * 相邻模块有数据
     * <p>
     * false 拦截全部竖直方向
     * true  只拦截下滑方向，可以上滑
     */
    boolean hasDataAll;

    private int lastX;
    private int lastY;


    public MyCoordinatorLayout(Context context) {
        super(context);
    }

    public MyCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (hasData) {
            return super.onInterceptTouchEvent(e);
        }

        boolean isIntercept = false;
        int action = e.getAction();
        int currentX = (int) e.getX();
        int currentY = (int) e.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //记录上次滑动的位置
                lastX = currentX;
                lastY = currentY;
                isIntercept = false;//点击事件分发给子控件
                break;
            case MotionEvent.ACTION_MOVE:
                if (isParentIntercept(currentX, currentY, lastX, lastY)) {//父容器拦截
                    isIntercept = true;
                } else {//点击事件分发给子控件
                    isIntercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                isIntercept = false;//点击事件分发给子控件
                break;
        }
        Log.e("isIntercept", String.valueOf(isIntercept));
        return isIntercept;
    }

    private boolean isParentIntercept(int currentX, int currentY, int lastX, int lastY) {
        int dx = currentX - lastX;
        int dy = currentY - lastY;
        //return Math.abs(dy) > Math.abs(dx);
        if (Math.abs(dy) > Math.abs(dx)) {
            return dy < 0 && Math.abs(dy) > ViewConfiguration.get(getContext()).getScaledTouchSlop();
            //if (hasDataAll || !UserLoginUtil.havaUser()) {
            //    return dy < ViewConfiguration.get(getContext()).getScaledTouchSlop();
            //}
            //return true;
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (hasData) {
            return super.onTouchEvent(e);
        }
        return true;
    }


    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public void setHasDataAll(boolean hasDataAll) {
        this.hasDataAll = hasDataAll;
    }
}
