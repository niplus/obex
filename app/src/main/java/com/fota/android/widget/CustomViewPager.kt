package com.fota.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager

class CustomViewPager: ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0;
        val child = getChildAt(currentItem)
        if (child != null) {
            child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            var h = child.measuredHeight;
            height = h
        }

//        for (i in 0 until childCount) {
//            val child = getChildAt(i)
//            child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            var h = child.measuredHeight;
//            if (h > height)
//                height = h;
//        }

        val myHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, myHeightMeasureSpec)
    }

}