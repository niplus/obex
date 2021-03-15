package com.fota.android.widget.btbwidget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.List;

/**
 * Created by jiang on 2018/11/06.
 */

public class FotaMarketTabLayout extends FotaTabLayout {
    Paint mPaint = new Paint();

    public FotaMarketTabLayout(Context context) {
        super(context);
        init(context);
    }

    public FotaMarketTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        commonNavigator = new CommonNavigator(context);
    }

    public void setCustomerTitleSize(float customerTitleSize) {
        this.customerTitleSize = customerTitleSize;
    }

    public void setupWithViewPager(ViewPager viewPager,
                                   PagerAdapter adapter,
                                   final List<String> labels) {
        mViewPager = viewPager;
        mAdapter = adapter;
        //AdjustMode 为true的话，会平分整个屏幕的宽度
        commonNavigator.setAdjustMode(true);
        commonNavigatorAdapter = new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mAdapter.getCount();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(labels.get(index));
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

            //重写一下weight
            @Override
            public float getTitleWeight(Context context, int index) {
                if(!AppConfigs.isChinaLanguage()) {
                    String text = labels.get(index);
                    float width = mPaint.measureText(text);
                    if(index == 1) {
                        String tmp = labels.get(0);
                        return mPaint.measureText(tmp)/2;
                    } else if(index == 2) {
                        try {
                            String tmp = labels.get(4);
                            return mPaint.measureText(tmp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return width;
                }
                return super.getTitleWeight(context, index);
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setColors(Pub.getColor(getContext(), R.attr.main_color));
//                linePagerIndicator.setYOffset(UIUtil.dip2px(getContext(), 6));
                linePagerIndicator.setLineHeight(UIUtil.dip2px(getContext(), 1));
//                linePagerIndicator.setLineWidth(UIUtil.dip2px(getContext(), 18));
                return linePagerIndicator;
            }
        };
        commonNavigator.setAdapter(commonNavigatorAdapter);
        setNavigator(commonNavigator);
        bindViewPager();
    }

}
