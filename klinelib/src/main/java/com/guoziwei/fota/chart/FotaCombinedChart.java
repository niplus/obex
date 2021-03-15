package com.guoziwei.fota.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonPub;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.guoziwei.fota.R;
import com.guoziwei.fota.chart.render.AppCombinedChartRenderer;
import com.guoziwei.fota.chart.view.LineChartXMarkerView;
import com.guoziwei.fota.chart.view.LineChartYMarkerView;
import com.guoziwei.fota.chart.view.fota.FotaChartMarkerView;
import com.guoziwei.fota.chart.view.fota.FotaChartYFutureMarkerView;

import java.util.List;

import static com.github.mikephil.charting.utils.Utils.convertDpToPixel;

/**
 * Created by JIANG on 2018/08/22.
 */

public class FotaCombinedChart extends AppCombinedChart {
    protected boolean isDrawLastCircle;
    private boolean isSpotMaxThanFuture = true;
    private float yMin;
    private float yMax;
    //1 index 2 future 3 usdt
    private int type;

//    private boolean isMarkerOutofBound;

    public int getmDigits() {
        return mDigits;
    }

    public void setmDigits(int mDigits) {
        this.mDigits = mDigits;
    }

    private int mDigits = 2;

    public void setDrawLastCircle(boolean drawLastCircle) {
        this.isDrawLastCircle = drawLastCircle;
        if (mRenderer != null) {
            ((AppCombinedChartRenderer) mRenderer).setDrawLastCircle(drawLastCircle);
        }
    }

    public void setSpotMaxThanFuture(boolean spotMaxThanFuture) {
        isSpotMaxThanFuture = spotMaxThanFuture;
        if(isSpotMaxThanFuture) {
            if(mLastYSpotMarker != null) {
                mLastYSpotMarker.setTop(true);
            }
            if(mLastYFutureMarker != null) {
                mLastYFutureMarker.setTop(false);
            }
        } else {
            if(mLastYSpotMarker != null) {
                mLastYSpotMarker.setTop(false);
            }
            if(mLastYFutureMarker != null) {
                mLastYFutureMarker.setTop(true);
            }
        }
    }

    private IMarker mXMarker;

    private FotaChartMarkerView mLastYSpotMarker;
    private FotaChartMarkerView mLastYFutureMarker;

    private float mYCenter;

    public FotaCombinedChart(Context context) {
        this(context, null);
    }

    public FotaCombinedChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FotaCombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new AppCombinedChartRenderer(this, mAnimator, mViewPortHandler);
    }

    public void setXMarker(IMarker marker) {
        mXMarker = marker;
    }

    public void setmLastYSpotMarker(FotaChartMarkerView mLastYSpotMarker) {
        this.mLastYSpotMarker = mLastYSpotMarker;
    }

    public void setmLastYFutureMarker(FotaChartMarkerView mLastYFutureMarker) {
        this.mLastYFutureMarker = mLastYFutureMarker;
    }

    @Override
    public void setData(CombinedData data) {
        try {
            super.setData(data);
        } catch (ClassCastException e) {
            // ignore
        }
        ((AppCombinedChartRenderer) mRenderer).setmDigits(mDigits);
        ((AppCombinedChartRenderer) mRenderer).createRenderers();
        mRenderer.initBuffers();
    }

    /**
     * @param canvas jiang add comment drawLineLastMarker
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (mData == null)
            return;

        autoScale();
        mAutoScaleMinMaxEnabled = false;
        //jiang 最右侧才展示marker
        float highVisibleX = Math.round(getHighestVisibleX()*10)*0.1f;
        yMin = mData.getYMin(YAxis.AxisDependency.LEFT);
        yMax = mData.getYMax(YAxis.AxisDependency.LEFT);
        setMarkerBoth(false);
        if(highVisibleX >= getXChartMax()) {
            if (getCandleData() != null) {
                drawCandleLastMarker(canvas);
            } else {
                drawLineLastMarker(canvas);
            }
            setMarkerBoth(true);
        } else {
            setMarkerBoth(false);
        }
        super.onDraw(canvas);
        //真正的绘制
        if((mLastYFutureMarker != null && mLastYFutureMarker.isNeedDraw()) ||
                (mLastYSpotMarker != null && mLastYSpotMarker.isNeedDraw())) {
            if (getCandleData() != null) {
                drawCandleLastMarker(canvas);
            } else {
                drawLineLastMarker(canvas);
            }
        }
        setMarkerBoth(false);
    }

    private void setMarkerBoth(boolean isDraw) {
        if(mLastYFutureMarker != null) {
            mLastYFutureMarker.setNeedDraw(isDraw);
        }
        if(mLastYSpotMarker != null) {
            mLastYSpotMarker.setNeedDraw(isDraw);
        }
    }

    protected void drawCandleLastMarker(Canvas canvas) {
        if (mLastYFutureMarker == null
                || mLastYSpotMarker == null || getCandleData() == null) {
            return;
        }

        //jiang k线 future
        IDataSet dataSet;
        type = 3;
        if (getLineData() != null) {
            if (getLineData().getDataSetCount() >= 2) {//
                //jiang 现货指数 最新价
                type = 2;
                dataSet = getLineData().getDataSetByIndex(1);
                calSpotIsMaxThanFuture(getCandleData().getDataSetByIndex(0), getLineData().getDataSetByIndex(1), true);
                drawLastMarkerBuffer(canvas, mLastYSpotMarker, dataSet);
            }
        }

        dataSet = getCandleData().getDataSetByIndex(0);
//        drawCandleLastCircle(canvas, dataSet);
        drawLastMarkerBuffer(canvas, mLastYFutureMarker, dataSet);
    }

    private void calSpotIsMaxThanFuture(IDataSet futureData, IDataSet spotData, boolean isFutureCandle) {
//        Entry eFuture = futureData.getEntryForIndex(futureData.getEntryCount() - 1);
//        Entry eSpot = spotData.getEntryForIndex(spotData.getEntryCount() - 1);
//        if(isFutureCandle) {
//            if (eFuture != null) {
//                CandleEntry ce = (CandleEntry) eFuture;
//                lastFuturePrice = ce.getClose() * mAnimator.getPhaseY();
//            }
//            if(eSpot != null) {
//                lastSpotPrice = eSpot.getY()*mAnimator.getPhaseY();
//            }
//        } else {
//            if(eFuture != null) {
//                lastFuturePrice = eFuture.getY()*mAnimator.getPhaseY();
//            }
//            if(eSpot != null) {
//                lastSpotPrice = eSpot.getY()*mAnimator.getPhaseY();
//            }
//        }
//        isSpotMaxThanFuture = lastSpotPrice > lastFuturePrice;
    }

    protected void drawLineLastMarker(Canvas canvas) {
        if (mLastYFutureMarker == null
                || mLastYSpotMarker == null || getLineData() == null) {
            return;
        }

        //jiang 合约最新价 or 一根线的情况 index or usdt
        IDataSet dataSet = getLineData().getDataSetByIndex(0);
        String label = dataSet.getLabel();
        type = 3;
        if (getLineData().getDataSetCount() == 2) {//有额外数据，合约的分时
            type = 2;
            //jiang 现货指数 最新价
            dataSet = getLineData().getDataSetByIndex(1);
            calSpotIsMaxThanFuture(getLineData().getDataSetByIndex(0), getLineData().getDataSetByIndex(1), false);
            drawLastMarkerBuffer(canvas, mLastYSpotMarker, dataSet);
        } else if (label.contains("spot")) {
            type = 1;
            //jiang 只有指数的情况，需要把标签置为false
            isSpotMaxThanFuture = false;
            drawLastMarkerBuffer(canvas, mLastYSpotMarker, dataSet);
            return;
        }

        dataSet = getLineData().getDataSetByIndex(0);
        drawLastMarkerBuffer(canvas, mLastYFutureMarker, dataSet);
    }

    private void drawLastMarkerBuffer(Canvas canvas, IMarker markerView, IDataSet dataSet) {
        float[] mBuffer = new float[2];

        if (dataSet != null) {
            FotaChartMarkerView castMarker = (FotaChartMarkerView) markerView;
            Transformer trans = getTransformer(dataSet.getAxisDependency());
            Entry e = dataSet.getEntryForIndex(dataSet.getEntryCount() - 1);
            if (e != null) {
                mBuffer[0] = e.getX();
                if(dataSet instanceof LineDataSet) {
                    mBuffer[1] = e.getY()*mAnimator.getPhaseY();
                } else if(dataSet instanceof CandleDataSet) {
                    CandleEntry ce = (CandleEntry)e;
                    mBuffer[1] = ce.getClose()*mAnimator.getPhaseY();
                }

                castMarker.setyValue(mBuffer[1]);
                trans.pointValuesToPixel(mBuffer);
                //jiang
                markerView.refreshContent(e, null);
                if (getCandleData() != null && markerView instanceof FotaChartYFutureMarkerView) {
                    int red = ContextCompat.getColor(getContext(), R.color.red_color);
                    int green = ContextCompat.getColor(getContext(), R.color.green_color);
                    int mDecreasingColor = AppConfigs.getRedup() ? green : red;
                    int mIncreasingColor = AppConfigs.getRedup() ? red : green;
                    CandleEntry ce = (CandleEntry)e;

                    ((FotaChartYFutureMarkerView) markerView).setBaseColorForCandel(ce.getOpen() > ce.getClose() ? mDecreasingColor : mIncreasingColor);
                }
                //jiang add offset right
//                if(finalY + castMarker.getMeasuredHeight()/2 >= mViewPortHandler.contentBottom()) {
//                    finalY -= castMarker.getMeasuredHeight();
//                } else {
//                finalY -= castMarker.getMeasuredHeight()/2;
                    calculateFinalY(mBuffer[1], castMarker);
//                }

                //jiang fix
                //如果调整了，就先不绘制
                calulateMaxMinForMarker(trans, castMarker);

                float finalX = mBuffer[0];
//                markerView.draw(canvas, mViewPortHandler.contentRight() - 4.0f, finalY);
                if(castMarker.isNeedDraw()) {
                    castMarker.draw(canvas, finalX, castMarker.getFinalY());
                }
            }
        }
    }

    /**
     * 计算 上下区间
     * @param trans
     * @param castMarker
     * castMarker.finalY -- 当前要描绘的marker 最后的y位置
     * @return 调整区间 返回false，表示不绘制了
     */
    private void calulateMaxMinForMarker(Transformer trans, FotaChartMarkerView castMarker) {
        float finalY = castMarker.getFinalY();
        float finalYtempBottom = finalY + castMarker.getMeasuredHeight() + Utils.convertDpToPixel(3f);//yBottom
        float finalYtempTop = finalY - Utils.convertDpToPixel(3f);//yTop
        if(mViewPortHandler.isInBoundsBottom(finalYtempBottom) && mViewPortHandler.isInBoundsTop(finalYtempTop)) {

        } else {
            if (!mViewPortHandler.isInBoundsBottom(finalYtempBottom)) {
                float[] mBufferTmep = new float[2];
                mBufferTmep[1] = finalYtempBottom + Utils.convertDpToPixel(6);
                trans.pixelsToValue(mBufferTmep);
                yMin = mBufferTmep[1];
                addtionAutoScale(mBufferTmep[1], yMax);
                if(castMarker instanceof FotaChartYFutureMarkerView && type == 2) {
                    float[] temp = new float[2];
                    temp[1] = mLastYSpotMarker.getyValue();
                    trans.pointValuesToPixel(temp);
                    calculateFinalY(temp[1], mLastYSpotMarker);
                    calulateMaxMinForMarker(trans, mLastYSpotMarker);
                }
            } else if (!mViewPortHandler.isInBoundsTop(finalYtempTop)) {
                float[] mBufferTmep = new float[2];
                mBufferTmep[1] = finalYtempTop - Utils.convertDpToPixel(6);
                trans.pixelsToValue(mBufferTmep);
                yMax = mBufferTmep[1];
                addtionAutoScale(yMin, mBufferTmep[1]);
                if(castMarker instanceof FotaChartYFutureMarkerView && type == 2) {
                    float[] temp = new float[2];
                    temp[1] = mLastYSpotMarker.getyValue();
                    trans.pointValuesToPixel(temp);
                    calculateFinalY(temp[1], mLastYSpotMarker);
                    calulateMaxMinForMarker(trans, mLastYSpotMarker);
                }
            }
        }
    }

    private void calculateFinalY(float defaultValue, FotaChartMarkerView castMarker) {
        float result = defaultValue;
        if (castMarker instanceof FotaChartYFutureMarkerView) {//future
            if(!isSpotMaxThanFuture) {
                result -= (castMarker.getMeasuredHeight() + Utils.convertDpToPixel(1f));
//                ((FotaChartMarkerView) castMarker).setTop(true);
            } else {
//                ((FotaChartMarkerView) castMarker).setTop(false);
            }
        } else {//spot
            if(isSpotMaxThanFuture) {
                result -= (castMarker.getMeasuredHeight() + Utils.convertDpToPixel(1f));
//                ((FotaChartMarkerView) castMarker).setTop(true);
            } else {
//                ((FotaChartMarkerView) castMarker).setTop(false);
            }
        }
        castMarker.setFinalY(result);
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        if (mXMarker == null)
            return;

        List<LimitLine> limitLines = getAxisLeft().getLimitLines();
        if (limitLines == null || limitLines.size() <= 0)
            return;
    }

    protected void drawDescription(Canvas c) {

        // check if description should be drawn
        if (mDescription != null && mDescription.isEnabled()) {

            MPPointF position = mDescription.getPosition();

            mDescPaint.setTypeface(mDescription.getTypeface());
            mDescPaint.setTextSize(mDescription.getTextSize());
            mDescPaint.setColor(mDescription.getTextColor());
            mDescPaint.setTextAlign(mDescription.getTextAlign());

            float x, y;

            // if no position specified, draw on default position
            if (position == null) {
                x = getWidth() - mViewPortHandler.offsetRight() - mDescription.getXOffset();
                y = mDescription.getTextSize() + mViewPortHandler.offsetTop() + mDescription.getYOffset();
            } else {
                x = position.x;
                y = position.y;
            }

            c.drawText(mDescription.getText(), x, y, mDescPaint);
        }
    }


    /**
     * 重写这两个方法，为了让开盘价和涨跌幅剧中显示
     * Performs auto scaling of the axis by recalculating the minimum and maximum y-values based on the entries currently in view.
     */
    //jiang 0614
    protected void autoScale() {
        final float fromX = getLowestVisibleX();
//        final float fromX = mData.getXMax() - 150;
        final float toX = getHighestVisibleX();

//        Log.e("kline chart", fromX + "-" + toX);
        mData.calcMinMaxY(fromX, toX);

        mXAxis.calculate(mData.getXMin(), mData.getXMax());

        // calculate axis range (min / max) according to provided data

        if (mAxisLeft.isEnabled()) {
            if (mYCenter == 0) {
                mAxisLeft.calculate(mData.getYMin(YAxis.AxisDependency.LEFT),
                        mData.getYMax(YAxis.AxisDependency.LEFT));
            } else {
                float yMin = mData.getYMin(YAxis.AxisDependency.LEFT);
                float yMax = mData.getYMax(YAxis.AxisDependency.LEFT);
                float interval = (float) Math.max(Math.abs(mYCenter - yMax), Math.abs(mYCenter - yMin));
                yMax = (float) Math.max(yMax, (mYCenter + interval));
                yMin = (float) Math.min(yMin, (mYCenter - interval));
                mAxisLeft.calculate(yMin, yMax);
            }
        }

        if (mAxisRight.isEnabled()) {
            if (mYCenter == 0) {
                mAxisRight.calculate(mData.getYMin(YAxis.AxisDependency.RIGHT),
                        mData.getYMax(YAxis.AxisDependency.RIGHT));
            } else {
                float yMin = mData.getYMin(YAxis.AxisDependency.RIGHT);
                float yMax = mData.getYMax(YAxis.AxisDependency.RIGHT);
                float interval = (float) Math.max(Math.abs(mYCenter - yMax), Math.abs(mYCenter - yMin));
                yMax = (float) Math.max(yMax, (mYCenter + interval));
                yMin = (float) Math.min(yMin, (mYCenter - interval));
                mAxisRight.calculate(yMin, yMax);
            }
        }

        calculateOffsets();
    }

    //jiang 1025 add 额外的计算scale值，不用原来的autoScale
    //主要用于绘制右侧气泡，需要上下调整偏移的值
    protected void addtionAutoScale(float yMin, float yMax) {
        if (mAxisLeft.isEnabled()) {
            mAxisLeft.calculate(yMin, yMax);
        }
        if (mAxisRight.isEnabled()) {
            mAxisRight.calculate(yMin, yMax);
        }

        calculateOffsets();
    }

    /**
     * 重写这两个方法，为了让开盘价和涨跌幅剧中显示
     */
    @Override
    protected void calcMinMax() {

        mXAxis.calculate(mData.getXMin(), mData.getXMax());

        if (mYCenter == 0) {
            // calculate axis range (min / max) according to provided data
            mAxisLeft.calculate(mData.getYMin(YAxis.AxisDependency.LEFT), mData.getYMax(YAxis.AxisDependency.LEFT));
            mAxisRight.calculate(mData.getYMin(YAxis.AxisDependency.RIGHT), mData.getYMax(YAxis.AxisDependency
                    .RIGHT));
        } else {
            float yLMin = mData.getYMin(YAxis.AxisDependency.LEFT);
            float yLMax = mData.getYMax(YAxis.AxisDependency.LEFT);
            float interval = (float) Math.max(Math.abs(mYCenter - yLMax), Math.abs(mYCenter - yLMin));
            yLMax = (float) Math.max(yLMax, (mYCenter + interval));
            yLMin = (float) Math.min(yLMin, (mYCenter - interval));
            mAxisLeft.calculate(yLMin, yLMax);

            float yRMin = mData.getYMin(YAxis.AxisDependency.RIGHT);
            float yRMax = mData.getYMax(YAxis.AxisDependency.RIGHT);
            float rinterval = (float) Math.max(Math.abs(mYCenter - yRMax), Math.abs(mYCenter - yRMin));
            yRMax = (float) Math.max(yRMax, (mYCenter + rinterval));
            yRMin = (float) Math.min(yRMin, (mYCenter - rinterval));
            mAxisRight.calculate(yRMin, yRMax);
        }
    }

    /**
     * 设置图表中Y居中的值
     */
    public void setYCenter(float YCenter) {
        mYCenter = YCenter;
    }
}
