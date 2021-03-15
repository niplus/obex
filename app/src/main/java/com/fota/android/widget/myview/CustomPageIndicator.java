package com.fota.android.widget.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.fota.android.commonlib.utils.L;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.commonlib.utils.WelcomeUtil;

import java.util.ArrayList;

public class CustomPageIndicator extends View {
    private int circleCount = 4;

    private int activeCircleIndex = 0;
    private int circleRadius;// 半径4
    private int activeCircleRadius;// 当前小球半径6
    private int circleDivider;// 间距6
    private ArrayList<Circle> circles = null;

    Paint mPaint;
    private int normalCircleColor;
    private int acitiveCircleColor;
    private int alpaColor = Color.parseColor("#00FFFFFF");

    public CustomPageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CustomPageIndicator(Context context) {
        this(context, null);
    }

    public CustomPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initCircle();
    }

    private void initCircle() {
        normalCircleColor = Color.parseColor("#80FFFFFF");
        acitiveCircleColor = Color.parseColor("#FFFFFFFF");
        // 填充、抗锯齿--颜色、大小
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        circleRadius = UIUtil.dip2px(getContext(), 2);
        activeCircleRadius = UIUtil.dip2px(getContext(), 4);
        circleDivider = UIUtil.dip2px(getContext(), 14);
        circles = new ArrayList<Circle>();
        // 创建3个原点集合
        for (int i = 0; i < circleCount; i++) {
            Circle circle = new Circle();
            if (activeCircleIndex == i) {
                // 当前的点位
                circle.color = acitiveCircleColor;
                circle.radius = activeCircleRadius;
                circle.centerX = activeCircleRadius;
                circle.centerY = activeCircleRadius;

            } else {
                // 其他点位
                circle.color = normalCircleColor;
                circle.radius = circleRadius;
                circle.centerX = activeCircleRadius + i
                        * circleDivider;
                circle.centerY = activeCircleRadius;
            }
            circles.add(circle);
        }
    }

    /**
     * 设置圆点大小 z最多至原来的2/3
     *
     * @param percent
     */
    public void setIndiaSize(float percent) {
        L.a("setIndiaSize percent = " + percent);
        percent = 2.0f / 3 + percent / 3f;
        circleRadius = (int) (UIUtil.dip2px(getContext(), 2) * percent);
        activeCircleRadius = (int) (UIUtil.dip2px(getContext(), 4) * percent);
        circleDivider = (int) (UIUtil.dip2px(getContext(), 14) * percent);
        if (percent == 1) {
            for (int i = 0; i < circles.size(); i++) {
                if (0 == i) {
                    // 当前的点位
                    circles.get(i).centerX = activeCircleRadius;
                } else {
                    // 其他点位
                    circles.get(i).centerX = activeCircleRadius + i
                            * circleDivider;
                }
            }
            invalidate();
        }

    }

    /**
     * 设置圆点大小 z最多至原来的2/3
     *
     * @param percent
     */
    public void setFullIndiaSize(int index, float percent) {
        L.a("setIndiaSize percent = " + percent);
        percent = 2.0f / 3 + percent / 3f;
        circleRadius = (int) (UIUtil.dip2px(getContext(), 2) * percent);
        activeCircleRadius = (int) (UIUtil.dip2px(getContext(), 4) * percent);
        circleDivider = (int) (UIUtil.dip2px(getContext(), 14) * percent);
        for (int i = 0; i < circles.size(); i++) {
            if (0 == i) {
                // 当前的点位
                circles.get(i).centerX = activeCircleRadius;
            } else {
                // 其他点位
                circles.get(i).centerX = activeCircleRadius + i
                        * circleDivider;
            }

            if (index == i) {
                circles.get(i).radius = activeCircleRadius;
                circles.get(i).color = acitiveCircleColor;
            } else {
                circles.get(i).radius = circleRadius;
                circles.get(i).color = normalCircleColor;
                // 其他点位
            }
            invalidate();

        }


    }

    /**
     * 设置目前活动的小球
     * 第一部分根据startValue求出其中A,R,G,B中各个色彩的初始值；第二部分根据endValue求出其中A,R,G,B中各个色彩的结束值
     * ，最后是根据当前动画的百分比进度求出对应的数值
     */
    public void setActivityCircle(int index) {
        lastCircle = circles.get(activeCircleIndex);
        lastCircle.color = WelcomeUtil.evaluate(0.0f, normalCircleColor,
                acitiveCircleColor);
        lastCircle.radius = circleRadius;
        activeCircleIndex = index;
        currentActivedCircele = circles.get(activeCircleIndex);
        currentActivedCircele.color = WelcomeUtil.evaluate(0.0f,
                acitiveCircleColor, normalCircleColor);
        currentActivedCircele.radius = activeCircleRadius;
        for (int i = 0; i < circles.size(); i++) {
            if (i != index) {
                circles.get(i).radius = circleRadius;
            }
            if (0 == i) {
                // 当前的点位
                circles.get(i).centerX = activeCircleRadius;
            } else {
                // 其他点位
                circles.get(i).centerX = activeCircleRadius + i
                        * circleDivider;
            }

        }
        invalidate();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resultWidth = 0;
        int resultHeight = 0;

//        resultWidth = circleCount * circleRadius * 2 + (circleCount - 1)
//                * circleDivider + 2 * (activeCircleRadius - circleRadius);
        resultWidth = circleRadius + (circleCount - 1)
                * circleDivider + activeCircleRadius;
        resultHeight = 2 * activeCircleRadius;
        setMeasuredDimension(resultWidth, resultHeight);
    }

    public int getIndicWidth() {
        int resultWidth = circleCount * circleRadius * 2 + (circleCount - 1)
                * circleDivider + 2 * (activeCircleRadius - circleRadius);
        return resultWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < circleCount; i++) {
            Circle circle = circles.get(i);
            mPaint.setColor(circle.color);
            canvas.drawCircle(circle.centerX, circle.centerY, circle.radius,
                    mPaint);
        }
    }

    /**
     * 圆心xy坐标，半径，颜色
     */
    private class Circle {
        float centerX;
        float centerY;
        float radius;
        int color;

    }

    private float lastPercent = -1;
    private Circle lastCircle;
    private Circle currentActivedCircele;
    private Circle right;
    private Circle left;

    public void setScrollProgress(int leftPosition, float percent) {
        right = circles.get(leftPosition + 1);
        left = circles.get(leftPosition);

        // 手指右滑-递减
        if (lastPercent > percent) {
            left.radius = circleRadius + (1 - percent)
                    * UIUtil.dip2px(getContext(), 2);
            right.radius = circleRadius + percent
                    * UIUtil.dip2px(getContext(), 2);
            if (left.color == normalCircleColor) {
                left.color = WelcomeUtil.evaluate(percent, normalCircleColor,
                        acitiveCircleColor);
            } else {
                left.color = WelcomeUtil.evaluate(percent, acitiveCircleColor,
                        normalCircleColor);
            }
            if (right.color == normalCircleColor) {
                right.color = WelcomeUtil.evaluate(1 - percent,
                        normalCircleColor, acitiveCircleColor);
            } else {
                right.color = WelcomeUtil.evaluate(1 - percent,
                        acitiveCircleColor, normalCircleColor);
            }


        }
        // 手指左滑-递增
        if (lastPercent < percent) {
            left.radius = circleRadius + (1 - percent)
                    * UIUtil.dip2px(getContext(), 2);
            right.radius = circleRadius + percent
                    * UIUtil.dip2px(getContext(), 2);
            if (right.color == normalCircleColor) {
                right.color = WelcomeUtil.evaluate(1 - percent,
                        normalCircleColor, acitiveCircleColor);
            } else {
                right.color = WelcomeUtil.evaluate(1 - percent,
                        acitiveCircleColor, normalCircleColor);
            }
            if (left.color == normalCircleColor) {
                left.color = WelcomeUtil.evaluate(percent, normalCircleColor,
                        acitiveCircleColor);
            } else {
                left.color = WelcomeUtil.evaluate(percent, acitiveCircleColor,
                        normalCircleColor);
            }

        }

        for (int i = 0; i < circles.size(); i++) {
            if (i != leftPosition && i != (leftPosition + 1)) {
                circles.get(i).radius = circleRadius;
            }
            if (0 == i) {
                // 当前的点位
                circles.get(i).centerX = activeCircleRadius;

            } else {
                // 其他点位
                circles.get(i).centerX = activeCircleRadius + i
                        * circleDivider;
            }
        }
        invalidate();

        lastPercent = percent;
    }


}
