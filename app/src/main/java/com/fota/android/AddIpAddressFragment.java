package com.fota.android;

import com.fota.android.common.addressmanger.AddAddressFragment;
import com.fota.android.common.bean.wallet.AddressEntity;
import com.fota.android.utils.UserLoginUtil;

public class AddIpAddressFragment extends AddAddressFragment {

    @Override
    protected void submit() {
        AddressEntity entity = new AddressEntity(mBinding.etAddressText.getText().toString(),
                mBinding.etAddressRemarkName.getText().toString());
//        IpTools.setAddress(entity);
        UserLoginUtil.ipChanged(getContext());
    }
}
