package com.fota.android.widget.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.fota.android.commonlib.utils.UIUtil;

public class ExchangeBehavior2 extends CoordinatorLayout.Behavior<View> {
    // 列表顶部和title底部重合时，列表的滑动距离。
    private float deltaY;


    public ExchangeBehavior2() {
    }

    public ExchangeBehavior2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof ViewPager;
    }

    //被观察的view发生改变时回调
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (deltaY == 0) {
            deltaY = dependency.getY() - child.getHeight();
        }
        float dy = dependency.getY() - child.getHeight();

        float dp40 = UIUtil.dip2px(child.getContext(), 40);

        float dependency_40 = dependency.getY() - dp40;

        if (dependency_40 < dp40) {
            child.setTranslationY(dp40);
        } else {
            child.setTranslationY(dependency_40);
        }


        return true;
    }
}
