package com.fota.android.common.listener;

import com.fota.android.common.bean.wallet.AddressEntity;
import com.fota.android.common.bean.wallet.WalletItem;

public interface IAddress {

    WalletItem getModel();

    boolean isSelelctAddress(AddressEntity model);

    void setAddress(AddressEntity model);

}
