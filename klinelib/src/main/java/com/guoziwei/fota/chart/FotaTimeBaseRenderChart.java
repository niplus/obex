package com.guoziwei.fota.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonPub;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.guoziwei.fota.R;
import com.guoziwei.fota.chart.render.AppCombinedChartRenderer;
import com.guoziwei.fota.chart.render.FotaTimeBarXAxisRenderer;
import com.guoziwei.fota.chart.view.LineChartXMarkerView;
import com.guoziwei.fota.chart.view.LineChartYMarkerView;
import com.guoziwei.fota.util.DateUtils;

/**
 * Created by JIANG on 2018/09/05.
 * 只用来显示底部的时间线
 */

public class FotaTimeBaseRenderChart extends CombinedChart {

    private float mYCenter;

    public FotaTimeBaseRenderChart(Context context) {
        this(context, null);
    }

    public FotaTimeBaseRenderChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FotaTimeBaseRenderChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mXAxisRenderer = new FotaTimeBarXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);
    }

    public void setXAisRender(XAxisRenderer renderer) {
        mXAxisRenderer = renderer;
    }

    @Override
    public void setData(CombinedData data) {
        try {
            super.setData(data);
        } catch (ClassCastException e) {
            // ignore
        }
    }

    /**
     * @param canvas jiang add comment
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (mData == null)
            return;

        if (mAutoScaleMinMaxEnabled) {
            autoScale();
        }
        mXAxisRenderer.computeAxis(mXAxis.mAxisMinimum, mXAxis.mAxisMaximum, false);
        mXAxisRenderer.renderAxisLabels(canvas);
    }

    /**
     * 设置图表中Y居中的值
     */
    public void setYCenter(float YCenter) {
        mYCenter = YCenter;
    }
}
