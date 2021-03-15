package com.fota.android.widget.popwin;

import android.content.Context;
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

/**
 * 带箭头样式的选择弹框
 */
public class ArrowSpinerPopWindow extends PopupWindow {

    private Context context;
    private LayoutInflater inflater;
    private RecyclerView recyclerView;
    private EasyAdapter mAdapter;
    SpinerPopWindow.OnPopClickListener listener;
    String value;
    //FtKeyValue selectItem;

    /**
     * 默认的popwindow
     *
     * @param context
     */
    public ArrowSpinerPopWindow(Context context) {
        super(context);
        this.context = context;
        mAdapter = new EasyAdapter<FtKeyValue, ViewHolder>(getContext(), R.layout.item_popup_arrow) {

            @Override
            public void convert(ViewHolder holder, final FtKeyValue model, final int position) {
                boolean isCheck = model.getValue().equals(value);
                TextView tv = holder.getView(R.id.item_default_pop_tv);
                tv.setText(model.getKey());

                //tv.setTextColor(Pub.getColor(getContext(), isCheck ? R.attr.bg_color2 : R.attr.unable));
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
        tv.setTextColor(isCheck ? Pub.getColor(getContext(), R.attr.font_color) : Pub.getColor(getContext(), R.attr.font_color2));
//        tv.setBackgroundResource(isCheck ? (AppConfigs.isWhiteTheme() ? R.drawable.ft_corner_toast_bg : R.drawable.ft_corner_font_color)
//                : R.drawable.ft2_corner_select);
    }


    private void init() {
        View view = inflater.inflate(R.layout.pop_window_arrow_layout, null);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        setRootView(view);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(null);
        recyclerView = (RecyclerView) view.findViewById(R.id.reclclerview);
        RecyclerViewUtils.initRecyclerView(recyclerView, getContext());
        recyclerView.setAdapter(mAdapter);
    }

    public Context getContext() {
        return context;
    }

    protected void setRootView(View rootView) {
    }

    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     */
    public interface OnPopClickListener {

        void onPopClick(FtKeyValue model, int position);

    }

    public SpinerPopWindow.OnPopClickListener getListener() {
        return listener;
    }

    public void setOnPopListener(SpinerPopWindow.OnPopClickListener listener) {
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
}
