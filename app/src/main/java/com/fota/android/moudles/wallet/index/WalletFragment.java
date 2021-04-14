package com.fota.android.moudles.wallet.index;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.common.bean.wallet.WalletItem;
import com.fota.android.common.bean.wallet.WalletPresenter;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.ToastUitl;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.commonlib.utils.URLUtils;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.core.event.Event;
import com.fota.android.databinding.WalletFragmentHeadBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.MineBean;
import com.fota.android.moudles.wallet.WithTransferFragment;
import com.fota.android.moudles.wallet.history.WithDrawHistoryFragment;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.widget.TitleLayout;
import com.fota.android.widget.btbwidget.ClearEdittext;
import com.fota.android.widget.btbwidget.FotaButton;
import com.fota.android.widget.btbwidget.FotaTextWatch;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class WalletFragment extends WithTransferFragment<WalletPresenter> implements View.OnClickListener {


    private WalletFragmentHeadBinding binding;
    public final static int SET_All_MONEY_INFO = 0;
    public final static int SET_All_MONEY_INFO_USD = 1;
    double available;
    String availableStr;

    ClearEdittext clearEdittext;
    FotaButton fotaButton;
    TextView allinText;
    TextView maxtransferText;
    private MineBean meData;


    @Override
    protected WalletPresenter createPresenter() {
        return new WalletPresenter(this);
    }

    /**
     * list coins
     */
    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<WalletItem, ViewHolder>(getContext(), R.layout.item_wallet_fragment) {

            @Override
            public void convert(ViewHolder holder, final WalletItem model, int position) {
                holder.setText(R.id.tv_name, model.getAssetName());
                holder.setText(R.id.tv_total, model.getAllAmount());
                holder.setText(R.id.tv_available, model.getAmount());
                holder.setText(R.id.tv_val, model.getValuation());
                holder.setText(R.id.tv_frozen, model.getLockedAmount());

                holder.setText(R.id.tv_total_title, mContext.getResources().getString(R.string.exchange_totol_money) + "(" + model.getAssetName() + ")");
                holder.setText(R.id.tv_available_title, mContext.getResources().getString(R.string.common_usable) + "(" + model.getAssetName() + ")");
                holder.setText(R.id.tv_val_title, mContext.getResources().getString(R.string.common_valuation) + "(BTC)");
                holder.setText(R.id.tv_frozen_title, mContext.getResources().getString(R.string.common_freeze) + "(" + model.getAssetName() + ")");
                holder.setVisible(R.id.ll_val, !"BTC".equals(model.getAssetName()));
//                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        addFragment(WalletDetailsFragment.newInstance(model));
//                    }
//
//                });
                ImageView imageView = holder.getConvertView().findViewById(R.id.imv_icon);
                Glide.with(getContext()).load(URLUtils.getFullPath(model.getCoinIconUrl())).into(imageView);
            }

            @Override
            protected boolean getHasFoot() {
                return true;
            }

            protected View CreateFootView(ViewGroup parent) {
                LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_wallet_foot, null);
                clearEdittext = ll.findViewById(R.id.edit_transfer);
                fotaButton = ll.findViewById(R.id.bt_sure);
                allinText = ll.findViewById(R.id.txt_all_in);
                maxtransferText = ll.findViewById(R.id.txt_max_transfer_tips);
                WalletFragment.super.setView(clearEdittext, fotaButton);
                fotaButton.setOnClickListener(WalletFragment.this);
                allinText.setOnClickListener(WalletFragment.this);
                bindValid(fotaButton, clearEdittext);
                clearEdittext.addTextChangedListener(new FotaTextWatch() {
                    @Override
                    protected void onTextChanged(String s) {
                        try {
                            if (Pub.isTooLongCharacter(clearEdittext, 8)) {
                                return;
                            }
                            if (isMaxBalance(clearEdittext)) {
                                String maxAvalible = String.format(getString(R.string.transfer_max_usdt), availableStr);
                                ToastUitl.showShort(maxAvalible);
                                return;
                            }
                            double inputDouble = Pub.GetDouble(s.toString());
                            fotaButton.setBtbEnabled(inputDouble >= 0.00000001);
                        } catch (Exception e) {
                            fotaButton.setBtbEnabled(false);
                        }
                    }
                });
                KeyBoardUtils.addOnKeyboardListener(binding.getRoot(), new KeyBoardUtils.OnKeyboardListener() {
                    @Override
                    public void changed(boolean isOpened) {
                        if (!isOpened && !TextUtils.isEmpty(clearEdittext.getText())) {
                            double input = Pub.GetDouble(clearEdittext.getText().toString(), 0);
                            if (input < 0.00000001) {
                                //showMinToast();
                                showToast(R.string.transfer_min_usdt);
                            }
                        }
                    }
                });
                return ll;
            }
        };
    }

    @Override
    protected String setAppTitle() {
        return getXmlString(R.string.wallet_contact_my_money_account);
    }

    @Override
    protected View setHeadView() {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.wallet_fragment_head,
                null, false);
        binding.setView(this);
        return binding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        binding.setView(this);

        binding.llExtract.setOnClickListener(this);
        binding.llRecharge.setOnClickListener(this);
        onRefresh();
        initMeData();
        if (AppConfigs.getTheme() == 0) {
            mTitleLayout.setRightIcon(R.mipmap.wallet_his_black);
        } else {
            mTitleLayout.setRightIcon(R.mipmap.wallet_his_white);
        }
        mTitleLayout.setRightIconSize(UIUtil.dip2px(mContext, 18), UIUtil.dip2px(mContext, 19.5));
        mTitleLayout.setOnRightButtonClickListener(new TitleLayout.OnRightButtonClickListener() {
            @Override
            public void onRightButtonClick(View v) {
                addFragment(new WithDrawHistoryFragment());
            }
        });

    }

    private void initMeData() {
        Http.getHttpService().getMineData()
                .compose(new CommonTransformer<MineBean>())
                .subscribe(new CommonSubscriber<MineBean>(this) {

                    @Override
                    public void onNext(MineBean mineBean) {
                        meData = mineBean;
                    }

                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sure:
                submit();
                break;
            case R.id.txt_all_in:
                allIn();
                break;
            case R.id.ll_recharge:
                SimpleFragmentActivity.gotoFragmentActivity(mContext, ConstantsPage.RechargeMoneyFragment);
                break;
            case R.id.ll_extract:
                //提币
                if (meData == null) {
                    return;
                }
                if (meData.getUserSecurity() == null) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                if (!meData.getUserSecurity().isPhoneAuth()) {
                    sb.append(getString(R.string.first_bind_phone));
                    sb.append(" ");
                }
                if (!meData.getUserSecurity().isGoogleAuth()) {
                    sb.append(getString(R.string.first_bind_google));
                    sb.append(" ");
                }
                if (!meData.getUserSecurity().isFundPwdSet()) {
                    sb.append(getString(R.string.first_bind_asset));
                    sb.append(" ");
                }
                if (sb.length() > 0) {
                    String needInfo = getXmlString(R.string.wallet_withdraw_need);
                    DialogUtils.showDialog(getContext(),
                            new DialogModel(sb.toString())
                                    .setTitle(needInfo)
                                    .setSureText(getXmlString(R.string.common_goto_ret))
                                    .setSureClickListen(new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SimpleFragmentActivity.gotoFragmentActivity(getContext(),
                                                    ConstantsPage.SafeSettingFragment
                                            );
                                        }

                                    })
                                    .setCancelText(getXmlString(R.string.cancel))
                    );
                    return;
                }
                FtRounts.toActivity(getContext(), ConstantsPage.WithdrawActivity);
                break;
        }
    }

    @Override
    public void notifyFromPresenter(int action, String data) {
        switch (action) {
            case SET_All_MONEY_INFO:
                binding.usdtMoney.setText(data);
                break;
            case SET_All_MONEY_INFO_USD:
                binding.usdMoney.setText(" ≈ " + (TextUtils.isEmpty(data) ? "" : data));
                break;
            default:
                super.notifyFromPresenter(action, data);
        }
    }

    @Override
    public void setDataList(List list) {
//        List data = new ArrayList();
//        WalletItem usdt = null;
        if (Pub.isListExists(list)) {
            for (Object listItem : list) {
                if (listItem instanceof WalletItem) {
                    if ("USDT".equals(((WalletItem) listItem).getAssetName())) {
                        WalletItem item = (WalletItem) listItem;
                        available = Pub.GetDouble(item.getAmount());
                        availableStr = item.getAmount();
                        super.setAvailable(available);
                        setAvailabelString(availableStr);
                        String maxAvalible = item.getAmount();
                        maxtransferText.setText(maxAvalible);
//                        break;
                    }
                }
            }

            super.setDataList(list);
        } else {
            super.setDataList(null);
        }
//        setUsdtItem(usdt);
    }

    @Override
    public void afterTransferDialog() {
        DialogUtils.showDialog(getContext(),
                new DialogModel(getString(R.string.wallet_exchange_ok))
                        .setSureText(getXmlString(R.string.transfer_see_history))
                        .setIcon(R.mipmap.icon_check_suc)
                        .setSureClickListen(new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                addFragment(new TransferHistoryFragment());
                                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.TransferHistoryFragment, null);

                            }
                        })
                        .setCanCancelOnTouchOutside(true)
        );
        onRefresh();
    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_capital_quickcheck_suc:
                if (!TextUtils.equals(mRequestCode, event.getParam(String.class))) {
                    return;
                }
                finalDo();
                break;
            case R.id.event_capital_quickcheck_fail:
                if (!TextUtils.equals(mRequestCode, event.getParam(String.class))) {
                    return;
                }
                pwdDialogShow();
                break;
        }
    }
}
