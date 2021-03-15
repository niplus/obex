package com.fota.android.moudles.market.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by jiang on 2018/4/26.
 */

public class NoScrollContentViewPager extends ViewPager {

    public NoScrollContentViewPager(Context context) {
        super(context);
    }

    public NoScrollContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private boolean isCanScroll = true;

    public void setScanScroll(boolean isCanScroll){
        this.isCanScroll = isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y){
        if (isCanScroll){
            super.scrollTo(x, y);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isCanScroll) {
            return super.onTouchEvent(ev);
        } else {
            return true;
        }
    }
}
