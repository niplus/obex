
package com.guoziwei.fota.chart.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonPub;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.renderer.LineScatterCandleRadarRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.guoziwei.fota.R;

import java.util.List;

public class CandleStickChartRenderer extends LineScatterCandleRadarRenderer {
    public int getmDigits() {
        return mDigits;
    }

    public void setmDigits(int mDigits) {
        this.mDigits = mDigits;
    }

    private int mDigits = 2;
    private boolean isDrawHighLowIndicate = false;
    //jiang 0822 记录touch的x值
    private float touchY;

    public void setTouchY(float touchY) {
        this.touchY = touchY;
    }

    public void setDrawHighLowIndicate(boolean drawHighLowIndicate) {
        isDrawHighLowIndicate = drawHighLowIndicate;
    }

    public CandleDataProvider mChart;

    private float[] mShadowBuffers = new float[8];
    private float[] mBodyBuffers = new float[4];
    private float[] mRangeBuffers = new float[4];
    private float[] mOpenBuffers = new float[4];
    private float[] mCloseBuffers = new float[4];

    public CandleStickChartRenderer(CandleDataProvider chart, ChartAnimator animator,
                                    ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;
    }

    @Override
    public void initBuffers() {

    }

    @Override
    public void drawData(Canvas c) {

        CandleData candleData = mChart.getCandleData();

        for (ICandleDataSet set : candleData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    @SuppressWarnings("ResourceAsColor")
    protected void drawDataSet(Canvas c, ICandleDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseY = mAnimator.getPhaseY();
        float barSpace = dataSet.getBarSpace();
        boolean showCandleBar = dataSet.getShowCandleBar();

        mXBounds.set(mChart, dataSet);

        mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

        // draw the body
        for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {

            // get the entry
            CandleEntry e = dataSet.getEntryForIndex(j);

            if (e == null)
                continue;

            final float xPos = e.getX();

            final float open = e.getOpen();
            final float close = e.getClose();
            final float high = e.getHigh();
            final float low = e.getLow();

            if (showCandleBar) {
                // calculate the shadow

                mShadowBuffers[0] = xPos;
                mShadowBuffers[2] = xPos;
                mShadowBuffers[4] = xPos;
                mShadowBuffers[6] = xPos;

                if (open > close) {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = open * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = close * phaseY;
                } else if (open < close) {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = close * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = open * phaseY;
                } else {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = open * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = mShadowBuffers[3];
                }

                trans.pointValuesToPixel(mShadowBuffers);

                // draw the shadows

                if (dataSet.getShadowColorSameAsCandle()) {

                    if (open > close)
                        mRenderPaint.setColor(
                                dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getDecreasingColor()
                        );

                    else if (open < close)
                        mRenderPaint.setColor(
                                dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getIncreasingColor()
                        );

                    else
                        mRenderPaint.setColor(
                                dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getNeutralColor()
                        );

                } else {
                    mRenderPaint.setColor(
                            dataSet.getShadowColor() == ColorTemplate.COLOR_NONE ?
                                    dataSet.getColor(j) :
                                    dataSet.getShadowColor()
                    );
                }

                mRenderPaint.setStyle(Paint.Style.STROKE);

                c.drawLines(mShadowBuffers, mRenderPaint);

                // calculate the body

                mBodyBuffers[0] = xPos - 0.5f + barSpace;
                mBodyBuffers[1] = close * phaseY;
                mBodyBuffers[2] = (xPos + 0.5f - barSpace);
                mBodyBuffers[3] = open * phaseY;

                trans.pointValuesToPixel(mBodyBuffers);

                // draw body differently for increasing and decreasing entry
                if (open > close) { // decreasing

                    if (dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getDecreasingColor());
                    }

                    mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());

                    c.drawRect(
                            mBodyBuffers[0], mBodyBuffers[3],
                            mBodyBuffers[2], mBodyBuffers[1],
                            mRenderPaint);

                } else if (open < close) {

                    if (dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getIncreasingColor());
                    }

                    mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());

                    c.drawRect(
                            mBodyBuffers[0], mBodyBuffers[1],
                            mBodyBuffers[2], mBodyBuffers[3],
                            mRenderPaint);
                } else { // equal values

                    if (dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getNeutralColor());
                    }

                    c.drawLine(
                            mBodyBuffers[0], mBodyBuffers[1],
                            mBodyBuffers[2], mBodyBuffers[3],
                            mRenderPaint);
                }
            } else {

                mRangeBuffers[0] = xPos;
                mRangeBuffers[1] = high * phaseY;
                mRangeBuffers[2] = xPos;
                mRangeBuffers[3] = low * phaseY;

                mOpenBuffers[0] = xPos - 0.5f + barSpace;
                mOpenBuffers[1] = open * phaseY;
                mOpenBuffers[2] = xPos;
                mOpenBuffers[3] = open * phaseY;

                mCloseBuffers[0] = xPos + 0.5f - barSpace;
                mCloseBuffers[1] = close * phaseY;
                mCloseBuffers[2] = xPos;
                mCloseBuffers[3] = close * phaseY;

                trans.pointValuesToPixel(mRangeBuffers);
                trans.pointValuesToPixel(mOpenBuffers);
                trans.pointValuesToPixel(mCloseBuffers);

                // draw the ranges
                int barColor;

                if (open > close)
                    barColor = dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getDecreasingColor();
                else if (open < close)
                    barColor = dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getIncreasingColor();
                else
                    barColor = dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getNeutralColor();

                mRenderPaint.setColor(barColor);
                c.drawLine(
                        mRangeBuffers[0], mRangeBuffers[1],
                        mRangeBuffers[2], mRangeBuffers[3],
                        mRenderPaint);
                c.drawLine(
                        mOpenBuffers[0], mOpenBuffers[1],
                        mOpenBuffers[2], mOpenBuffers[3],
                        mRenderPaint);
                c.drawLine(
                        mCloseBuffers[0], mCloseBuffers[1],
                        mCloseBuffers[2], mCloseBuffers[3],
                        mRenderPaint);
            }
        }
    }

    //jiang 自定义绘制的内容 左右箭头
    @Override
    public void drawValues(Canvas c) {

        List<ICandleDataSet> dataSets = mChart.getCandleData().getDataSets();

        for (int i = 0; i < dataSets.size(); i++) {

            ICandleDataSet dataSet = dataSets.get(i);

            if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            int minx = (int) Math.max(dataSet.getXMin(), 0);
            int maxx = (int) Math.min(dataSet.getXMax(), dataSet.getEntryCount() - 1);

            float[] positions = trans.generateTransformedValuesCandle(
                    dataSet, mAnimator.getPhaseX(), mAnimator.getPhaseY(), minx, maxx);


            //计算最大值和最小值
            float maxValue = 0, minValue = 0;
            int maxIndex = 0, minIndex = 0;
            CandleEntry maxEntry = null;
            boolean firstInit = true;
            for (int j = 0; j < positions.length; j += 2) {

                float x = positions[j];
                float y = positions[j + 1];

                if (!mViewPortHandler.isInBoundsRight(x))
                    break;

                if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                    continue;

                CandleEntry entry = dataSet.getEntryForIndex(j / 2 + minx);

                if (firstInit) {
                    maxValue = entry.getHigh();
                    minValue = entry.getLow();
                    firstInit = false;
                    maxEntry = entry;
                } else {
                    if (entry.getHigh() > maxValue) {
                        maxValue = entry.getHigh();
                        maxIndex = j;
                        maxEntry = entry;
                    }

                    if (entry.getLow() < minValue) {
                        minValue = entry.getLow();
                        minIndex = j;
                    }

                }
            }

            //jiang 0808
            if(!isDrawHighLowIndicate) {
                continue;
            }

            //绘制最大值和最小值
            float x = positions[minIndex];
            DefaultValueFormatter valueFormatter = new DefaultValueFormatter(mDigits);
            if (maxIndex > minIndex) {
                //画右边
                String minValueStr = valueFormatter.getFormattedValue(minValue, null, 0, null);
                String highString = "← " + minValueStr;

                //计算显示位置
                //计算文本宽度
                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString);

                float[] tPosition = new float[2];
                tPosition[1] = minValue;
                trans.pointValuesToPixel(tPosition);
                mValuePaint.setColor(dataSet.getValueTextColor(minIndex / 2));
                c.drawText(highString, x + highStringWidth / 2, tPosition[1], mValuePaint);
            } else {
                //画左边
                String minValueStr = valueFormatter.getFormattedValue(minValue, null, 0, null);
                String highString = minValueStr + " →";

                //计算显示位置
                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString);
                float[] tPosition = new float[2];
                tPosition[1] = minValue;
                trans.pointValuesToPixel(tPosition);
                mValuePaint.setColor(dataSet.getValueTextColor(minIndex / 2));
                c.drawText(highString, x - highStringWidth / 2, tPosition[1], mValuePaint);
            }

            if (maxIndex > minIndex) {
                //画左边
                String maxValueStr = valueFormatter.getFormattedValue(maxValue, null, 0, null);
                String highString = maxValueStr + " →";

                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString);

                float[] tPosition = new float[2];
                tPosition[0] = maxEntry == null ? 0f : maxEntry.getX();
                tPosition[1] = maxEntry == null ? 0f : maxEntry.getHigh();
                trans.pointValuesToPixel(tPosition);

                mValuePaint.setColor(dataSet.getValueTextColor(maxIndex / 2));
                c.drawText(highString, tPosition[0] - highStringWidth / 2, tPosition[1], mValuePaint);
            } else {
                //画右边
                String maxValueStr = valueFormatter.getFormattedValue(maxValue, null, 0, null);
                String highString = "← " + maxValueStr;

                //计算显示位置
                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString);

                float[] tPosition = new float[2];
                tPosition[0] = maxEntry == null ? 0f : maxEntry.getX();
                tPosition[1] = maxEntry == null ? 0f : maxEntry.getHigh();
                trans.pointValuesToPixel(tPosition);

                mValuePaint.setColor(dataSet.getValueTextColor(maxIndex / 2));
                c.drawText(highString, tPosition[0] + highStringWidth / 2, tPosition[1], mValuePaint);

            }

        }
//        }
    }


    @Override
    public void drawExtras(Canvas c) {
        //jiang drawLastCircle
        float highVisibleX = Math.round(mChart.getHighestVisibleX()*10)*0.1f;
        if(highVisibleX >= mChart.getXChartMax()) {
            List<ICandleDataSet> dataSets = mChart.getCandleData().getDataSets();
            drawCandleLastCircle(c, dataSets.get(0));
        }
    }

    //jiang 1022
    private void drawCandleLastCircle(Canvas canvas, ICandleDataSet dataSet) {
        Paint mRenderPaint = new Paint();
//        int red = ContextCompat.getColor(getContent(), R.color.red_color);
//        int green = ContextCompat.getColor(getContext(), R.color.green_color);
//        int red = dataSet.getDecreasingColor();
//        int green = dataSet.getIncreasingColor();
        mRenderPaint.setStyle(Paint.Style.STROKE);
        mRenderPaint.setStrokeWidth(Utils.convertDpToPixel(1f));
        Paint mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(dataSet.getCircleColorHole());
        float[] mCirclesBuffer = new float[2];
        mCirclesBuffer[0] = 0;
        mCirclesBuffer[1] = 0;

        if (dataSet != null) {
            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            Entry e = dataSet.getEntryForIndex(dataSet.getEntryCount() - 1);

            if (e != null) {
                CandleEntry ce = (CandleEntry) e;
                mCirclesBuffer[0] = e.getX();
                mCirclesBuffer[1] = ce.getClose()*mAnimator.getPhaseY();

                trans.pointValuesToPixel(mCirclesBuffer);

                if (!mViewPortHandler.isInBoundsRight(mCirclesBuffer[0]))
                    return;

                if (!mViewPortHandler.isInBoundsLeft(mCirclesBuffer[0]) ||
                        !mViewPortHandler.isInBoundsY(mCirclesBuffer[1]))
                    return;

//                int mDecreasingColor = AppConfigs.getRedup() ? green : red;
//                int mIncreasingColor = AppConfigs.getRedup() ? red : green;
                int mDecreasingColor = dataSet.getDecreasingColor();
                int mIncreasingColor = dataSet.getIncreasingColor();

                mRenderPaint.setColor(ce.getOpen() > ce.getClose() ? mDecreasingColor : mIncreasingColor);
                //jiang
                canvas.drawCircle(
                        mCirclesBuffer[0] + 9.0f,
                        mCirclesBuffer[1],
                        9.0f,
                        mRenderPaint);

                canvas.drawCircle(
                        mCirclesBuffer[0] + 9.0f,
                        mCirclesBuffer[1],
                        8.0f,
                        mCirclePaintInner);
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        CandleData candleData = mChart.getCandleData();

        for (Highlight high : indices) {

            ICandleDataSet set = candleData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            CandleEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            float lowValue = e.getLow() * mAnimator.getPhaseY();
            float highValue = e.getHigh() * mAnimator.getPhaseY();
            float y = (lowValue + highValue) / 2f;

            MPPointD pix = mChart.getTransformer(set.getAxisDependency()).getPixelForValues(e.getX(), y);

            high.setDraw((float) pix.x, (float) pix.y);

            // draw the lines
//            drawHighlightLines(c, (float) pix.x, (float) pix.y, set);
            //jiang 自定义 hightvalue
            drawHighlightLines(c, (float) pix.x, (float) touchY, set);
        }
    }
}
