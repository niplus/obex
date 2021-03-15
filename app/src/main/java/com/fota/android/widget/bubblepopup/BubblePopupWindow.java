package com.fota.android.widget.bubblepopup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.RecyclerViewUtils;
import com.fota.android.widget.recyclerview.ViewHolder;

public class BubblePopupWindow extends PopupWindow {

    private BubbleRelativeLayout bubbleView;
    private Context context;
    LayoutInflater inflater;
    private RecyclerView recyclerView;
    private EasyAdapter mAdapter;
    OnPopClickListener listener;
    String value;

    public BubblePopupWindow(final Context context) {

        this.context = context;
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        setOutsideTouchable(false);
        setClippingEnabled(false);

        ColorDrawable dw = new ColorDrawable(0);
        setBackgroundDrawable(dw);
//        View view = inflater.inflate(R.layout.pop_window_arrow_layout, null);
//        setContentView(view);init
        mAdapter = new EasyAdapter<FtKeyValue, ViewHolder>(getContext(), R.layout.item_popup_arrow) {

            @Override
            public void convert(ViewHolder holder, final FtKeyValue model, final int position) {
                boolean isCheck = model.getValue().equals(value);
                TextView tv = holder.getView(R.id.item_default_pop_tv);
                tv.setText(model.getKey());

//                if (AppConfigs.getTheme() == 0) {
//                    tv.setBackgroundColor(context.getResources().getColor(isCheck ? R.color.pop_spinnercheck_checked_black : R.color.bg_color2_black));
//                } else {
//                    tv.setBackgroundColor(Pub.getColor(getContext(), isCheck ? R.attr.bg_color2 : R.attr.reverse_bg2));
//                }
//                tv.setTextColor(Pub.getColor(getContext(), isCheck ? R.attr.main_color : R.attr.font_color4));

                setItemTvStyle(isCheck, tv);
//                holder.getConvertView().setBackgroundColor(AppConfigs.isWhiteTheme() ? Pub.getColor(getContext(), R.attr.bg_color) :
//                        Pub.getColor(getContext(), R.attr.bg_color2));

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        value = model.getValue();
                        if (getListener() != null) {
                            listener.onPopClick(model, position);
                        }
                        getAdapter().notifyDataSetChanged();
                        dismiss();
                    }
                });
            }
        };
        inflater = LayoutInflater.from(context);
        init();
    }

    protected void setItemTvStyle(boolean isCheck, TextView tv) {
        tv.setTextColor(isCheck ? Pub.getColor(getContext(), R.attr.main_color) : Pub.getColor(getContext(), R.attr.font_color4));
//        tv.setBackgroundResource(isCheck ? (AppConfigs.isWhiteTheme() ? R.drawable.ft_corner_toast_bg : R.drawable.ft_corner_font_color)
//                : R.drawable.ft2_corner_select);
    }

//    public void setBubbleView(View view) {
//        bubbleView = inflater.
//        bubbleView.setBackgroundColor(Color.TRANSPARENT);
//        bubbleView.addView(view);
//        setContentView(R.layout.popup_check);
//    }

    public void setParam(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void show(View parent) {
        show(parent, Gravity.BOTTOM, getMeasuredWidth() / 2);
    }

    public void show(View parent, int gravity) {
        show(parent, gravity, getMeasuredWidth() / 2);
    }

    /**
     * 显示弹窗
     *
     * @param parent
     * @param gravity
     * @param bubbleOffset 气泡尖角位置偏移量。默认位于中间
     */
    public void show(View parent, int gravity, float bubbleOffset) {
        BubbleRelativeLayout.BubbleLegOrientation orientation = BubbleRelativeLayout.BubbleLegOrientation.LEFT;
        if (!this.isShowing()) {
            switch (gravity) {
                case Gravity.BOTTOM:
                    orientation = BubbleRelativeLayout.BubbleLegOrientation.TOP;
                    break;
                case Gravity.TOP:
                    orientation = BubbleRelativeLayout.BubbleLegOrientation.BOTTOM;
                    break;
                case Gravity.RIGHT:
                    orientation = BubbleRelativeLayout.BubbleLegOrientation.LEFT;
                    break;
                case Gravity.LEFT:
                    orientation = BubbleRelativeLayout.BubbleLegOrientation.RIGHT;
                    break;
                default:
                    break;
            }
            bubbleView.setBubbleParams(orientation, bubbleOffset); // 设置气泡布局方向及尖角偏移

            int[] location = new int[2];
            parent.getLocationOnScreen(location);

            switch (gravity) {
                case Gravity.BOTTOM:
//                    showAsDropDown(parent);
                    showAsDropDown(parent, (parent.getMeasuredWidth() - getMeasuredWidth()) / 2, 0);

//                    showAtLocation(parent, Gravity.NO_GRAVITY,  parent.getWidth() - 30, location[1] + parent.getHeight());
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

    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     */
    public interface OnPopClickListener {

        void onPopClick(FtKeyValue model, int position);

    }

    public OnPopClickListener getListener() {
        return listener;
    }

    public void setOnPopListener(OnPopClickListener listener) {
        this.listener = listener;
    }

    public EasyAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(EasyAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }


    public void setValue(String value) {
        this.value = value;
        if (getAdapter() != null) {
            getAdapter().notifyDataSetChanged();
        }
    }

    private void init() {
        View view = inflater.inflate(R.layout.pop_window_arrow_layout, null);
        setRootView(view);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(null);
        recyclerView = (RecyclerView) view.findViewById(R.id.reclclerview);
        bubbleView = view.findViewById(R.id.bubbleView);
        RecyclerViewUtils.initRecyclerView(recyclerView, getContext());
        recyclerView.setAdapter(mAdapter);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }

    public Context getContext() {
        return context;
    }

    protected void setRootView(View rootView) {
    }
}