package com.fota.android.widget.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights reserved by Author<br>
 * Author: Dong YuHui<br>
 * Email: <a href="mailto:dyh920827@hotmail.com">dyh920827@hotmail.com</a><br>
 * Blog: <a href="http://www.kyletung.com">www.kyletung.com</a>
 * Create Time: 2016/1/12<br>
 * 自定义加载更多的 Recycler Adapter，根据是否有脚布局来呈现最后的 ProgressBar
 *
 * @author fjw
 */
public abstract class EasyAdapter<T, VH extends RecyclerView.ViewHolder> extends
        RecyclerView.Adapter {

    private OnItemClickListener mOnItemClickListener;

    @Deprecated
    /**
     * 这个方法  没有适配XR 谨慎使用
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    protected static final int TYPE_FOOT = 1;
    protected static final int TYPE_DATA = 0;
    protected int resource;
    protected Context mContext;
    public List<T> mListData;

    public EasyAdapter(Context mContext, int resource) {
        this.mContext = mContext;
        this.resource = resource;
        mListData = new ArrayList<>();
    }

    protected int getItemViewResourceByType(int itemType) {
        return 0;
    }

    /**
     * 生成 View
     *
     * @param parent ViewGroup
     * @return 返回 View
     */
    protected View createView(ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(resource, parent, false);
    }

    public List<T> getListData() {
        return mListData;
    }

    public void setListData(List<T> listData) {
        mListData = listData;
    }

    protected View createView(ViewGroup parent, @LayoutRes int id) {
        return LayoutInflater.from(mContext).inflate(id, parent, false);
    }

    /**
     * 替换列表内容
     *
     * @param list 列表内容
     */
    public void putList(List<T> list) {
        mListData.clear();
        if (list == null) {
            notifyDataSetChanged();
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            mListData.add(list.get(i));
        }
        notifyDataSetChanged();
    }

    public ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        int tempResouceId = getItemViewResourceByType(viewType);
        if (tempResouceId > 0) {
            view = createView(parent, tempResouceId);
        } else {
            view = createView(parent);
        }
        ViewHolder mHold = new ViewHolder(view, mContext, parent);
        setListener(parent, mHold, viewType);
        return mHold;
    }

    /**
     * 添加列表内容
     *
     * @param list 列表内容
     */
    public void addList(List<T> list) {
        if (list == null) {
            return;
        }
        mListData.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 添加列表内容
     */
    public void add(T model) {
        if (model == null) {
            return;
        }
        mListData.add(model);
        notifyDataSetChanged();
    }

    /**
     * 添加列表内容
     */
    public void remove(T model) {
        if (model == null) {
            return;
        }
        if (mListData.contains(model)) {
            mListData.remove(model);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        mListData.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (getHasFoot()) {
            if (viewType == TYPE_FOOT) {
                View v = CreateFootView(parent);
                return new FootViewHolder(v);
            } else {
                return onCreateDataViewHolder(parent, viewType);
            }
        } else {
            return onCreateDataViewHolder(parent, viewType);
        }
    }

    protected View CreateFootView(ViewGroup parent) {
        return null;
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }

    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) {
            return;
        }
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = getPosition(viewHolder);
                    mOnItemClickListener.onItemClick(parent, v, mListData.get(position), position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = getPosition(viewHolder);
                    return mOnItemClickListener.onItemLongClick(parent, v, mListData.get(position),
                            position);
                }
                return false;
            }
        });
    }

    protected int getPosition(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition();
    }



  /*  */

    /**
     * 绑定正常数据
     *
     * @return ViewHolder
     */

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount()) {
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

    /**
     * 绑定数据
     *
     * @param holder   ViewHolder
     * @param model
     * @param position 位置
     */
    public abstract void convert(VH holder, T model, int position);

    /**
     * 设置是否有 FooterView
     */
    protected boolean getHasFoot() {
        return false;
    }


    @Override
    public int getItemViewType(int position) {
        if (getHasFoot()) {
            if (position == mListData.size()) {
                return TYPE_FOOT;
            } else {
                return TYPE_DATA;
            }
        } else {
            return TYPE_DATA;
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size() + (getHasFoot() ? 1 : 0);
    }

    public T getItem(int position) {
        try {
            return mListData.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
