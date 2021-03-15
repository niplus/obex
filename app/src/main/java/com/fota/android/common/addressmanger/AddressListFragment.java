package com.fota.android.common.addressmanger;

import android.content.DialogInterface;
import android.view.View;

import com.fota.android.R;
import com.fota.android.common.bean.wallet.AddressEntity;
import com.fota.android.common.listener.IAddress;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.core.event.Event;
import com.fota.android.widget.btbwidget.FotaButton;
import com.fota.android.widget.myview.AddressSelectView;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by fjw on 2018/4/18.
 */
public class AddressListFragment extends MvpListFragment<AddressListPresenter> implements
        View.OnClickListener {

    protected FotaButton fotaButton;

    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_xml_with_button;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        fotaButton = findViewById(R.id.bt_sure);
        fotaButton.setText(R.string.wallet_add_address);
        fotaButton.setOnClickListener(this);
        getPresenter().setAssetId(getAssetId());
        getEmpterLayout().setBackgroundColor(Pub.getColor(getContext(), R.attr.bg_color2));
        mTitleLayout.setBackgroundColor(Pub.getColor(getContext(), R.attr.bg_color2));
        //getView().setBackgroundColor(Pub.getColor(getContext(), R.attr.bg_color2));
        onRefresh();
    }

    protected String getAssetId() {
        if (((IAddress) getHoldingActivity()).getModel() == null) {
            return "";
        }
        return ((IAddress) getHoldingActivity()).getModel().getAssetId() + "";
    }

    @Override
    protected String setAppTitle() {
        return getXmlString(R.string.address_manager);
    }

    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<AddressEntity, ViewHolder>(getContext(), R.layout.item_address_list_fragment) {
            @Override
            public void convert(ViewHolder holder, final AddressEntity model, int position) {

                holder.<AddressSelectView>getView(R.id.address_select).setCheck(isSelelctAddress(model));

                holder.setText(R.id.address_name, model.getAddress());
                holder.setText(R.id.address_text, model.getRemarks());

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectAddress(model);
                        onLeftClick();
                    }
                });
                holder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        delect(model.getId());
                        return false;
                    }
                });
            }
        };
    }

    protected boolean isSelelctAddress(AddressEntity model) {
        return ((IAddress) getHoldingActivity()).isSelelctAddress(model);
    }


    protected void selectAddress(AddressEntity model) {
        ((IAddress) getHoldingActivity()).setAddress(model);
    }


    protected void delect(final int id) {
        DialogUtils.showDialog(getContext(), new DialogModel(getXmlString(R.string.sure_delete))
                .setCancelText(getXmlString(R.string.cancel))
                .setSureClickListen(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getPresenter().deleteAddress(id);
                    }
                }).setSureText(getXmlString(R.string.sure))
        );
    }

    @Override
    protected AddressListPresenter createPresenter() {
        return new AddressListPresenter(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_sure:
                addFragment(new AddAddressFragment());
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_notify_address_edit:
                onRefresh();
                break;
        }
    }

    @Override
    public boolean eventEnable() {
        return true;
    }
}
