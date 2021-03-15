package com.fota.android.widget.recyclerview;

import android.content.Context;

import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.utils.Pub;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SmartRefreshLayoutUtils {

    public static void initHeader(SmartRefreshLayout refreshLayout, Context context) {
        if (refreshLayout == null || context == null) {
            return;
        }
        ClassicsHeader header = new ClassicsHeader(context);
        header.setAccentColor(Pub.getColor(context, R.attr.font_color));
        header.setPrimaryColor(Pub.getColor(context, R.attr.bg_color));
        refreshLayout.setRefreshHeader(header);
    }

    public static void initFooter(SmartRefreshLayout refreshLayout, Context context) {
        ClassicsFooter footer = new ClassicsFooter(context);
        footer.setAccentColor(Pub.getColor(context, R.attr.font_color));
        footer.setPrimaryColor(Pub.getColor(context, R.attr.bg_color));
        refreshLayout.setRefreshFooter(footer);
    }

    public static void initHeaderTrans(SmartRefreshLayout refreshLayout, Context context) {
        if (refreshLayout == null || context == null) {
            return;
        }
        ClassicsHeader header = new ClassicsHeader(context);
        header.setAccentColor(Pub.getColor(context, R.attr.font_color));
        header.setPrimaryColor(FotaApplication.getInstance().getResources().getColor(R.color.transparent));
        refreshLayout.setRefreshHeader(header);
    }

    public static void initFooterTrans(SmartRefreshLayout refreshLayout, Context context) {
        ClassicsFooter footer = new ClassicsFooter(context);
        footer.setAccentColor(Pub.getColor(context, R.attr.font_color));
        footer.setPrimaryColor(FotaApplication.getInstance().getResources().getColor(R.color.transparent));
        refreshLayout.setRefreshFooter(footer);
    }

    public static void refreshHeadLanguage(SmartRefreshLayout refreshLayout, Context context) {
        if (refreshLayout == null || context == null) {
            return;
        }
        if (refreshLayout.getRefreshHeader() == null) {
            return;
        }
        if (!(refreshLayout.getRefreshHeader() instanceof ClassicsHeader)) {
            return;
        }
        ClassicsHeader header = (ClassicsHeader) refreshLayout.getRefreshHeader();

        header.REFRESH_HEADER_PULLING = context.getString(com.scwang.smartrefresh.layout.R.string.srl_header_pulling);
        header.REFRESH_HEADER_REFRESHING = context.getString(com.scwang.smartrefresh.layout.R.string.srl_header_refreshing);
        header.REFRESH_HEADER_LOADING = context.getString(com.scwang.smartrefresh.layout.R.string.srl_header_loading);
        header.REFRESH_HEADER_RELEASE = context.getString(com.scwang.smartrefresh.layout.R.string.srl_header_release);
        header.REFRESH_HEADER_FINISH = context.getString(com.scwang.smartrefresh.layout.R.string.srl_header_finish);
        header.REFRESH_HEADER_FAILED = context.getString(com.scwang.smartrefresh.layout.R.string.srl_header_failed);
        header.REFRESH_HEADER_UPDATE = context.getString(com.scwang.smartrefresh.layout.R.string.srl_header_update);
        header.REFRESH_HEADER_SECONDARY = context.getString(com.scwang.smartrefresh.layout.R.string.srl_header_secondary);
        header.setTimeFormat(new SimpleDateFormat(header.REFRESH_HEADER_UPDATE, Locale.getDefault()));

    }

    public static void refreshFooterLanguage(SmartRefreshLayout refreshLayout, Context context) {
        if (refreshLayout == null || context == null) {
            return;
        }
        if (refreshLayout.getRefreshFooter() == null) {
            return;
        }
        if (!(refreshLayout.getRefreshFooter() instanceof ClassicsFooter)) {
            return;
        }
        ClassicsFooter footer = (ClassicsFooter) refreshLayout.getRefreshFooter();
        footer.REFRESH_FOOTER_PULLING = context.getString(com.scwang.smartrefresh.layout.R.string.srl_footer_pulling);
        footer.REFRESH_FOOTER_RELEASE = context.getString(com.scwang.smartrefresh.layout.R.string.srl_footer_release);
        footer.REFRESH_FOOTER_LOADING = context.getString(com.scwang.smartrefresh.layout.R.string.srl_footer_loading);
        footer.REFRESH_FOOTER_REFRESHING = context.getString(com.scwang.smartrefresh.layout.R.string.srl_footer_refreshing);
        footer.REFRESH_FOOTER_FINISH = context.getString(com.scwang.smartrefresh.layout.R.string.srl_footer_finish);
        footer.REFRESH_FOOTER_FAILED = context.getString(com.scwang.smartrefresh.layout.R.string.srl_footer_failed);
        footer.REFRESH_FOOTER_NOTHING = context.getString(R.string.no_more_data);

    }
}
