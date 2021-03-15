package com.guoziwei.fota.chart.view.fota;

/**
 * Created by Administrator on 2016/2/1.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.guoziwei.fota.R;
import com.guoziwei.fota.util.DoubleUtil;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class FotaChartYSpotMarkerView extends FotaChartMarkerView {

    public FotaChartYSpotMarkerView(Context context, int digits) {
        super(context, digits, R.layout.view_fota_last_spot_marker);
        triagleView.setmBorderColor(getResources().getColor(R.color.spot_marker_text_color));
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //必须refresh - measure - layout 否则动态设置LayoutParams等代码不生效
        super.refreshContent(e, highlight);
        float value = e.getY();
        tvContent.setText(DoubleUtil.getStringByDigits(value, digits));
        tvContent.setTextColor(getResources().getColor(R.color.spot_marker_text_color));
    }
}
