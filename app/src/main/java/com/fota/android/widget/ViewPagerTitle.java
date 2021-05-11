package com.fota.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;

import java.util.List;

public class ViewPagerTitle extends LinearLayout {

    private List<String> titles;
    private int index;
    private ViewPager viewPager;

    SelectItemListener listener;

    public ViewPagerTitle(Context context) {
        super(context);
        init();
    }

    public ViewPagerTitle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPagerTitle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {

    }

    /**
     * 头部View
     *
     * @param titles
     */
    public void initTitles(List<String> titles) {
        this.titles = titles;
        removeAllViews();
        if (Pub.isListExists(titles)) {
            for (int i = 0; i < titles.size(); i++) {
                getItemView(titles.get(i), i);
            }
            setCurrentItem(0);
        }

    }


    /**
     * 添加chilid
     *
     * @param s
     * @param i
     * @return
     */
    private void getItemView(String s, final int i) {
        View view = View.inflate(getContext(), R.layout.item_viewpager_title, null);
        LayoutParams weight1 = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        view.setLayoutParams(weight1);
        TextView textView = view.findViewById(R.id.item_viewpager_title_title);
        textView.setText(s);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItem(i);
                if (viewPager != null) {
                    viewPager.setCurrentItem(i);
                }
            }
        });
        addView(view);
    }


    public void setCurrentItem(int position) {
        index = position;
        if (!Pub.isListExists(titles)) {
            return;
        }
        for (int i = 0; i < titles.size(); i++) {
            setSelect(i, i == position);
            if (i == position && listener != null) {
                listener.click(position);
            }
        }

    }

    /**
     * 设置选中样式
     *
     * @param i
     * @param b
     */
    private void setSelect(int i, boolean b) {
        TextView tv = getChildAt(i).findViewById(R.id.item_viewpager_title_title);
        int color = Pub.getColor(getContext(), b ? R.attr.font_color : R.attr.font_color4);
        tv.setTextColor(color);
        View view = getChildAt(i).findViewById(R.id.v_line);
        view.setVisibility(b?View.VISIBLE:View.GONE);


        //view.setVisibility(b ? VISIBLE : INVISIBLE);
    }


    public void bindViewpager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (position == index) {
//                    ViewPagerBackView view = getChildAt(position).findViewById(R.id.item_viewpager_title_tab);
//                    view.setScollPercent(positionOffset);
//                }
//                if (position == index - 1) {
//                    ViewPagerBackView view = getChildAt(position + 1).findViewById(R.id.item_viewpager_title_tab);
//                    view.setScollPercent(positionOffset - 1);
//                }

            }

            @Override
            public void onPageSelected(int position) {
                setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public interface SelectItemListener {
        void click(int position);
    }

    public SelectItemListener getListener() {
        return listener;
    }

    public void setListener(SelectItemListener listener) {
        this.listener = listener;
    }

    public int getIndex() {
        return index;
    }
}
