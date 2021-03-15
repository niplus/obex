package com.fota.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.fota.android.R;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiang on 2018/4/28.
 */

public class DepthFixNumberLinearView extends LinearLayout {
    private EasyAdapter adapter;
    private List<ViewHolder> holderLists = new ArrayList<>();
    //defaut 5
    private int coloums = 5;

    public DepthFixNumberLinearView(Context context) {
        super(context);
    }

    public DepthFixNumberLinearView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.depth);
            coloums = array.getInt(R.styleable.depth_numbers, 5);
            array.recycle();
        }
    }

    public void setDepthAdapter(EasyAdapter adapter) {
        this.adapter = adapter;
        for(int i=0;i<coloums;i++) {
            ViewHolder holder = adapter.onCreateDataViewHolder(this, 0);
            holderLists.add(holder);
            View each = holder.getConvertView();
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
            each.setLayoutParams(lp);
            this.addView(holder.getConvertView());
        }
        this.setOrientation(VERTICAL);
    }

    public EasyAdapter getAdapter() {
        return adapter;
    }

    public void onRefreshView(boolean isReverse) {
        if(isReverse) {
            for (int i = coloums-1; i >= 0; i--) {
                adapter.onBindViewHolder(holderLists.get(i), coloums-1-i);
            }
        } else {
            for (int i = 0; i < coloums; i++) {
                adapter.onBindViewHolder(holderLists.get(i), i);
            }
        }
    }

    public int getColoums() {
        return coloums;
    }
}
