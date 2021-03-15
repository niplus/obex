package com.fota.android.moudles.common;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.FootViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiang on 2018/09/06.
 */

public abstract class DealCanNullAdapter<T, VH extends RecyclerView.ViewHolder> extends EasyAdapter<T, VH> {
    private int dealColums;

    public DealCanNullAdapter(Context mContext, int resource, int dealColums) {
        super(mContext, resource);
        this.dealColums = dealColums;
    }

    @Override
    public void putList(List<T> list) {
        if(list == null || list.size() == 0) {
            list = new ArrayList();
            for (int i = 0; i < dealColums; i++) {
                list.add(null);
            }
        } else if(list.size() < dealColums) {
            for(int i = list.size();i < dealColums; i++) {
                list.add(null);
            }
        }
        super.putList(list);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount()) {
            convert((VH) holder, null, position);
        } else {
            if (getHasFoot()) {
                if (holder instanceof FootViewHolder) {

                } else {
                    T model = mListData.get(position);
                    convert((VH) holder, model, position);
                }
            } else {
                T model = mListData.get(position);
                convert((VH) holder, model, position);
            }
        }
    }

    public void setDealColums(int dealColums) {
        this.dealColums = dealColums;
    }
}
