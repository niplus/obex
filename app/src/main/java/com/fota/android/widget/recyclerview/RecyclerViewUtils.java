package com.fota.android.widget.recyclerview;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.fota.android.widget.recyclerview.layoutmanger.NoScrollLinearLayoutManger;


/**
 * @author fanjianwei
 * @Description ：StwRecyclerViewUtils  帮助类 不用再进行烦躁的初始化
 * @date 创建日期
 */

public class RecyclerViewUtils {


    /**
     * 对recyclerView进行默认的初始化操作
     * 一般
     *
     * @param recyclerView
     * @param context
     */
    public static void initRecyclerView(RecyclerView recyclerView, Context context) {
        //实例化布局管理器并设置布局管理器，滚动到顶部
        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        // 当item大小一样时有利于优化，具体怎么个优化法我也不知道。。
        recyclerView.setHasFixedSize(true);
    }

    /**
     * 对recyclerView进行默认的初始化操作
     * 一般
     *
     * @param recyclerView
     * @param context
     */
    public static void initNoScrollRecyclerView(RecyclerView recyclerView, Context context) {
        //实例化布局管理器并设置布局管理器，滚动到顶部
        NoScrollLinearLayoutManger layoutManager = new NoScrollLinearLayoutManger(context);
        layoutManager.setOrientation(NoScrollLinearLayoutManger.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        // 当item大小一样时有利于优化，具体怎么个优化法我也不知道。。
        recyclerView.setHasFixedSize(true);
    }


    public static void InitScrollRecyclerView(RecyclerView recyclerView, Context context) {
        //实例化布局管理器并设置布局管理器，滚动到顶部
        ScrollSpeedLinearLayoutManger layoutManager;
        layoutManager = new ScrollSpeedLinearLayoutManger(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        // 当item大小一样时有利于优化，具体怎么个优化法我也不知道。。
        recyclerView.setHasFixedSize(true);
    }

    public static void initGridRecyclerView(RecyclerView recyclerView, Context context, int span) {
        //实例化布局管理器并设置布局管理器，滚动到顶部
        GridLayoutManager layoutManager = new GridLayoutManager(context, span);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        // 当item大小一样时有利于优化，具体怎么个优化法我也不知道。。
        recyclerView.setHasFixedSize(true);
    }

    public static void initHorizotalRecyclerView(RecyclerView recyclerView, Context context) {
        //实例化布局管理器并设置布局管理器，滚动到顶部
        ScrollSpeedLinearLayoutManger layoutManager;
        layoutManager = new ScrollSpeedLinearLayoutManger(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        // 当item大小一样时有利于优化，具体怎么个优化法我也不知道。。
        recyclerView.setHasFixedSize(true);
    }


  /*  *//**
     * 对recyclerView进行默认的初始化操作
     * 一般
     *
     * @param recyclerView
     * @param context
     *//*
    public static void initFlowRecyclerView(RecyclerView recyclerView, Context context) {
        //实例化布局管理器并设置布局管理器，滚动到顶部
        // 当item大小一样时有利于优化，具体怎么个优化法我也不知道。
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(context)
                //set vertical gravity for all items in a row. Default = Gravity.CENTER_VERTICAL
                .setChildGravity(Gravity.TOP)
                //whether RecyclerView can scroll
                .setScrollingEnabled(true)
                //set maximum views count in a particular row
                .setMaxViewsInRow(4)
                //set gravity resolver where you can determine gravity for item in position. This method have priority over
                // previous one
                .setGravityResolver(new_address IChildGravityResolver() {
                    @Override
                    public int getItemGravity(int position) {
                        return Gravity.LEFT;
                    }
                })
                .build();
        recyclerView.setLayoutManager(chipsLayoutManager);
        recyclerView.setHasFixedSize(true);
    }*/

    /**
     * 实现瀑布流的效果
     *
     * @param recyclerView
     * @param context
     */
    public static void initStaggeredGridLayoutRecyclerView(RecyclerView recyclerView, Context context) {
        //实例化布局管理器并设置布局管理器，滚动到顶部
        StaggeredGridLayoutManager layoutManager;
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        // 当item大小一样时有利于优化，具体怎么个优化法我也不知道。。
        recyclerView.setHasFixedSize(true);
    }


}