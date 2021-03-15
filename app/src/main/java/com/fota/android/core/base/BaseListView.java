package com.fota.android.core.base;

import com.fota.android.commonlib.base.BaseView;

import java.util.List;

public interface BaseListView extends BaseView {

    void setDataList(List list);

    void addDataList(List list);

    void refreshComplete();
}
