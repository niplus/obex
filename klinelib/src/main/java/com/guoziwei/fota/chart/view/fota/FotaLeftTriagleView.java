package com.guoziwei.fota.chart.view.fota;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.fota.android.commonlib.utils.CommonPub;
import com.github.mikephil.charting.utils.Utils;
import com.guoziwei.fota.R;

/**
 * 三角形
 * Created by  on 2016/09/06.
 */
public class FotaLeftTriagleView extends View {

    int arrowWidth = 4;
    int arrwoHeight = 6;
    float strokeWidth;

    public void setmBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
    }

    protected int mBorderColor;

    //无参
    public FotaLeftTriagleView(Context context) {
        super(context);
        strokeWidth = Utils.convertDpToPixel(1f);
    }

    //有参
    public FotaLeftTriagleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        strokeWidth = Utils.convertDpToPixel(1f);
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        for (int i = 0; i < getChildCount(); i++) {
//            getChildAt(i).layout(left, top, right, bottom);
//        }
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int mMeasuredHeight = ((View)getParent()).getMeasuredHeight();
            setMeasuredDimension(widthMeasureSpec, mMeasuredHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int bgColor = CommonPub.getColor(getContext(), R.attr.bg_color);
        Paint p = new Paint();
        p.setColor(mBorderColor);
        p.setStrokeWidth(strokeWidth);
        p.setStyle(Paint.Style.STROKE);
        Paint p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        p1.setColor(bgColor);
        p1.setStyle(Paint.Style.FILL);
        //实例化路径
        Path path = new Path();
        Path path1 = new Path();

        int arrowWidth = (int)Utils.convertDpToPixel(4);
        int arrwoHeight = (int)Utils.convertDpToPixel(6);
        float halfStroke = strokeWidth / 2;


        path.moveTo(arrowWidth, halfStroke);// 此点为多边形的起点
        path.lineTo(getWidth() - halfStroke, halfStroke);
        path.lineTo(getWidth() - strokeWidth /2 , getHeight() - halfStroke);
        path.lineTo(arrowWidth, getHeight() - halfStroke);
        path.lineTo(arrowWidth, getHeight() / 2 + arrwoHeight / 2);
        path.lineTo(halfStroke, getHeight() / 2);
        path.lineTo(arrowWidth, getHeight() / 2 - arrwoHeight / 2);
        path.lineTo(arrowWidth, halfStroke);

        path1.moveTo(arrowWidth + halfStroke, strokeWidth);// 此点为多边形的起点
        path1.lineTo(getWidth() - strokeWidth, strokeWidth);
        path1.lineTo(getWidth() - strokeWidth, getHeight() - strokeWidth);
        path1.lineTo(arrowWidth + halfStroke, getHeight() - strokeWidth);
        path1.lineTo(arrowWidth + halfStroke, getHeight() / 2 + arrwoHeight / 2);
        path1.lineTo(strokeWidth, getHeight() / 2);
        path1.lineTo(arrowWidth + halfStroke, getHeight() / 2 - arrwoHeight / 2);
        path1.lineTo(arrowWidth + halfStroke, strokeWidth);

        canvas.drawPath(path, p);
        canvas.drawPath(path1, p1);
    }
}