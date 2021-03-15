package com.fota.android.widget.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;

/**
 * 三角形
 * Created by LGL on 2016/1/7.
 */
public class RightTrigonView extends View {

    private int color = -1;

    //无参
    public RightTrigonView(Context context) {
        super(context);
    }

    //有参
    public RightTrigonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RightTrigonView);
        if (ta.hasValue(R.styleable.RightTrigonView_view_color)) {
            color = ta.getColor(R.styleable.RightTrigonView_view_color, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        if (color == -1) {
            color = Pub.getColor(getContext(), R.attr.font_color);
        }
        p.setColor(color);
        //实例化路径
        Path path = new Path();
        path.moveTo(0, 0);// 此点为多边形的起点
        path.lineTo(getWidth(), getHeight() / 2);
        path.lineTo(0, getHeight());
        path.lineTo(0, 0);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

    }
}