package com.fota.android.moudles.mine;

import com.fota.android.commonlib.base.BaseView;
import com.fota.android.moudles.mine.bean.MineBean;

public interface IMineView extends BaseView {
    void mineDataSuccess(MineBean mineBean);
    void mineDataFail();
    void unLogin();
    void setNoticeView(String haveNew);

}
