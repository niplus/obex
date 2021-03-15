package com.fota.android.widget.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;

public class AddressSelectView extends View {

    //private float ringWidth;
    private Paint p;
    private int color = -1;
    private boolean isCheck;

    //无参
    public AddressSelectView(Context context) {
        super(context);
    }

    //有参
    public AddressSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.DownTrigonView);


        p = new Paint();
        //ringWidth = UIUtil.dip2px(context, 5); //设置圆环宽度
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (color == -1) {
            color = Pub.getColor(getContext(), isCheck ? R.attr.main_color : R.attr.font_color4);
        }
        p.setAntiAlias(true);
        // 1/6 1/6 1/6  1/6 1/6 1/6
        p.setColor(color);

        p.setStyle(Paint.Style.STROKE); //绘制空心圆
        p.setStrokeWidth(getWidth() / 24);
        canvas.drawCircle(getHeight() / 2, getWidth() / 2, getWidth() / 4, p);

        if (isCheck) {
            p.setStyle(Paint.Style.FILL); //绘制空心圆
            canvas.drawCircle(getHeight() / 2, getWidth() / 2, getWidth() / 8, p);
        }

    }

    public void setCheck(boolean check) {
        isCheck = check;
        invalidate();
    }
}