package com.fota.android.widget.btbwidget;


import androidx.viewpager.widget.ViewPager;

/**
 * desc:自动轮播监听
 * author：zg
 * date:16/8/27
 * time:下午2:02
 */
public class AutoPlayPageChangeListener implements ViewPager.OnPageChangeListener {
    AutoPlayViewPager viewPager;

    public AutoPlayPageChangeListener(AutoPlayViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (null != viewPager)
            viewPager.startImageTimerTask(); // 开始下次计时
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            //不靠谱,时有时无
        }
    }
}
