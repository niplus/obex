package com.fota.android.common.bean.home;

import java.io.Serializable;

public class GaiLanSocketBean implements Serializable {

    private GaiLanBean general;

    public GaiLanBean getGeneral() {
        return general;
    }

    public void setGeneral(GaiLanBean general) {
        this.general = general;
    }

    @Override
    public String toString() {
        return "GaiLanSocketBean{" +
                ", general=" + general +
                '}';
    }
}
