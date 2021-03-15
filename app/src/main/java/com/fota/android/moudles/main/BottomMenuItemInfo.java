package com.fota.android.moudles.main;

import java.io.Serializable;
import java.util.List;

public class BottomMenuItemInfo implements Serializable {

    List<BottomMenuItem> tabbar;

    String versionStatus;


    public List<BottomMenuItem> getTabbar() {
        return tabbar;
    }

    public void setTabbar(List<BottomMenuItem> tabbar) {
        this.tabbar = tabbar;
    }

    public String getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(String versionStatus) {
        this.versionStatus = versionStatus;
    }
}
