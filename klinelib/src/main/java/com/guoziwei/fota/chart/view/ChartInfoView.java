package com.guoziwei.fota.chart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.Chart;
import com.guoziwei.fota.model.HisData;

/**
 * Created by dell on 2017/10/25.
 */

public abstract class ChartInfoView extends LinearLayout {

    public Chart[] mLineCharts;
    protected Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //jiang
//            setVisibility(GONE);
            if (mLineCharts != null) {
                for (Chart chart : mLineCharts) {
                    chart.highlightValue(null);
                }
            }
        }
    };

    public ChartInfoView(Context context) {
        super(context);
    }

    public ChartInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setData(double lastClose, HisData data);

    public abstract void setTochData(double price);

    public void setChart(Chart... chart) {
        mLineCharts = chart;
    }
}
