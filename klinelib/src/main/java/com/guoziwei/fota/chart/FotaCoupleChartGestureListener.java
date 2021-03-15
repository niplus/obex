package com.guoziwei.fota.chart;


import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.Renderer;
import com.guoziwei.fota.chart.render.AppCombinedChartRenderer;
import com.guoziwei.fota.chart.render.CandleStickChartRenderer;
import com.guoziwei.fota.chart.view.ChartInfoView;

import java.util.List;

/**
 * http://stackoverflow.com/questions/28521004/mpandroidchart-have-one-graph-mirror-the-zoom-swipes-on-a-sister-graph
 */
public class FotaCoupleChartGestureListener implements OnChartGestureListener {

    private static final String TAG = FotaCoupleChartGestureListener.class.getSimpleName();

    private BarLineChartBase srcChart;
    private Chart[] dstCharts;

    private OnAxisChangeListener listener;
    private ChartInfoView mInfoView;


    private OnLoadEdgeListener mOnLoadEdgeListener;

    private boolean isLoad = false;

    public void setOnEdgeListener(OnLoadEdgeListener onLoadEdgeListener) {
        mOnLoadEdgeListener = onLoadEdgeListener;
    }

    public FotaCoupleChartGestureListener(OnAxisChangeListener listener, ChartInfoView chartInfoView, BarLineChartBase srcChart, Chart... dstCharts) {
        this(srcChart, dstCharts);
        this.mInfoView = chartInfoView;
        this.listener = listener;
    }

    public FotaCoupleChartGestureListener(BarLineChartBase srcChart, Chart... dstCharts) {
        this.srcChart = srcChart;
        this.dstCharts = dstCharts;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
//        syncCharts();
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
//        syncCharts();
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
//        syncCharts();
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
//        syncCharts();
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        if(mInfoView != null) {
            mInfoView.setVisibility(View.GONE);
        }
        srcChart.highlightValue(null);
        for (Chart dstChart : dstCharts) {
            dstChart.highlightValue(null);
        }
//        syncCharts();
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        syncCharts();
        if (listener != null) {
            listener.onAxisChange(srcChart);
        }
        performLoadEdge();
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        if (listener != null) {
            listener.onAxisChange(srcChart);
        }
        performLoadEdge();
        if(mOnLoadEdgeListener != null) {
            //scale
            float scalceXlog = srcChart.getScaleX();
            Log.e(TAG, "scaleX" + scalceXlog);
            float minScale = srcChart.getViewPortHandler().getMinScaleX();
            float maxScale = srcChart.getViewPortHandler().getMaxScaleX();
            if(scaleX<1 && scalceXlog - minScale < 0.5) {//缩小的时候会比较接近，所以1为阀值
                mOnLoadEdgeListener.onScale(false, true);
            } else if(scaleX>1 && maxScale - scalceXlog < 10) {//放大的时候有放大因子，所以10为阀值
                mOnLoadEdgeListener.onScale(true, false);
            }
        }
        syncCharts();
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
//        Log.d(TAG, "onChartTranslate " + dX + "/" + dY + " X=" + me.getX() + "Y=" + me.getY());
//        Log.d(TAG, "getHighestVisibleX  " +srcChart.getHighestVisibleX());
        if (listener != null) {
            listener.onAxisChange(srcChart);
        }
        performLoadEdge();
        syncCharts();
    }

    private void performLoadEdge() {
        // 加载 左边或右边
        if (mOnLoadEdgeListener != null) {
            if (!isLoad && srcChart.getLowestVisibleX() <= 0) {
                isLoad = true;
                mOnLoadEdgeListener.onEdgeTouch(true, false);
            }
//            else if(!isLoad && srcChart.getHighestVisibleX() >= srcChart.getXChartMax()) {
//                isLoad = true;
//                mOnLoadEdgeListener.onEdgeTouch(false, true);
//            }
            else if(srcChart.getLowestVisibleX() > 0 && srcChart.getHighestVisibleX() < srcChart.getXChartMax()) {
                isLoad = false;
            }
        }
    }


    private void syncCharts() {
        Matrix srcMatrix;
        float[] srcVals = new float[9];
        Matrix dstMatrix;
        float[] dstVals = new float[9];
        // get src chart translation matrix:
        srcMatrix = srcChart.getViewPortHandler().getMatrixTouch();
        srcMatrix.getValues(srcVals);
        // apply X axis scaling and position to dst charts:
        for (Chart dstChart : dstCharts) {
            dstMatrix = dstChart.getViewPortHandler().getMatrixTouch();
            dstMatrix.getValues(dstVals);

            dstVals[Matrix.MSCALE_X] = srcVals[Matrix.MSCALE_X];
            dstVals[Matrix.MSKEW_X] = srcVals[Matrix.MSKEW_X];
            dstVals[Matrix.MTRANS_X] = srcVals[Matrix.MTRANS_X];
            dstVals[Matrix.MSKEW_Y] = srcVals[Matrix.MSKEW_Y];
            dstVals[Matrix.MSCALE_Y] = srcVals[Matrix.MSCALE_Y];
            dstVals[Matrix.MTRANS_Y] = srcVals[Matrix.MTRANS_Y];
            dstVals[Matrix.MPERSP_0] = srcVals[Matrix.MPERSP_0];
            dstVals[Matrix.MPERSP_1] = srcVals[Matrix.MPERSP_1];
            dstVals[Matrix.MPERSP_2] = srcVals[Matrix.MPERSP_2];

            dstMatrix.setValues(dstVals);
            dstChart.getViewPortHandler().refresh(dstMatrix, dstChart, true);
        }
    }

    public interface OnAxisChangeListener {
        void onAxisChange(BarLineChartBase chart);
    }

}