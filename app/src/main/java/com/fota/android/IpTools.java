package com.fota.android;

import com.fota.android.common.bean.wallet.AddressEntity;
import com.fota.android.commonlib.base.AppConfigs;

public class IpTools {

    public static void setIpAddress(AddressEntity model) {
        AppConfigs.setIpAddress(model.getAddress());
    }

    public static void setWsAddress(AddressEntity model) {
        AppConfigs.setWsAddress(model.getAddress());
    }
}
