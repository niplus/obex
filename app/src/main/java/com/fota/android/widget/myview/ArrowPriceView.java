package com.fota.android.widget.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.UIUtil;

/**
 * fjw绘制
 * lineWidth = 中间的长度
 * <p>
 * arrowHidth = 箭头的高度
 * <p>
 * getWith = 箭头的宽度
 * <p>
 * 以下跌未模板  从左上角的点开始画
 */
public class ArrowPriceView extends View {


    boolean isUp;

    int arrowHight;
    int lineWidth;


    private int color = -1;

    Paint paint;

    //无参
    public ArrowPriceView(Context context) {
        super(context);
    }

    //有参
    public ArrowPriceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        arrowHight = UIUtil.dip2px(getContext(), 5);
        lineWidth = UIUtil.dip2px(getContext(), 2);
    }

    public void setWidthHeight(int width, int height) {
        this.lineWidth = width;
        this.arrowHight = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        paint.reset();
        color = AppConfigs.getColor(isUp);
        paint.setColor(color);
        paint.setAntiAlias(true);
        //实例化路径
        Path path = new Path();

        if (isUp) {
            path.moveTo(getWidth() / 2 - lineWidth / 2, getHeight());//箭头左上角

            path.lineTo(getWidth() / 2 + lineWidth / 2, getHeight()); //箭头右上角

            path.lineTo(getWidth() / 2 + lineWidth / 2, arrowHight); //箭头凹点

            path.lineTo(getWidth(), arrowHight);//箭头最右边

            path.lineTo(getWidth() / 2, 0); //最低点

            path.lineTo(0, arrowHight); //箭头最左边

            path.lineTo(getWidth() / 2 - lineWidth / 2, arrowHight);//箭头凹点

            path.lineTo(getWidth() / 2 - lineWidth / 2, getHeight());//箭头左上角

        } else {

            path.moveTo(getWidth() / 2 - lineWidth / 2, 0);//箭头左上角

            path.lineTo(getWidth() / 2 + lineWidth / 2, 0); //箭头右上角

            path.lineTo(getWidth() / 2 + lineWidth / 2, getHeight() - arrowHight); //箭头凹点

            path.lineTo(getWidth(), getHeight() - arrowHight);//箭头最右边

            path.lineTo(getWidth() / 2, getHeight()); //最低点

            path.lineTo(0, getHeight() - arrowHight); //箭头最左边

            path.lineTo(getWidth() / 2 - lineWidth / 2, getHeight() - arrowHight);//箭头凹点

            path.lineTo(getWidth() / 2 - lineWidth / 2, 0);//箭头左上角

        }

        path.close();
        canvas.drawPath(path, paint);
    }

    public void setUp(boolean up) {
        isUp = up;
        invalidate();
    }
}
