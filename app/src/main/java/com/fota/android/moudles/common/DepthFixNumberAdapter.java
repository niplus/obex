package com.fota.android.moudles.common;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.FootViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiang on 2018/5/4.
 */

public abstract class DepthFixNumberAdapter<T, VH extends RecyclerView.ViewHolder> extends EasyAdapter<T, VH> {
    private int depthFixColoums;

    public DepthFixNumberAdapter(Context mContext, int resource, int depthFixColoums) {
        super(mContext, resource);
        this.depthFixColoums = depthFixColoums;
    }

    @Override
    public void putList(List<T> list) {
        if(list == null || list.size() == 0) {
            list = new ArrayList();
            for (int i = 0; i < depthFixColoums; i++) {
                list.add(null);
            }
        }
        super.putList(list);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount()) {
            //jiang 少于规定数，填充null
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

}
