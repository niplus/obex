package com.guoziwei.fota.chart;

/**
 * Created by jiang on 2018/09/21.
 */

public interface OnLoadEdgeListener {
    void onEdgeTouch(boolean leftEdge, boolean rightEdge);

    void onScale(boolean isMax, boolean isMin);
}