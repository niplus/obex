package com.guoziwei.fota.chart.render;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Renderer class that is responsible for rendering multiple different data-types.
 */
public class AppCombinedChartRenderer extends DataRenderer {
    private boolean isDrawLastCircle;
    //拆分之后不再需要isDrawHighLow
//    private boolean isDrawHighLow;

//    public void setDrawHighLow(boolean drawHighLow) {
//        isDrawHighLow = drawHighLow;
//        CombinedChart chart = (CombinedChart)mChart.get();
//        if (chart == null)
//            return;
//        for (DataRenderer renderer : mRenderers) {
//            if (renderer instanceof CandleStickChartRenderer) {
//                CandleStickChartRenderer chartRenderer = (CandleStickChartRenderer) renderer;
//                chartRenderer.setDrawHighLowIndicate(isDrawHighLow);
//                break;
//            }
//        }
//    }
    private int mDigits;

    public int getmDigits() {
        return mDigits;
    }

    public void setmDigits(int mDigits) {
        this.mDigits = mDigits;
    }

    /**
     * @param drawLastCircle
     * 设置混合模式的lineRender
     * 是否默认绘制最后的一个点
     */
    public void setDrawLastCircle(boolean drawLastCircle) {
        isDrawLastCircle = drawLastCircle;
        CombinedChart chart = (CombinedChart)mChart.get();
        if (chart == null)
            return;
        for (DataRenderer renderer : mRenderers) {
            if (renderer instanceof AppLineChartRenderer) {
                AppLineChartRenderer lineChartRenderer = (AppLineChartRenderer) renderer;
                lineChartRenderer.setDrawLastCircle(isDrawLastCircle);
                break;
            }
        }
    }

    /**
     * all rederers for the different kinds of data this combined-renderer can draw
     */
    protected List<DataRenderer> mRenderers = new ArrayList<DataRenderer>(5);

    protected WeakReference<Chart> mChart;

    public AppCombinedChartRenderer(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = new WeakReference<Chart>(chart);
        createRenderers();
    }

    /**
     * Creates the renderers needed for this combined-renderer in the required order. Also takes the DrawOrder into
     * consideration.
     */
    public void createRenderers() {

        mRenderers.clear();

        CombinedChart chart = (CombinedChart)mChart.get();
        if (chart == null)
            return;

        DrawOrder[] orders = chart.getDrawOrder();

        for (DrawOrder order : orders) {

            switch (order) {
                case BAR:
                    if (chart.getBarData() != null)
                        mRenderers.add(new BarChartRenderer(chart, mAnimator, mViewPortHandler));
                    break;
                case BUBBLE:
                    if (chart.getBubbleData() != null)
                        mRenderers.add(new BubbleChartRenderer(chart, mAnimator, mViewPortHandler));
                    break;
                case LINE:
                    if (chart.getLineData() != null) {
                        AppLineChartRenderer lineChartRenderer = new AppLineChartRenderer(chart, mAnimator, mViewPortHandler);
                        mRenderers.add(lineChartRenderer);
                    }
                    break;
                case CANDLE:
                    if (chart.getCandleData() != null) {
                        CandleStickChartRenderer renderer = new CandleStickChartRenderer(chart, mAnimator, mViewPortHandler);
                        renderer.setmDigits(mDigits);
                        mRenderers.add(renderer);
                    }
                    break;
                case SCATTER:
                    if (chart.getScatterData() != null)
                        mRenderers.add(new ScatterChartRenderer(chart, mAnimator, mViewPortHandler));
                    break;
            }
        }
    }

    @Override
    public void initBuffers() {

        for (DataRenderer renderer : mRenderers)
            renderer.initBuffers();
    }

    @Override
    public void drawData(Canvas c) {

        for (DataRenderer renderer : mRenderers)
            renderer.drawData(c);
    }

    @Override
    public void drawValues(Canvas c) {

        for (DataRenderer renderer : mRenderers)
            renderer.drawValues(c);
    }

    @Override
    public void drawExtras(Canvas c) {

        for (DataRenderer renderer : mRenderers)
            renderer.drawExtras(c);
    }

    protected List<Highlight> mHighlightBuffer = new ArrayList<Highlight>();

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        Chart chart = mChart.get();
        if (chart == null) return;

        for (DataRenderer renderer : mRenderers) {
            ChartData data = null;

            if (renderer instanceof BarChartRenderer)
                data = ((BarChartRenderer)renderer).mChart.getBarData();
            else if (renderer instanceof AppLineChartRenderer)
                data = ((AppLineChartRenderer)renderer).mChart.getLineData();
            else if (renderer instanceof CandleStickChartRenderer)
                data = ((CandleStickChartRenderer)renderer).mChart.getCandleData();
            else if (renderer instanceof ScatterChartRenderer)
                data = ((ScatterChartRenderer)renderer).mChart.getScatterData();
            else if (renderer instanceof BubbleChartRenderer)
                data = ((BubbleChartRenderer)renderer).mChart.getBubbleData();

            int dataIndex = data == null ? -1
                    : ((CombinedData)chart.getData()).getAllData().indexOf(data);

            mHighlightBuffer.clear();

            for (Highlight h : indices) {
                if (h.getDataIndex() == dataIndex || h.getDataIndex() == -1)
                    mHighlightBuffer.add(h);
            }

            renderer.drawHighlighted(c, mHighlightBuffer.toArray(new Highlight[mHighlightBuffer.size()]));
        }
    }

    /**
     * Returns the sub-renderer object at the specified index.
     *
     */
    public DataRenderer getSubRenderer(int index) {
        if (index >= mRenderers.size() || index < 0)
            return null;
        else
            return mRenderers.get(index);
    }

    /**
     * Returns all sub-renderers.
     *
     */
    public List<DataRenderer> getSubRenderers() {
        return mRenderers;
    }

    public void setSubRenderers(List<DataRenderer> renderers) {
        this.mRenderers = renderers;
    }
}
