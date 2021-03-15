package com.fota.android.widget.btbwidget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * desc:自动轮播的viewpager
 * author：zg
 * date:16/8/27
 * time:下午12:29
 */
public class AutoPlayViewPager extends ViewPager {
    private Handler mHandler = new Handler();

    /**
     * 图片自动轮播Task
     */
    private Runnable mImageTimerTask = new Runnable() {

        @Override
        public void run() {
            if (count > 0) {
                // 下标等于图片列表长度说明已滚动到最后一张图片,重置下标
                int currentIndex = getCurrentItem();
                ++currentIndex;
                setCurrentItem(currentIndex);
            }
        }
    };

    public AutoPlayViewPager(Context context) {
        super(context);
        init();
    }

    public AutoPlayViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        addOnPageChangeListener(new AutoPlayPageChangeListener(this));
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
    }


    /**
     * 开始图片滚动任务
     */
    public void startImageTimerTask() {
        stopImageTimerTask();
        // 图片每7秒滚动一次
        if (null != mHandler && null != mImageTimerTask) {
            mHandler.postDelayed(mImageTimerTask, 3000);
        }
    }

    /**
     * 停止图片滚动任务
     */
    public void stopImageTimerTask() {
        if (mHandler != null && mImageTimerTask != null) {
            mHandler.removeCallbacks(mImageTimerTask);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopImageTimerTask();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startImageTimerTask();
    }
}
