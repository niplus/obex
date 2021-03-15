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
public class ViewPagerBackView extends View {


    float borderWidth;
    float arrowWidth;
    float arrowHeight;
    float diffWidth;

    boolean isCheck;
    private float scoll;

    private int color = -1;

    Paint paint;


    //无参
    public ViewPagerBackView(Context context) {
        super(context);
    }

    //有参
    public ViewPagerBackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        arrowWidth = UIUtil.dip2px_float(getContext(), 8);
        arrowHeight = UIUtil.dip2px_float(getContext(), 4);
        diffWidth = UIUtil.dip2px_float(getContext(), 8);
        borderWidth = UIUtil.dip2px_float(getContext(), 1);
//        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.DownTrigonView);
//        if (ta.hasValue(R.styleable.DownTrigonView_view_color)) {
//            color = ta.getColor(R.styleable.DownTrigonView_view_color, 0);
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        paint.reset();
        paint.setStyle(Paint.Style.STROKE);//设置为空心
        paint.setStrokeWidth(borderWidth);
        color = Pub.getColor(getContext(), isCheck ? R.attr.font_color : R.attr.font_color4);
        paint.setColor(color);


        float out = borderWidth / 2;
        //实例化路径
        Path path = new Path();

        path.moveTo(diffWidth + out, out);// 左上角的点

        path.lineTo(getWidth() - out, out);//右上角

        path.lineTo(getWidth() - diffWidth - out, getHeight() - arrowHeight - out);//右下角

        if (isCheck) {

            float bottomMiddle = (getWidth() - diffWidth) / 2 + scoll;

            path.lineTo(bottomMiddle + arrowWidth / 2, getHeight() - arrowHeight - out);

            path.lineTo(bottomMiddle, getHeight());

            path.lineTo(bottomMiddle - arrowWidth / 2, getHeight() - arrowHeight - out);

        }

        path.lineTo(0 + out, getHeight() - arrowHeight - out);

        path.lineTo(diffWidth + out, 0 + out);

        canvas.drawPath(path, paint);
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
        invalidate();
    }

    public float getScoll() {
        return scoll;
    }

    public void setScoll(float scoll) {
        this.scoll = scoll;
        invalidate();
    }

    public void setScollPercent(float scoll) {
        this.scoll = scoll * getWidth() / 2;
        float left = borderWidth / 2 - (getWidth() - diffWidth) / 2 + arrowWidth / 2;
        float right = getWidth() / 2 - borderWidth / 2 - diffWidth / 2 - arrowWidth / 2;
        if (this.scoll > right) {
            this.scoll = right;
        }
        if (this.scoll < left) {
            this.scoll = left;
        }
        invalidate();
    }
}
