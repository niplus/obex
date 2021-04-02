package com.fota.android.moudles.wallet.withdraw;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fota.android.MyViewHolder;
import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.ConstantsPage;
import com.fota.android.common.addressmanger.AddressListFragment;
import com.fota.android.common.bean.wallet.AddressEntity;
import com.fota.android.common.bean.wallet.RateBean;
import com.fota.android.common.bean.wallet.WalletItem;
import com.fota.android.common.bean.wallet.WithDrawEntity;
import com.fota.android.common.listener.IAddress;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.AndroidBug5497Workaround;
import com.fota.android.commonlib.utils.DecimalDigitsInputFilter;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.SpannableStringUtils;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.commonlib.utils.URLUtils;
import com.fota.android.core.base.BoolEnum;
import com.fota.android.core.base.BtbMap;
import com.fota.android.core.base.MvpActivity;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.core.event.Event;
import com.fota.android.databinding.ActivityWithdrawBinding;
import com.fota.android.moudles.wallet.history.WithDrawHistoryFragment;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.utils.apputils.TradeUtils;
import com.fota.android.widget.TitleLayout;
import com.fota.android.widget.btbwidget.FotaTextWatch;
import com.fota.android.widget.popwin.PasswordDialog;
import com.fota.android.widget.popwin.SelectItemDialog;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class WithdrawActivity extends MvpActivity<WithdrawPresenter> implements WithdrawView, IAddress, View
        .OnClickListener {

    private final static String FORMAT = "^((ETH|FOTA)(0x)?[a-z0-9A-Z]{40})|(BTC(1|2|3)[a-z0-9A-Z]{25,34})|((USDT)[a-z0-9A-Z]{1,40})$";

    ActivityWithdrawBinding binding;
    private WalletItem model;
    private AddressEntity address;
    private double maxValue;

    public static final int WITHDRAW_PRE = 1;

    public List<String> netData;

    private String selectNet = "";


    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        model = (WalletItem) bundle.getSerializable(BundleKeys.MODEL);
    }

    @Override
    protected void initSystemBar() {
        super.initSystemBar();
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public String getAppTitle() {
        return getXmlString(R.string.wallet_withdraw_money);
    }

    @Override
    protected void onDateBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_withdraw);
        binding.setView(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan:
                //充币
                scanQrCode(REQUEST_CODE_SCAN, null);
                break;
            case R.id.select_currency_address_layout:
                //充币
                addFragment(new AddressListFragment());
                break;
            case R.id.amount_all:
                //充币
                if (model == null) {
                    return;
                }
                binding.amount.setText(model.getAmount());
                break;
            case R.id.bt_sure:
                getPresenter().withDrawCheck();
                break;
            case R.id.select_asset:
                showSelectDialog();
                break;
            default:
                break;
        }
    }

    private void showSelectDialog() {
        SelectItemDialog dialog = new SelectItemDialog(getContext(), getXmlString(R.string.select_withdraw_asset),
                getPresenter().getItems(), model);
        dialog.setListener(new SelectItemDialog.OnSureClickListener() {

            @Override
            public void onClick(FtKeyValue model) {
                setSelectItem((WalletItem) model);
            }
        });
        dialog.show();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        getPresenter().getWallet();
        netData = new ArrayList<>();
        if (AppConfigs.getTheme() == 0) {
            mTitleLayout.setRightIcon(R.mipmap.wallet_his_black);
        } else {
            mTitleLayout.setRightIcon(R.mipmap.wallet_his_white);
        }
        mTitleLayout.setRightIconSize(UIUtil.dip2px(getContext(), 18),
                UIUtil.dip2px(getContext(), 19.5));
        mTitleLayout.setOnRightButtonClickListener(new TitleLayout.OnRightButtonClickListener() {
            @Override
            public void onRightButtonClick(View v) {
                if (model == null) {
                    return;
                }
                addFragment(WithDrawHistoryFragment.newInstance(model.getAssetId()));
            }
        });

        binding.rvNet.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.rvNet.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_withdraw_recharge_net, null, false);
//                TextView tvNet = rootView.findViewById(R.id.tv_net);
                return new MyViewHolder(rootView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                TextView view = ((MyViewHolder)holder).itemView.findViewById(R.id.tv_net);
                view.setText(netData.get(position));

                if (netData.get(position).endsWith(selectNet)){
                    view.setTextColor((int)0xFF3C78D7);
                    view.setBackgroundResource(R.drawable.shape_stroke_3c78d7);
                }else {
                    view.setTextColor((int)0xFF999999);
                    view.setBackgroundResource(R.drawable.shape_stroke_cacaca);
                }

                view.setOnClickListener(v -> {
                    selectNet = view.getText().toString();
                    getPresenter().getRate(model.getAssetName(), selectNet);
                    binding.rvNet.getAdapter().notifyDataSetChanged();
                });

            }

            @Override
            public int getItemCount() {
                return netData.size();
            }
        });
        binding.rvNet.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != 0){
                    outRect.left = 30;
                }
            }
        });
    }

    private void initListener() {
        binding.amount.addTextChangedListener(new FotaTextWatch() {
            @Override
            protected void onTextChanged(String s) {
                changeByAmount();
            }
        });
//        changeByAmount();
    }

    private RateBean selectRate;

    private void changeByAmount() {
        if (selectRate == null)
            return;
        //double value = Pub.GetDouble(binding.amount.getText().toString());
        double all = Pub.GetDouble(binding.amount.getText().toString());
        double fee = 0;
        if (!Pub.isStringEmpty(selectRate.getFixedFeeAmount())) {
            //固定手续费
            fee = Pub.GetDouble(selectRate.getFixedFeeAmount());//小于最小 取最下
        } else {
            //无固定手续费
            fee = Pub.multiply(all, Pub.GetDouble(selectRate.getWithdrawFeeRate()));
            double minFee = Pub.GetDouble(selectRate.getWithdrawFeeRate());
            fee = Math.max(fee, minFee);//小于最小 取最下

            if (all == 0) {
                fee = 0;
            }
        }

        String feeString = Pub.getPriceFormat(fee, getAmountPrecision(), RoundingMode.UP);
        getPresenter().getModel().setFee(String.valueOf(fee));

        SpannableStringUtils.Builder sb = SpannableStringUtils.getBuilder(getString(R.string.wallet_fee));
        sb.append(" ");
        sb.append(Pub.zoomZero(feeString)).setForegroundColor(Pub.getColor(getContext(), R.attr.main_color));
        sb.append(" ");
        sb.append(model.getAssetName());
        binding.fee.setText(sb.create());


        double getMoney = Pub.sub(all, fee);
        getMoney = Math.max(getMoney, 0);//小于0 取0
        String getMoneyString = Pub.getPriceFormat(getMoney, getAmountPrecision());
        SpannableStringUtils.Builder sbGet = SpannableStringUtils.getBuilder(getString(R.string.wallet_get_money));
        sbGet.append(" ");
        sbGet.append(Pub.zoomZero(getMoneyString)).setForegroundColor(Pub.getColor(getContext(), R.attr.main_color));
        sbGet.append(" ");
        sbGet.append(model.getAssetName());
        binding.getMoney.setText(sbGet.create());
    }

    @Override
    protected WithdrawPresenter createPresenter() {
        return new WithdrawPresenter(this);
    }


    @Override
    public void setAddress(AddressEntity address) {
        this.address = address;
        binding.address.setText(address.getAddress());
        UIUtil.setTextWithVisable(binding.addressRemark, address.getRemarks());
    }

    @Override
    public boolean isSelelctAddress(AddressEntity address) {
        if (address == null) {
            return false;
        }
        return address.equals(this.address);
    }

    /**
     * 获取当前数量的小数位数
     *
     * @return
     */
    public int getAmountPrecision() {
        if (model != null && model.getMinWithdrawPrecision() != null) {
            return Pub.GetInt(model.getMinWithdrawPrecision());
        }
        return 2;
    }

    @Override
    public WalletItem getModel() {
        return model;
    }

    private void setTip() {
        //StringBuilder sb = new StringBuilder();
        SpannableStringUtils.Builder sb = SpannableStringUtils.getBuilder(
                "1. ");
        sb.append(getString(R.string.wallet_with_draw_tip_1));
        sb.append(" ");
        sb.append(model.getAssetName());
        sb.append(" ");
        sb.append(getString(R.string.wallet_with_draw_tip_2));
        sb.append(" ");
        sb.append(getString(R.string.common_full_point));
        sb.append("\n");
        sb.append(getString(R.string.wallet_tip_withdraw_3));
        sb.append(" ");
        sb.append(getString(R.string.wallet_tip_withdraw_history_page)).setForegroundColor(Pub.getColor(getContext(),
                R.attr.main_color));
        sb.append(" ");
        sb.append(getString(R.string.wallet_tip_withdraw_review));
        binding.tip.setText(sb.create());
        binding.tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model == null) {
                    return;
                }
                addFragment(WithDrawHistoryFragment.newInstance(model.getAssetId()));
            }
        });
    }

    /**
     * 执行交易
     *
     * @param fundCode
     * @param code
     */
    protected void tradeToPresenter(String fundCode, String code, String googleCode) {
        getPresenter().getModel().setSmsCode(code);
        getPresenter().getModel().setTradeToken(fundCode);
        getPresenter().getModel().setGoogleCode(googleCode);
        getPresenter().submit(selectNet);
    }

    /**
     * 验证密码
     */
    protected void verifyPassword() {
        TradeUtils.getInstance().validPassword(getContext(), mRequestCode, new TradeUtils.ValidPasswordListener() {
            @Override
            public void showPasswordDialog() {
                showPhoneDialog(true);
            }
        });
    }

    /**
     * 指纹弹框 不需要资金密码
     */
    private void showPhoneDialog(final boolean needPassword) {
        if (getHoldingActivity().isFinishing()) {
            return;
        }
        PasswordDialog dialog = new PasswordDialog(getContext());
        dialog.setShowFundCode(needPassword);
        dialog.setShowEmsCode(true);
        dialog.setShowGoogleCode(true);
        dialog.setMoreListener(new PasswordDialog.OnMoreSureClickListener() {

            @Override
            public void onClick(String fundCode, final String ems, final String google) {
                if (needPassword) {
                    TradeUtils.getInstance().changePasswordToToken(getContext(), fundCode,
                            new TradeUtils.ChangePassWordListener() {
                                @Override
                                public void setPasswordToken(String token) {
                                    tradeToPresenter(UserLoginUtil.getCapital(), ems, google);
                                }
                            });
                    return;
                }
                tradeToPresenter(UserLoginUtil.getCapital(), ems, google);
            }

        });
        dialog.show();
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
                showPhoneDialog(false);
                break;
            case R.id.event_capital_quickcheck_fail:
                if (!TextUtils.equals(mRequestCode, event.getParam(String.class))) {
                    return;
                }
                showPhoneDialog(true);
                //showPasswordPhoneDialog();
                break;
        }
    }

    @Override
    public void notifyFromPresenter(int action) {
        switch (action) {
            case WITHDRAW_PRE:
                //验证通过
                double amount = Pub.GetDouble(binding.amount.getText().toString());
                if (selectRate != null && Pub.GetDouble(selectRate.getMinWithdrawFeeStandard()) > 0 &&
                        amount < Pub.GetDouble(selectRate.getMinWithdrawFeeStandard())
                        ) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getXmlString(R.string.wallet_min_withdraw_money));
                    sb.append(selectRate.getMinWithdrawFeeStandard());
                    model.getAssetName();
                    showToast(sb.toString());
                    return;
                }
//                String asset = model.getAssetName() + binding.address.getText().toString();
//                if (!asset.matches(FORMAT)) {
//                    showToast(R.string.code_101300);
//                    return;
//                }
                if (amount > Pub.GetDouble(model.getAmount())) {
                    showToast(getString(R.string.wallet_your_wallet_can_use) + model.getAssetName() + getString(R.string.wallet_your_wallet_can_use_2));
                    return;
                }
                getPresenter().getModel().setToAddress(binding.address.getText().toString());
                getPresenter().getModel().setAmount(binding.amount.getText().toString());
                verifyPassword();
                break;
        }
        super.notifyFromPresenter(action);
    }

    @Override
    public void setSuccess(BtbMap map) {
        String isSuccess = map.get("isSuccess");
        String isVerify = map.get("isVerify");
        String amount = map.get("amount");
        if (BoolEnum.isTrue(isSuccess)) {
            DialogUtils.showDialog(getContext(), new DialogModel(getXmlString(R.string.wallet_withdraw_ok))
                    .setIcon(R.mipmap.icon_check_suc)
                    .setSureText(getString(R.string.wallet_trade_right_now))
                    .setSureClickListen(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.ExchangeFragment);
                        }
                    })
                    .setCancelText(getString(R.string.wallet_look_for_history))
                    .setCancelClickListen(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.WithDrawHistoryFragment);
                        }
                    })
            );
        } else {
            StringBuilder sb = new StringBuilder();
            //完成身份认证的，超过提币限制，展示文案「单日单次最大提现额度 100 个 BTC」。
            //未完成身份认证，超过提币限制，展示文案「您未完成身份认证，单日单次最大提现额度 2 个BTC 」；
            if (!BoolEnum.isTrue(isVerify)) {
                sb.append(getString(R.string.wallet_limit_error1));
            }
            sb.append(getString(R.string.wallet_limit_error2));
            sb.append(" ");
            sb.append(amount);
            sb.append(" ");
            sb.append(getString(R.string.wallet_limit_error3));
            sb.append(" ");
            sb.append(model.getAssetName());
            sb.append(" ");
            showToast(sb.toString());
        }
    }

    @Override
    public void setSelectItem(WalletItem walletItem) {
        model = walletItem;
        if (model == null) {
            return;
        }
        WithDrawEntity entity = new WithDrawEntity(model);
        getPresenter().setModel(entity);

        binding.address.setText("");
        binding.amount.setText("");
        UIUtil.setTextWithVisable(binding.addressRemark, "");

        binding.assetName.setText(model.getAssetName());
        Glide.with(getContext()).load(URLUtils.getFullPath(model.getCoinIconUrl()))
                .apply(new RequestOptions().error(R.mipmap.icon_logo))
                .into(binding.assetIcon);
        binding.balanceMoney.setText(model.getAmount());
        binding.addressTitle.setText(model.getAssetName() + " " + getString(R.string.wallet_address));
        binding.balanceTitle.setText(getString(R.string.wallet_can_withdraw) + " " + model.getAssetName() + " :");
        binding.address.addTextChangedListener(new FotaTextWatch() {
            @Override
            protected void onTextChanged(String s) {
                UIUtil.setTextWithVisable(binding.addressRemark, "");
            }
        });
        binding.amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(getAmountPrecision())});


        netData.clear();
        selectNet = "";
        if (model.getWnetwork() != null) {
            netData.addAll(model.getWnetwork());
            selectNet = model.getWnetwork().get(0);
            getPresenter().getRate(model.getAssetName(), selectNet);
        }
        binding.rvNet.getAdapter().notifyDataSetChanged();

        setTip();
        initListener();
        bindValid(binding.btSure, binding.address, binding.amount);
    }

    @Override
    public void setRate(RateBean rate) {
        selectRate = rate;
        changeByAmount();
    }


    /**
     * 扫描逻辑
     */
    public static final int REQUEST_CODE_SCAN = 1;
    public static final int REQUEST_CODE_SCAN_FRAGMENT = 11;
    public static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;

    public int mScanRequestCode = REQUEST_CODE_SCAN;

    public void scanQrCode(int i, EditText etAddressText) {
        this.fragmentEditText = etAddressText;
        this.mScanRequestCode = i;
        //检查版本是否大于M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CALL_CAMERA);
            } else {
                Intent intent = new Intent(WithdrawActivity.this, CaptureActivity.class);
                startActivityForResult(intent, mScanRequestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(WithdrawActivity.this, CaptureActivity.class);
                startActivityForResult(intent, mScanRequestCode);
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_CALL_CAMERA) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    ActivityCompat.shouldShowRequestPermissionRationale(WithdrawActivity.this, permissions[i]);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private EditText fragmentEditText;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                if (requestCode == REQUEST_CODE_SCAN) {
                    binding.address.setText(content);
                }
                if (requestCode == REQUEST_CODE_SCAN_FRAGMENT && this.fragmentEditText != null) {
                    fragmentEditText.setText(content);
                }
            }
        }
    }

    /**
     * 扫描逻辑
     */
}