package com.fota.android.widget.popwin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.http.ContractAssetBean;
import com.fota.android.moudles.futures.FutureContractBean;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.RecyclerViewUtils;
import com.fota.android.widget.recyclerview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 2个window
 * Created by Administrator on 2018/4/19.
 */
public class SpinerPopWindow3 extends BasePopWindow {

    private Context context;
    private LayoutInflater inflater;
    private RecyclerView recyclerView;
    private EasyAdapter mAdapter;
    OnPopClickListener listener;
    String value;

    /**
     * 默认的popwindow
     *
     * @param context
     */
    public SpinerPopWindow3(Context context) {
        super(context);
        this.context = context;
        mAdapter = new EasyAdapter<FtKeyValue, ViewHolder>(getContext(), R.layout.item_default_pop) {

            @Override
            public void convert(ViewHolder holder, final FtKeyValue model, final int position) {
                boolean isCheck = model.getValue().equals(value);
                TextView tv = holder.getView(R.id.item_default_pop_tv);
                tv.setText(model.getKey());
                setItemTvStyle(isCheck, tv);
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
        if (AppConfigs.isWhiteTheme()) {
            tv.setTextColor(isCheck ? CommonUtils.getColor(getContext(), R.color.white) : Pub.getColor(getContext(), R.attr.font_color3));
            tv.setBackgroundResource(isCheck ? R.drawable.ft_corner_toast_bg : R.drawable.ft2_corner_select);
        } else {
            tv.setTextColor(isCheck ? Pub.getColor(getContext(), R.attr.bg_color) : Pub.getColor(getContext(), R.attr.font_color3));
            tv.setBackgroundResource(isCheck ? R.drawable.ft_corner_font_color : R.drawable.ft2_corner_select);
        }
//        tv.setTextColor(isCheck ? CommonUtils.getColor(getContext(), R.color.white) : Pub.getColor(getContext(), R.attr.font_color3));
//
//
//        if (isCheck) {
//            if (AppConfigs.isWhiteTheme()) {
//                //白色
//                tv.setTextColor(Pub.getColor(getContext(), R.attr.font_color));
//                UIUtil.setRoundCornerBg(tv, Pub.getColor(getContext(), R.attr.bg_color2));
//                //tv.setBackgroundResource(R.drawable.ft_corner_toast_bg);
//            } else {
//                tv.setTextColor(Pub.getColor(getContext(), R.attr.bg_color));
//                tv.setBackgroundResource(R.drawable.ft_corner_font_color);
//            }
//
//        } else {
//            tv.setTextColor(Pub.getColor(getContext(), R.attr.font_color3));
//            tv.setBackgroundResource(R.drawable.ft2_corner_select);
//        }

    }


    @SuppressLint("WrongConstant")
    private void init() {
        View view = inflater.inflate(R.layout.content_pop_window_layout, null);
        setRootView(view);
        setContentView(view);
        //设置弹出窗体需要软键盘
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//设置模式，和Activity的一样，覆盖，调整大小
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(getContext().getResources().getDrawable(
                AppConfigs.isWhiteTheme() ? R.drawable.ft3_corner_bg_color_white__line :
                        R.drawable.ft_corner_bg_color2_black));
        recyclerView = (RecyclerView) view.findViewById(R.id.listview);
        RecyclerViewUtils.initRecyclerView(recyclerView, getContext());
        recyclerView.setAdapter(mAdapter);
    }

    public Context getContext() {
        return context;
    }

    protected void setRootView(View rootView) {

    }

    public void showAsDropDown(View anchor, List<ContractAssetBean> allList, ContractAssetBean key, FutureContractBean nowValue) {
        super.showAsDropDown(anchor);
        List<FutureContractBean> beanList = new ArrayList<>();
        for (ContractAssetBean bean : allList) {
            for (FutureContractBean model : bean.getContent()) {
                beanList.add(model);
                model.setParent(bean);
            }
        }
        mAdapter.putList(beanList);
        this.value = nowValue.getValue();
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

}