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
public class ToUpToDownView extends View {


    int arrowHidth;
    int lineWidth;


    private int color = -1;

    Paint paint;

    //无参
    public ToUpToDownView(Context context) {
        super(context);
    }

    //有参
    public ToUpToDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        arrowHidth = UIUtil.dip2px(getContext(), 8);
        lineWidth = UIUtil.dip2px(getContext(), 2);
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
        //👆 👇
        path.moveTo(0, arrowHidth);//此点为多边形的起点
        path.lineTo(getWidth() / 3, 0);
        path.lineTo(getWidth() / 3, getHeight());
        path.lineTo(getWidth() / 3 - lineWidth, getHeight());
        path.lineTo(getWidth() / 3 - lineWidth, arrowHidth);
        path.lineTo(0, arrowHidth);
        path.close();
        canvas.drawPath(path, paint);

        //用上一个的基础 减去
        //getWidth() - 0   getHeight() - getHeight()这里不写具体数值 方便理解
        Path path2 = new Path();
        path2.moveTo(getWidth(), getHeight() - arrowHidth);//此点为多边形的起点
        path2.lineTo(getWidth() - getWidth() / 3, getHeight());
        path2.lineTo(getWidth() - getWidth() / 3, getHeight() - getHeight());
        path2.lineTo(getWidth() - (getWidth() / 3 - lineWidth) + 1, getHeight() - getHeight());
        path2.lineTo(getWidth() - (getWidth() / 3 - lineWidth) + 1, getHeight() - arrowHidth);
        path2.lineTo(getWidth(), getHeight() - arrowHidth);
        path2.close();
        canvas.drawPath(path2, paint);


    }

}
