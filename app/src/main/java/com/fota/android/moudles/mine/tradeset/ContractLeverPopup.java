package com.fota.android.moudles.mine.tradeset;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;

/**
 * 交易设置级别选择
 */
public class ContractLeverPopup extends PopupWindow {
    private int lever_checked;
    private Context context;
    private LayoutInflater inflater;
    private EditText recyclerView;
    //    private EasyAdapter mAdapter;
    private View bubbleView;

    OnPopClickListener listener;
    int position;


    int selectItem;

    /**
     * 默认的popwindow
     *
     * @param context
     */
    public ContractLeverPopup(final Context context, final int lever_checked, final int position_click, final OnPopClickListener onclick) {
        super(context);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.context = context;
        this.position = position;
        this.listener = onclick;
        this.lever_checked = lever_checked;

//        mAdapter = new EasyAdapter<Integer, ViewHolder>(getContext(), R.layout.item_popup_contractlever) {
//            @Override
//            public void convert(ViewHolder holder, final Integer model, final int position) {
//                holder.setText(R.id.tv_lever, model + "x");
//
//                if (lever_checked == model) {
//                    ((TextView) holder.getView(R.id.tv_lever)).setTextColor(Pub.getColor(getContext(), R.attr.main_color));
////                    holder.setTextColorRes(R.id.tv_lever, R.color.disable);
//                    holder.setVisible(R.id.imv_gou, true);
////                    holder.setBackgroundColor(R.id.rl, R.color.login_msg1_color);
////                    ((RelativeLayout)holder.getView(R.id.rl)).setBackgroundColor(Pub.getColor(getContext(), R.attr.tradeset_lever_checked_bg));
//                } else {
//                    ((TextView) holder.getView(R.id.tv_lever)).setTextColor(Pub.getColor(getContext(), R.attr.font_color4));
//                    holder.setVisible(R.id.imv_gou, false);
////                    holder.setBackgroundColor(R.id.rl, R.color.login_btn_unable);
////                    ((RelativeLayout)holder.getView(R.id.rl)).setBackgroundColor(Pub.getColor(getContext(), R.attr.bg_color2));
//                }
//                holder.setOnClickListener(R.id.rl, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        onclick.onPopClick(list.get(position), position_click);
//
//                        dismiss();
//                    }
//                });
//
//            }
//        };
        inflater = LayoutInflater.from(context);

        init();
//        mAdapter.setListData(list);
    }


    private void init() {
        View view = inflater.inflate(R.layout.content_pop_contractlever, null);
        setContentView(view);

        setFocusable(true);
        setBackgroundDrawable(null);
        recyclerView = (EditText) view.findViewById(R.id.reclclerview);
        bubbleView = view.findViewById(R.id.sure);
//        recyclerView.setAdapter(mAdapter);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        recyclerView.setText(lever_checked + "");
        bubbleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListener().onPopClick(Pub.GetInt(recyclerView.getText().toString()), position);
                dismiss();
            }
        });
    }

    public Context getContext() {
        return context;
    }

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     */
    public interface OnPopClickListener {

        void onPopClick(int lever, int position);

    }

    public OnPopClickListener getListener() {
        return listener;
    }

    public void setOnPopListener(OnPopClickListener listener) {
        this.listener = listener;
    }

    public void show(View parent) {
        show(parent, Gravity.BOTTOM, getMeasuredWidth() / 2);
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
        if (!this.isShowing()) {
            super.showAsDropDown(parent);
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
}
