package com.guoziwei.fota.chart.view.fota;

/**
 * Created by Administrator on 2016/2/1.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.icu.text.UnicodeSetSpanner;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.guoziwei.fota.R;
import com.guoziwei.fota.util.DoubleUtil;

/**
 * Custom implementation of the MarkerView.
 *
 * @author  jiang
 */
public abstract class FotaChartMarkerView extends MarkerView {
    //y轴实际数据
    private float yValue;

    public float getyValue() {
        return yValue;
    }

    public void setyValue(float yValue) {
        this.yValue = yValue;
    }

    //最后要绘制的位置
    private float finalY;

    public float getFinalY() {
        return finalY;
    }

    public void setFinalY(float finalY) {
        this.finalY = finalY;
    }

    protected int digits = 2;
    protected TextView tvContent;
    protected FotaAutoTriagleView triagleView;
    //marker在上面，箭头向下
    protected boolean isTop;

    protected boolean needDraw = false;
//    protected float mFinayY;
//
//    public float getmFinayY() {
//        return mFinayY;
//    }
//
//    public void setmFinayY(float mFinayY) {
//        this.mFinayY = mFinayY;
//    }

    public boolean isNeedDraw() {
        return needDraw;
    }

    public void setNeedDraw(boolean needDraw) {
        this.needDraw = needDraw;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
        if(triagleView != null) {
            triagleView.setTop(top);
        }
        setTopOrnotGravity();
    }

    protected  void setTopOrnotGravity() {
        if(tvContent != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)tvContent.getLayoutParams();
            if(isTop) {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int)Utils.convertDpToPixel(16));
                params.gravity = Gravity.TOP;
                tvContent.setLayoutParams(params);
            } else {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int)Utils.convertDpToPixel(16));
                params.gravity = Gravity.BOTTOM;
                tvContent.setLayoutParams(params);
            }
        }
    }

    public FotaChartMarkerView(Context context, int digits, int layResource) {
        super(context, layResource);
        this.digits = digits;
        tvContent = (TextView) findViewById(R.id.tvContent);
        triagleView = findViewById(R.id.triangle);

    }


    /**
     * @param color
     * 此调用放在refreshContent之后，以修改设置的颜色
     */
    public void setBaseColorForCandel(int color) {
        triagleView.setmBorderColor(color);
        tvContent.setTextColor(color);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {

        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();
        float left = 0;
        float right = canvas.getWidth();
        float top = 0;
        float bottom = canvas.getHeight() + getMeasuredHeight();
        canvas.clipRect(left, top, right, bottom);
        // translate to the correct position and draw
        //jiang
        float additionOffsetX = 1.5f*triagleView.getArrowWidth();
        float additionOffsetY = isTop ? -9f : 9f;
        canvas.translate(posX + offset.x - additionOffsetX, posY + offset.y + additionOffsetY);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }
}
