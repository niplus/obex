package com.fota.android.widget.btbwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.List;

/**
 * Created by Dell on 2018/5/3.
 */

public class FotaTabLayout extends MagicIndicator {

    protected CommonNavigator commonNavigator;
    protected CommonNavigatorAdapter commonNavigatorAdapter;
    protected ViewPager mViewPager;
    protected PagerAdapter mAdapter;
    protected int statue;
    protected float customerTitleSize;
    //默认标题大小 UI给出的是18像素
    protected final float defaultTitleSize = 14;
    //是否 全屏
    private boolean isFullScreen;

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public FotaTabLayout(Context context) {
        super(context);
        init(context);
    }


    public FotaTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        commonNavigator = new CommonNavigator(context);
    }

    public void setCustomerTitleSize(float customerTitleSize) {
        this.customerTitleSize = customerTitleSize;
    }

    public void setupWithViewPager(ViewPager viewPager, PagerAdapter adapter, final List<String> labels) {
        mViewPager = viewPager;
        mAdapter = adapter;
        //AdjustMode 为true的话，会平分整个屏幕的宽度
        commonNavigator.setAdjustMode(isFullScreen);
        commonNavigatorAdapter = new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mAdapter.getCount();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(labels.get(index));
//                simplePagerTitleView.setTextSize(customerTitleSize > 0 ? customerTitleSize : defaultTitleSize);
                simplePagerTitleView.setTextSize(customerTitleSize > 0 ? customerTitleSize : defaultTitleSize);
                simplePagerTitleView.setNormalColor(Pub.getColor(getContext(), R.attr.font_color4));
                simplePagerTitleView.setSelectedColor(Pub.getColor(getContext(), R.attr.main_color));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }


            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setColors(Pub.getColor(getContext(), R.attr.main_color));
                linePagerIndicator.setYOffset(UIUtil.dip2px(getContext(), 6));
                linePagerIndicator.setLineHeight(UIUtil.dip2px(getContext(), 2));
                linePagerIndicator.setLineWidth(UIUtil.dip2px(getContext(), 18));
                return linePagerIndicator;
            }
        };
        commonNavigator.setAdapter(commonNavigatorAdapter);
        setNavigator(commonNavigator);
        bindViewPager();
    }

    protected void bindViewPager() {
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                getMagicIndicator().onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                getMagicIndicator().onPageSelected(position);
                statue = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                getMagicIndicator().onPageScrollStateChanged(state);
            }
        });
        mViewPager.setCurrentItem(statue);
        commonNavigator.onPageSelected(statue);
    }

    private MagicIndicator getMagicIndicator() {
        return this;
    }

}
