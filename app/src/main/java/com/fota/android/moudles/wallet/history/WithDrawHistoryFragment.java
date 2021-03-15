package com.fota.android.moudles.wallet.history;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.common.bean.wallet.WalletHistoryBean;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.databinding.FragmentWithdrawHistoryBinding;
import com.fota.android.widget.bubblepopup.BubblePopupWindow;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * create by fjw
 * 充提币记录
 */
public class WithDrawHistoryFragment extends MvpListFragment<WithDrawHistoryPresenter> implements View.OnClickListener {

    @Override
    protected boolean setLoadEnable() {
        return true;
    }

    private FragmentWithdrawHistoryBinding mBinding;
    private BubblePopupWindow popupTypeWindow;
    private BubblePopupWindow popupAssetWindow;
    private ClipboardManager myClipboard;

    public static WithDrawHistoryFragment newInstance(String key) {
        Bundle args = new Bundle();
        args.putString(BundleKeys.KEY, key);
        WithDrawHistoryFragment fragment = new WithDrawHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdraw_history, container, false);
        mBinding.setView(this);
        myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        return mBinding.getRoot();
    }


    @Override
    protected WithDrawHistoryPresenter createPresenter() {
        return new WithDrawHistoryPresenter(this);
    }

    @Override
    protected int getContainerLayout() {
        return R.layout.fragment_withdraw_history;
    }


    /**
     * list coins
     */
    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<WalletHistoryBean, ViewHolder>(getContext(), R.layout.item_withdraw_history_fragment) {

            @Override
            public void convert(final ViewHolder holder, final WalletHistoryBean model, int position) {
                holder.setText(R.id.amount, model.getFormatAmout());
                holder.setText(R.id.tv_unit, model.getAssetName());
                holder.setText(R.id.fee, model.getFormatFee());
                holder.setText(R.id.address, model.getAddress());
                holder.setText(R.id.txHash, model.getTxHash());
                holder.setText(R.id.txTime, model.getFormatTime());
                holder.setText(R.id.status, model.getStatusFromat(getContext()));
                holder.setTextColor(R.id.status, model.getStatusColor(getContext()));
                holder.setText(R.id.status_details, model.getStatusDetailFormat(getContext()));
                holder.getView(R.id.address).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        copyTextToClipBoard(holder.<TextView>getView(R.id.address).getText().toString());
                        return true;
                    }
                });

                holder.getView(R.id.txHash).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        copyTextToClipBoard(holder.<TextView>getView(R.id.txHash).getText().toString());
                        return true;
                    }
                });


                holder.setVisible(R.id.status_reverse, model.canCancel());
//                holder.setTextColor(R.id.amount, AppConfigs.getColor(model.isUp()));
                if (model.getTransferType() == 1) {
                    holder.setVisible(R.id.view_recharge, true);

                } else if (model.getTransferType() == 2) {
                    holder.setVisible(R.id.view_recharge, false);

                }
                if (AppConfigs.getTheme() == 0) {
                    holder.setTextColor(R.id.status_reverse, mContext.getResources().getColor(R.color.withdrawhis_revert_black));
                } else {
                    holder.setTextColor(R.id.status_reverse, mContext.getResources().getColor(R.color.withdrawhis_revert_white));
                }

                holder.setOnClickListener(R.id.status_reverse, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelItem(model);
                    }
                });
                if (AppConfigs.isChinaLanguage()) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUtil.dip2px(mContext, 60),
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    holder.getView(R.id.tv1).setLayoutParams(params);
                    holder.getView(R.id.tv2).setLayoutParams(params);
                    holder.getView(R.id.tv3).setLayoutParams(params);
                    holder.getView(R.id.tv4).setLayoutParams(params);
                } else {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUtil.dip2px(mContext, 88),
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    holder.getView(R.id.tv1).setLayoutParams(params);
                    holder.getView(R.id.tv2).setLayoutParams(params);
                    holder.getView(R.id.tv3).setLayoutParams(params);
                    holder.getView(R.id.tv4).setLayoutParams(params);
                }
            }
        };
    }

    private void cancelItem(final WalletHistoryBean model) {
        DialogUtils.showDialog(getContext(), new DialogModel(getString(R.string.wallet_sure_withdraw_called))
                .setCancelText(getXmlString(R.string.cancel))
                .setSureClickListen(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getPresenter().deleteItem(model);
                    }
                }).setSureText(getXmlString(R.string.sure))
        );
    }

    @Override
    protected String setAppTitle() {
        return getXmlString(R.string.wallet_withdraw_history);
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        getPresenter().initTypeList();
        getPresenter().initBaseAssetList();
    }

    @Override
    public void showNoData() {
        super.showNoData();
        UIUtil.setImageResource(getEmpterImage(),
                AppConfigs.isWhiteTheme() ? R.mipmap.recharge_no_data_white : R.mipmap.recharge_no_data
        );
    }

    public boolean copyTextToClipBoard(String str) {
        boolean flag = false;
        if (str == null)
            return flag;
        try {
            ClipData myClip = ClipData.newPlainText("text", str);//str
            myClipboard.setPrimaryClip(myClip);
            showToast(CommonUtils.getResouceString(getContext(), R.string.copy_success));
        } catch (Exception e) {
        }
        return flag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.asset_name:
            case R.id.asset_name_icon:
                showAssetPopWindow();
                break;
            case R.id.type_name:
            case R.id.type_icon:
                showTypePopWindow();
                break;
        }
    }

    private void showTypePopWindow() {
        if (!Pub.isListExists(getPresenter().getTypeList())) {
            return;
        }
        if (popupTypeWindow == null) {
            popupTypeWindow = new BubblePopupWindow(getContext());
            popupTypeWindow.setOnPopListener(new BubblePopupWindow.OnPopClickListener() {
                @Override
                public void onPopClick(FtKeyValue model, int position) {
                    getPresenter().setTypeIndex(position);
                    refreshType();
                }
            });
        }
        popupTypeWindow.setValue(getPresenter().getTypeValue());
        popupTypeWindow.getAdapter().putList(getPresenter().getTypeList());
//        popupTypeWindow.showAsDropDown(mBinding.typeName);
//        popupTypeWindow.showAsDropDown(mBinding.typeName, (mBinding.typeName.getMeasuredWidth() - popupTypeWindow.getContentView().getMeasuredWidth()) / 2, 0);
        popupTypeWindow.show(mBinding.typeName);

    }

    private void refreshType() {
        mBinding.typeName.setText(getPresenter().getTypeList().get(getPresenter().getTypeIndex()).getKey());
    }

    private void showAssetPopWindow() {
        if (!Pub.isListExists(getPresenter().getAssetList())) {
            return;
        }
        if (popupAssetWindow == null) {
            popupAssetWindow = new BubblePopupWindow(getContext());
            popupAssetWindow.setOnPopListener(new BubblePopupWindow.OnPopClickListener() {
                @Override
                public void onPopClick(FtKeyValue model, int position) {
                    getPresenter().setAssetIndex(position);
                    refreshAsset();
                }
            });
        }
        popupAssetWindow.getAdapter().putList(getPresenter().getAssetList());
        popupAssetWindow.setValue(getPresenter().getAssetValue());
//        popupAssetWindow.showAsDropDown(mBinding.assetName);
//        popupAssetWindow.showAsDropDown(mBinding.assetName, (mBinding.assetName.getMeasuredWidth() - popupAssetWindow.getContentView().getMeasuredWidth()) / 2, 0);
        popupAssetWindow.show(mBinding.assetName);
    }

    private void refreshAsset() {
        mBinding.assetName.setText(getPresenter().getAssetList().get(getPresenter().getAssetIndex()).getAssetName());
    }

    @Override
    public void notifyFromPresenter(int action) {
        super.notifyFromPresenter(action);
        refreshAsset();
        refreshType();
    }

    @Override
    public boolean setFootAndHeadTrans() {
        return true;
    }
}
