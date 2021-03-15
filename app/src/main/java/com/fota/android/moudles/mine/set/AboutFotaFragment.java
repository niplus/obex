package com.fota.android.moudles.mine.set;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.core.base.ft.KeyValue;
import com.fota.android.databinding.FragmentAboutfotaBinding;
import com.fota.android.utils.DeviceUtils;
import com.fota.android.widget.popwin.SelectItemDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 关于方塔
 */
public class AboutFotaFragment extends BaseFragment {
    FragmentAboutfotaBinding mBinding;

    boolean noClass;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_aboutfota, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        String version = DeviceUtils.getVersonName(mContext);

        try {
            Class.forName(ConstantsPage.IpSettingFragment);
        } catch (ClassNotFoundException e) {
            noClass = true;
            e.printStackTrace();
        }
        mBinding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBinding.tvVersion.setText(mContext.getResources().getString(R.string.mine_about_broker_msg, version));
        mBinding.tvVersion.setOnClickListener(new View.OnClickListener() {

            int count;

            @Override
            public void onClick(View view) {
                //只有在测试环境下执行
                if (noClass) {
                    return;
                }
                showSelectDialog();
            }
        });
        setJustWhiteBarTxt();

    }

    private void showSelectDialog() {
        List<KeyValue> items = new ArrayList<>();
        items.add(new KeyValue("ip setting", ConstantsPage.IpSettingFragment));
        items.add(new KeyValue("ws setting", ConstantsPage.WsIpSettingFragment));
        SelectItemDialog dialog = new SelectItemDialog(getContext(), "select ip mode",
                items, items.get(0));
        dialog.setListener(new SelectItemDialog.OnSureClickListener() {

            @Override
            public void onClick(FtKeyValue model) {
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), model.getValue());
            }
        });
        dialog.show();
    }

    @Override
    public String setAppTitle() {
        return getResources().getString(R.string.mine_about);
    }

}
