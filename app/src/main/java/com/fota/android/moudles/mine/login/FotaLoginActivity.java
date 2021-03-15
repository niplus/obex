package com.fota.android.moudles.mine.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.app.MD5Utils;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.MvpActivity;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.ActivityLoginFotaBinding;
import com.fota.android.moudles.mine.login.bean.LoginBean;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.utils.apputils.MineInfoUtil;
import com.fota.android.widget.TitleLayout;
import com.fota.android.widget.dialog.GoogleCheckDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * sunchao
 * 登陆功能
 */
public class FotaLoginActivity extends MvpActivity<FtLoginPresenter> implements View.OnClickListener, FotaILoginView {

    //    activityl
    ActivityLoginFotaBinding mBinding;
    String loginTo = "";//标记跳转到的页面
    TitleLayout mTitleLayout;

    private static int TYPE_EMAIL = 1;
    private static int TYPE_PHONE = 0;
    private int TYPE = TYPE_PHONE;


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_ft:
                loginClick("");
                break;
            case R.id.ll_regist:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.RegistFragment);

                break;
            case R.id.tv_forget:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.FTForgetpasswordFragment);
                break;
            case R.id.tv_country:
                Bundle bundle = new Bundle();
                bundle.putString("from", "login");
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.CheckCountryFragment, bundle);

                break;
            case R.id.ll_phone_uncheck:
                if (TYPE == TYPE_EMAIL) {
                    TYPE = TYPE_PHONE;
                    mBinding.edtAccountPhone.setText("");
                    typeChange();
                }
                break;
            case R.id.ll_email_uncheck:
                if (TYPE == TYPE_PHONE) {
                    TYPE = TYPE_EMAIL;
                    mBinding.edtAccountEmail.setText("");
                    typeChange();
                }
                break;
            case R.id.ll_back:
                onBackPressed();
                break;

            default:
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_fota);
        mBinding.btnLoginFt.setOnClickListener(this);
        mBinding.llRegist.setOnClickListener(this);
        mBinding.tvForget.setOnClickListener(this);
        mBinding.llEmailUncheck.setOnClickListener(this);
        mBinding.llPhoneUncheck.setOnClickListener(this);
        mBinding.llBack.setOnClickListener(this);
//        mTitleLayout = findViewById(R.id.app_title_layout);
//        mTitleLayout.setOnLeftButtonClickListener(new TitleLayout.OnLeftButtonClickListener() {
//            @Override
//            public void onLeftButtonClick(View v) {
//                finish();
//            }
//        });

        bindValid(mBinding.btnLoginFt, mBinding.edtAccountEmail, mBinding.edtPassword, mBinding.edtAccountPhone);
        valid();
        edtFocus(mBinding.edtAccountPhone, mBinding.viewAccountPhone);
        edtFocus(mBinding.edtAccountEmail, mBinding.viewAccountEmail);
        edtFocus(mBinding.edtPassword, mBinding.viewPsw);
        edtHide(mBinding.edtPassword, mBinding.cbEyePsw);
        setDefaultPhone();
        if (AppConfigs.getTheme() == 0) {
            mBinding.rlParent.setBackgroundColor(Pub.getColor(this, R.attr.bg_color));
        } else {
            mBinding.rlParent.setBackgroundColor(Pub.getColor(this, R.attr.main_color));
            mBinding.edtAccountPhone.setTextColor(Pub.getColor(this, R.attr.main_color));
            mBinding.edtAccountEmail.setTextColor(Pub.getColor(this, R.attr.main_color));
            mBinding.edtPassword.setTextColor(Pub.getColor(this, R.attr.main_color));
        }
        setJustWhiteBarTxt();

    }

    @Override
    protected FtLoginPresenter createPresenter() {
        return new FtLoginPresenter(this);
    }


    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        if (bundle != null)
            loginTo = bundle.getString(BundleKeys.LOGIN_JUMPKEY, "");
    }

    @Override
    public void loginSuccess(LoginBean loginBean) {
        KeyBoardUtils.closeKeybord(getContext());
        if (loginBean != null)
            UserLoginUtil.saveUser(loginBean);

        FotaApplication.setLoginStatus(true);
        if (TYPE == TYPE_EMAIL) {
            UserLoginUtil.saveLoginedAccount(mBinding.edtAccountEmail.getText().toString().replace(" ", ""));
        } else {
            UserLoginUtil.saveLoginedAccount(mBinding.edtAccountPhone.getText().toString().replace(" ", ""));
        }
//        EventWrapper.post(Event.create(R.id.event_main_changelanguage));
        EventWrapper.post(Event.create(R.id.mine_refresh));//通知我的页面更新
        EventWrapper.post(Event.create(R.id.login_quicktoast));//通知弹出设置快速登录提示
        if (ConstantsPage.SafeSettingFragment.equals(loginTo) && !UserLoginUtil.haveQuickLogin()) {
            loginTo = "";
        }
        SimpleFragmentActivity.gotoFragmentActivity(this, loginTo);
        MineInfoUtil.getMindeMsg();
        showToast(R.string.login_suc);
        finish();

    }

    @Override
    public void loginFail(ApiException e) {
        Log.d("fata", "----------fail");
//        showToast("登录失败");
    }

    @Override
    public void needGoogle() {
        showGoogleDialog();
    }


    /**
     * 设置上次登录账号
     */
    private void setDefaultPhone() {
        String account = UserLoginUtil.getLoginedAccount();
        if (TextUtils.isEmpty(account)) {
            TYPE = TYPE_PHONE;
        } else if (account.contains("@")) {//邮箱
            TYPE = TYPE_EMAIL;
            mBinding.edtAccountEmail.setText(account);
        } else {//手机
            TYPE = TYPE_PHONE;
            mBinding.edtAccountPhone.setText(account);
        }
        typeChange();

    }

    @Override
    public void aotoLoginFromReq() {
        //super.aotoLoginFromReq();
    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_regist_setaccount:
                setDefaultPhone();
                break;
        }
    }

    /**
     * 邮箱和手机按钮切换
     */
    private void typeChange() {
        mBinding.edtPassword.setText("");
        if (TYPE == TYPE_EMAIL) {
            mBinding.edtAccountEmail.setVisibility(View.VISIBLE);
            mBinding.edtAccountPhone.setVisibility(View.GONE);
            mBinding.rlEmail.setVisibility(View.VISIBLE);
            mBinding.rlPhone.setVisibility(View.GONE);
            mBinding.llPhoneCheck.setVisibility(View.GONE);
            mBinding.llPhoneUncheck.setVisibility(View.VISIBLE);
            mBinding.llEmailCheck.setVisibility(View.VISIBLE);
            mBinding.llEmailUncheck.setVisibility(View.GONE);
//            TYPE = TYPE_EMAIL;
        } else {
            mBinding.edtAccountEmail.setVisibility(View.GONE);
            mBinding.edtAccountPhone.setVisibility(View.VISIBLE);
            mBinding.rlEmail.setVisibility(View.GONE);
            mBinding.rlPhone.setVisibility(View.VISIBLE);
            mBinding.llPhoneCheck.setVisibility(View.VISIBLE);
            mBinding.llPhoneUncheck.setVisibility(View.GONE);
            mBinding.llEmailCheck.setVisibility(View.GONE);
            mBinding.llEmailUncheck.setVisibility(View.VISIBLE);

//            TYPE = TYPE_PHONE;
        }
    }

    /**
     * 选中输入框下划线加粗
     *
     * @param editText
     * @param lineView
     */
    @Override
    public void edtFocus(EditText editText, final View lineView) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (AppConfigs.getTheme() == 0) {
                        lineView.setBackgroundColor(Pub.getColor(getContext(), R.attr.line_color_focus));
                        lineView.setBackgroundColor(getResources().getColor(R.color.font_color_black));

                    } else {
                        lineView.setBackgroundColor(getResources().getColor(R.color.main_color_white));
                    }
                } else {
                    lineView.setBackgroundColor(Pub.getColor(getContext(), R.attr.line_color));
                }
            }
        });
    }

    /**
     * 显示google弹窗
     */
    private void showGoogleDialog() {
        if (isFinishing()) {
            return ;
        }
        new GoogleCheckDialog(this, new GoogleCheckDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {

            }
        }, new GoogleCheckDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String googleCode) {
                loginClick(googleCode);
            }
        }).show();
    }

    private void loginClick(String googleCode) {
        String account = "";
        if (TYPE == TYPE_EMAIL) {
            account = mBinding.edtAccountEmail.getText().toString().replace(" ", "");
        } else {
            account = mBinding.edtAccountPhone.getText().toString().replace(" ", "");
        }
        if (TYPE == TYPE_EMAIL) {
            if (!StringFormatUtils.checkEmail(account)) {
                showToast(R.string.common_email_notlegal);
                return;
            }
        }
        String psw = mBinding.edtPassword.getText().toString();
        psw = MD5Utils.sha256Psw(psw);
        getPresenter().login(account, psw, googleCode);
    }
}
