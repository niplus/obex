package com.fota.android.widget.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;

/**
 * 平行四边形
 */
public class ToRightView extends View {


    int arrowWidth;
    int lineHeight;


    private int color = -1;

    Paint paint;

    //无参
    public ToRightView(Context context) {
        super(context);
    }

    //有参
    public ToRightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        arrowWidth = UIUtil.dip2px(getContext(), 8);
        lineHeight = UIUtil.dip2px(getContext(), 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        paint.reset();
        color = Pub.getColor(getContext(), R.attr.main_color);
        paint.setColor(color);
        paint.setAntiAlias(true);
        //实例化路径
        Path path = new Path();
        path.moveTo(0, getHeight());//此点为多边形的起点
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth() - arrowWidth, 0);
        path.lineTo(getWidth() - arrowWidth, getHeight() - lineHeight);
        path.lineTo(0, getHeight() - lineHeight);
        path.lineTo(0, getHeight());
        path.close();
        canvas.drawPath(path, paint);
    }

}
