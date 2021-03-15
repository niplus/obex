package com.fota.android;

import android.view.View;

import com.fota.android.app.Constants;
import com.fota.android.app.ConstantsPage;
import com.fota.android.common.addressmanger.AddressListFragment;
import com.fota.android.common.bean.wallet.AddressEntity;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.http.WebSocketUtils;
import com.fota.android.utils.UserLoginUtil;

import java.util.ArrayList;
import java.util.List;

public class WsIpSettingFragment extends AddressListFragment {

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        fotaButton.setText("手动输入websocket地址");
    }

    @Override
    public boolean eventEnable() {
        return false;
    }

    @Override
    protected void selectAddress(AddressEntity model) {
        IpTools.setWsAddress(model);
        UserLoginUtil.ipChanged(getContext());
    }

    @Override
    protected boolean isSelelctAddress(AddressEntity model) {
        return WebSocketUtils.getWsAddress().contains(model.getAddress());
    }

    @Override
    public void onRefresh() {
        //super.onRefresh();
        //return "ws://172.16.50.200:8092/websocket";
        //return "ws://192.168.1.171:8092/websocket";
        //return "ws://192.168.1.150:8092/websocket";
        List<AddressEntity> list = new ArrayList<>();
        list.add(new AddressEntity(Constants.BASE_WEBSOCKET, "测试环境"));
        list.add(new AddressEntity("ws://172.16.50.200:8092/websocket", "日常"));
        list.add(new AddressEntity("ws://192.168.1.150:8092/websocket", "廉颇150"));
        list.add(new AddressEntity("ws://192.168.1.172:8092/websocket", "韩信172"));
        list.add(new AddressEntity("ws://192.168.1.179:8092/websocket", "韩信179"));
        list.add(new AddressEntity("ws://192.168.1.92:8092/websocket", "戚继光"));

        setDataList(list);
    }

    @Override
    protected String getAssetId() {
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_sure:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.AddIpAddressFragment);
                break;
        }
    }

    @Override
    protected void delect(int id) {
    }
}
