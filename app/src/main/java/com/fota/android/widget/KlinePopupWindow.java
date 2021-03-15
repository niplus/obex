package com.fota.android.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;

import java.util.List;

/**
 * Created by jiang on 2018/08/06.
 */

public class KlinePopupWindow extends PopupWindow {
    private Activity context;
    private List<String> datas;
    private int index;
    private ListView listView;
    private PeriodAdapter adapter;
    private KlinePeriodInterface periodInterface;

    public void setIndex(int index) {
        this.index = index;
    }

    public KlinePopupWindow(Activity context, List<String> datas) {
        this.context = context;
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        setOutsideTouchable(false);
        setClippingEnabled(false);

        ColorDrawable dw = new ColorDrawable(0);
        setBackgroundDrawable(dw);
        this.datas = datas;
    }

    public void setPopView(View view, KlinePeriodInterface periodInterface) {
        listView = view.findViewById(R.id.list_period);
        initListView();
        this.periodInterface = periodInterface;
        setContentView(view);
    }

    private void initListView() {
        adapter = new PeriodAdapter();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void show(View parent) {
        show(parent, Gravity.TOP, getMeasuredWidth() / 2);
    }

    public void show(View parent, int gravity) {
        show(parent, gravity, getMeasuredWidth() / 2);
    }

    @Override
    public void dismiss() {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 1.0f;
        context.getWindow().setAttributes(lp);
        super.dismiss();
        periodInterface.dimissNotice();
    }

    /**
     * 显示弹窗
     *
     * @param parent
     * @param gravity
     * @param bubbleOffset 气泡尖角位置偏移量。默认位于中间
     */
    public void show(View parent, int gravity, float bubbleOffset) {
        if (!this.isShowing()) {
            WindowManager.LayoutParams lp = context.getWindow().getAttributes();
            lp.alpha = 0.7f;
            context.getWindow().setAttributes(lp);

            int[] location = new int[2];
            parent.getLocationOnScreen(location);

            switch (gravity) {
                case Gravity.BOTTOM:
                    int x = location[0] - (getMeasuredWidth() - parent.getWidth()) / 2;
                    int y = location[1] + parent.getHeight() + 8;
                    showAtLocation(parent, Gravity.NO_GRAVITY, x, y);
                    break;
                case Gravity.TOP:
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] - getMeasureHeight());
                    break;
                case Gravity.RIGHT:
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + parent.getWidth(), location[1] - (parent.getHeight() / 2));
                    break;
                case Gravity.LEFT:
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0] - getMeasuredWidth(), location[1] - (parent.getHeight() / 2));
                    break;
                default:
                    break;
            }
        } else {
            this.dismiss();
        }
    }

    /**
     * 测量高度
     *
     * @return
     */
    public int getMeasureHeight() {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popHeight = getContentView().getMeasuredHeight();
        return popHeight;
    }

    /**
     * 测量宽度
     *
     * @return
     */
    public int getMeasuredWidth() {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popWidth = getContentView().getMeasuredWidth();
        return popWidth;
    }

    class PeriodAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public PeriodAdapter() {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_simple_period, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.text1);
            textView.setText((String) getItem(position));
            if (position == index) {
                textView.setTextColor(Pub.getColor(context, R.attr.kline_pop_check));
            } else {
                textView.setTextColor(Pub.getColor(context, R.attr.kline_pop_uncheck));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    periodInterface.periodClick(position);
                    dismiss();
                }
            });
            return convertView;
        }
    }

    public interface KlinePeriodInterface {
        void periodClick(int index);

        void dimissNotice();
    }
}
