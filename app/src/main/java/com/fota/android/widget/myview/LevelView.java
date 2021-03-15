package com.fota.android.widget.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class LevelView extends View {

    private Paint p;
    int level = 1;

    private final static int Level_ALl = 5;
    private Rect rect;

    //无参
    public LevelView(Context context) {
        super(context);
    }

    //有参
    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                level++;
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int div = (getWidth() - getHeight() * 5) / 4;
        //int div = getWidth() - getHeight() * 1.25;
        int color = getColor();
        for (int i = 0; i < level; i++) {
            p.setColor(color);
            p.setStyle(Paint.Style.FILL);//设置空心
            int left = (div + getHeight()) * i;
            rect = new Rect(left, 1, left + getHeight(), getHeight());
            canvas.drawRect(rect, p);
        }

        for (int i = level; i < Level_ALl; i++) {
            p.setColor(0xFF606572);
            p.setStyle(Paint.Style.STROKE);//设置空心
            int left = (div + getHeight()) * i;
            rect = new Rect(left, 1, left + getHeight(), getHeight());
            canvas.drawRect(rect, p);
        }

    }

    private int getColor() {
        switch (level) {
            case 1:
            case 2:
                return 0xFF2C8F4E;
            case 3:
            case 4:
                return 0xFFF98446;
            default:
            case 5:
                return 0xFFE04D4D;
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level != this.level) {
            invalidate();
        }
        this.level = level;

    }
}