package com.fota.android.widget.popwin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.http.ContractAssetBean;
import com.fota.android.moudles.futures.FutureContractBean;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.RecyclerViewUtils;
import com.fota.android.widget.recyclerview.ViewHolder;

import java.util.List;

/**
 * 2个window
 * Created by Administrator on 2018/4/19.
 */
public class SpinerPopWindow2 extends BasePopWindow {

    private EasyAdapter<ContractAssetBean, ViewHolder> mFirstAdapter;
    private Context context;
    private LayoutInflater inflater;
    private RecyclerView recyclerView;
    private EasyAdapter mAdapter;

    OnPopClickListener listener;

    private RecyclerView recyclerViewFirst;


    /**
     * 中间过渡值  只是告诉用户点了哪个
     */
    private ContractAssetBean middleKey;

    private FutureContractBean nowValue;


    public void showAsDropDown(View anchor, List<ContractAssetBean> allList
            , ContractAssetBean key, FutureContractBean nowValue) {
        super.showAsDropDown(anchor);
        mFirstAdapter.putList(allList);
        if (key != null) {
            middleKey = key;
            mAdapter.putList(key.getContent());
        }
        this.nowValue = nowValue;
    }

    /**
     * 默认的popwindow
     *
     * @param context
     */
    public SpinerPopWindow2(Context context) {
        super(context);

        this.context = context;

        mFirstAdapter = new EasyAdapter<ContractAssetBean, ViewHolder>(getContext(), R.layout.item_default_pop2_first) {

            @Override
            public void convert(ViewHolder holder, final ContractAssetBean model, final int position) {
                boolean isCheck = model == middleKey;
                TextView tv = holder.getView(R.id.item_default_pop2_tv);
                tv.setText(model.getName());
                int color =
                        Pub.getColor(getContext(), isCheck ? R.attr.main_color : R.attr.font_color3);
                tv.setTextColor(color);
                holder.setVisible2(R.id.item_default_pop2_view, isCheck);
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        middleKey = model;
                        notifyDataSetChanged();
                        getAdapter().putList(model.getContent());
                    }
                });
            }
        };

        mAdapter = new EasyAdapter<FutureContractBean, ViewHolder>(getContext(), R.layout.item_default_pop) {

            @Override
            public void convert(ViewHolder holder, final FutureContractBean model, final int position) {
                boolean isCheck = model == nowValue;
                TextView tv = holder.getView(R.id.item_default_pop_tv);
                tv.setText(model.getKey());
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        nowValue = model;
                        if (getListener() != null) {
                            listener.onPopClick(middleKey, model, position);
                        }
                        getAdapter().notifyDataSetChanged();
                        dismiss();
                    }
                });
                if (AppConfigs.isWhiteTheme()) {
                    tv.setTextColor(isCheck ? CommonUtils.getColor(getContext(), R.color.white) : Pub.getColor(getContext(), R.attr.font_color3));
                    tv.setBackgroundResource(isCheck ? R.drawable.ft_corner_toast_bg : R.drawable.ft2_corner_select);
                } else {
                    tv.setTextColor(isCheck ? Pub.getColor(getContext(), R.attr.bg_color) : Pub.getColor(getContext(), R.attr.font_color3));
                    tv.setBackgroundResource(isCheck ? R.drawable.ft_corner_font_color : R.drawable.ft2_corner_select);
                }
            }
        };

        inflater = LayoutInflater.from(context);
        init();
    }

    @SuppressLint("WrongConstant")
    private void init() {
        View view = inflater.inflate(R.layout.content_pop_window_layout2, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setSoftInputMode(INPUT_METHOD_NEEDED);
        //设置模式，和Activity的一样，覆盖，调整大小
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setFocusable(true);
        setBackgroundDrawable(getContext().getResources().getDrawable(
                AppConfigs.isWhiteTheme() ? R.drawable.ft3_corner_bg_color_white__line :
                        R.drawable.ft_corner_bg_color2_black));
        recyclerViewFirst = (RecyclerView) view.findViewById(R.id.first_listview);
        RecyclerViewUtils.initRecyclerView(recyclerViewFirst, getContext());
        recyclerViewFirst.setAdapter(mFirstAdapter);
        recyclerView = (RecyclerView) view.findViewById(R.id.listview);
        RecyclerViewUtils.initRecyclerView(recyclerView, getContext());
        recyclerView.setAdapter(mAdapter);
    }

    public Context getContext() {
        return context;
    }

    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     */
    public interface OnPopClickListener {

        void onPopClick(ContractAssetBean parent, FutureContractBean model, int position);

    }

    public OnPopClickListener getListener() {
        return listener;
    }

    public void setOnPopListener(OnPopClickListener listener) {
        this.listener = listener;
    }


    public EasyAdapter<ContractAssetBean, ViewHolder> getFirstAdapter() {
        return mFirstAdapter;
    }

    public void setFirstAdapter(EasyAdapter<ContractAssetBean, ViewHolder> mFirstAdapter) {
        this.mFirstAdapter = mFirstAdapter;
    }

    public EasyAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(EasyAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

}