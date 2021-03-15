package com.fota.android.moudles.main.bean;

import java.io.Serializable;

/**
 * Created by Dell on 2018/5/16.
 */

public class VersionRootBean implements Serializable {

    VersionBean android;

    public VersionBean getAndroid() {
        return android;
    }

    public void setAndroid(VersionBean android) {
        this.android = android;
    }
}
