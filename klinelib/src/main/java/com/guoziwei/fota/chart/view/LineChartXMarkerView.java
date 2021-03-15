package com.guoziwei.fota.chart.view;

/**
 * Created by Administrator on 2016/2/1.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.guoziwei.fota.R;
import com.guoziwei.fota.model.HisData;
import com.guoziwei.fota.util.DateUtils;

import java.util.List;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class LineChartXMarkerView extends MarkerView {

    private TextView tvContent;
    private String holdingText = "";


    public LineChartXMarkerView(Context context) {
        super(context, R.layout.view_mp_real_price_marker);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    public void setHoldingText(String holdingText) {
        this.holdingText = holdingText;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText(holdingText);
        super.refreshContent(e, highlight);
    }
}
