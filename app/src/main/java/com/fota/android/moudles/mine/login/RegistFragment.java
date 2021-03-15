package com.fota.android.moudles.mine.login;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.LogUtils;
import com.fota.android.R;
import com.fota.android.app.Constants;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.MD5Utils;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.base.BaseView;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentRegistBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.login.bean.CounrtyAreasBean;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 注册账号
 */
public class RegistFragment extends BaseFragment implements View.OnClickListener, BaseView {

    private FragmentRegistBinding mBinding;
    private static int TYPE_EMAIL = 1;
    private static int TYPE_PHONE = 0;
    private int TYPE = TYPE_PHONE;

    CounrtyAreasBean.Area countryArea = null;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_regist_ft:
                String account = TYPE == TYPE_PHONE ? mBinding.edtAccountPhone.getText().toString().replace(" ", "") : mBinding.edtAccountEmail.getText().toString().replace(" ", "");
                String psw = mBinding.edtPasword.getText().toString();
                String psw2 = mBinding.edtRepassword.getText().toString();
                String vcode = mBinding.edtVcode.getText().toString();
                String invitationCode = mBinding.edtInvitation.getText().toString();
                if (TYPE == TYPE_EMAIL) {
                    if (!StringFormatUtils.checkEmail(account)) {
                        showToast(R.string.common_email_notlegal);
                        return;
                    }
                }
                if (!psw.equals(psw2)) {
                    showToast(mContext.getResources().getString(R.string.regist_psw2_notmatch));
                    return;
                }
                if (!StringFormatUtils.checkPswLength(psw)) {
                    showToast(mContext.getResources().getString(R.string.regist_psw_lengtherror));
                    return;
                }
                int agree = mBinding.cbReadme.isChecked() ? 1 : 0;
                psw = MD5Utils.sha256Psw(psw);
                psw2 = MD5Utils.sha256Psw(psw2);
                getRegist(TYPE, account, account, psw, psw2, vcode, countryArea.getCode(), countryArea.getKey(), invitationCode, agree);
                break;
            case R.id.tv_getvcode:
                String account2 = TYPE == TYPE_PHONE ? mBinding.edtAccountPhone.getText().toString().replace(" ", "") : mBinding.edtAccountEmail.getText().toString().replace(" ", "");

                if (TextUtils.isEmpty(account2)) {
                    if (TYPE == TYPE_EMAIL) {
                        showToast(mContext.getString(R.string.login_email_hint));
                    } else if (TYPE == TYPE_PHONE) {
                        showToast(mContext.getString(R.string.phone_notinput));
                    }
                    return;
                }
                if (TYPE == TYPE_EMAIL) {
                    if (!StringFormatUtils.checkEmail(account2)) {
                        showToast(R.string.common_email_notlegal);
                        return;
                    }
                }
                getVcode(TYPE, countryArea.getCode(), 1, account2);
                break;
            case R.id.tv_country_phone:
                Bundle bundle = new Bundle();
                bundle.putString("from", "regist");
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.CheckCountryFragment, bundle);
                break;
            case R.id.tv_fwtk:
                FtRounts.toWebView(getContext(), mContext.getResources().getString(R.string.regist_fwtk), AppConfigs.isChinaLanguage() ? Constants.URL_FWTK_CH : Constants.URL_FWTK_EN);

                break;
            case R.id.tv_mzsm:
                FtRounts.toWebView(getContext(), mContext.getResources().getString(R.string.regist_mzsm), AppConfigs.isChinaLanguage() ? Constants.URL_MZSM_CH : Constants.URL_MZSM_EN);

                break;
            case R.id.tv_ysbh:
                FtRounts.toWebView(getContext(), mContext.getResources().getString(R.string.regist_ysbh), AppConfigs.isChinaLanguage() ? Constants.URL_YSBH_CH : Constants.URL_YSBH_EN);

                break;
            case R.id.ll_phone_uncheck:
                if (TYPE == TYPE_EMAIL) {
                    typeChange();
                }
                break;
            case R.id.ll_email_uncheck:
                if (TYPE == TYPE_PHONE) {
                    typeChange();
                }
                break;
            case R.id.ll_back:
            case R.id.ll_login:
                finish();
                break;

            default:
                break;
        }
    }


    @Override
    protected boolean viewGroupFocused() {
        return false;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        setJustWhiteBarTxt();
        bindValid(mBinding.btnRegistFt, mBinding.edtAccountEmail, mBinding.edtAccountPhone, mBinding.edtVcode, mBinding.edtPasword, mBinding.edtRepassword, mBinding.cbReadme);
        valid();
        edtFocus(mBinding.edtAccountPhone, mBinding.viewAccountPhone);
        edtFocus(mBinding.edtAccountEmail, mBinding.viewAccountEmail);
        edtFocus(mBinding.edtPasword, mBinding.viewPsw);
        edtFocus(mBinding.edtRepassword, mBinding.viewPsw2);
        edtFocus(mBinding.edtVcode, mBinding.viewCode);
        edtHide(mBinding.edtPasword, mBinding.cbEyePsw);
        edtHide(mBinding.edtRepassword, mBinding.cbEyePsw2);
        edtFocus(mBinding.edtInvitation, mBinding.viewInvitation);
        countryArea = new CounrtyAreasBean.Area();
        countryArea.setCode("86");
        countryArea.setName_en("China");
        countryArea.setName_zh(mContext.getResources().getString(R.string.china));
        countryArea.setKey("CN");
        if (AppConfigs.getLanguegeInt() == 1) {
            mBinding.tvFwtk.setText(mContext.getResources().getString(R.string.regist_fwtk) + ",");
            mBinding.tvMzsm.setText(mContext.getResources().getString(R.string.regist_mzsm) + ",");
        }
    }

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_regist, container, false);
        mBinding.btnRegistFt.setOnClickListener(this);
        mBinding.llPhoneUncheck.setOnClickListener(this);
        mBinding.llEmailUncheck.setOnClickListener(this);
        mBinding.tvGetvcode.setOnClickListener(this);
        mBinding.tvCountryPhone.setOnClickListener(this);
        mBinding.llLogin.setOnClickListener(this);
        mBinding.tvMzsm.setOnClickListener(this);
        mBinding.tvFwtk.setOnClickListener(this);
        mBinding.tvYsbh.setOnClickListener(this);
        mBinding.llBack.setOnClickListener(this);
        mBinding.edtPasword.addTextChangedListener(new PswTextWatcher());
        if (AppConfigs.getTheme() == 0) {
            mBinding.rlParent.setBackgroundColor(Pub.getColor(mContext, R.attr.bg_color));
        } else {
            mBinding.rlParent.setBackgroundColor(Pub.getColor(mContext, R.attr.main_color));
            mBinding.edtAccountPhone.setTextColor(Pub.getColor(mContext, R.attr.main_color));
            mBinding.edtAccountEmail.setTextColor(Pub.getColor(mContext, R.attr.main_color));
            mBinding.edtPasword.setTextColor(Pub.getColor(mContext, R.attr.main_color));
            mBinding.edtRepassword.setTextColor(Pub.getColor(mContext, R.attr.main_color));
            mBinding.edtVcode.setTextColor(Pub.getColor(mContext, R.attr.main_color));
        }


        return mBinding.getRoot();
    }


    /**
     * 邮箱和手机按钮切换
     */
    private void typeChange() {
        mBinding.edtPasword.setText("");
        mBinding.edtRepassword.setText("");
        mBinding.edtVcode.setText("");
        if (TYPE == TYPE_PHONE) {
            mBinding.edtAccountEmail.setVisibility(View.VISIBLE);
            mBinding.edtAccountPhone.setVisibility(View.GONE);
            mBinding.rlEmail.setVisibility(View.VISIBLE);
            mBinding.rlPhone.setVisibility(View.GONE);
            mBinding.edtVcode.setHint(R.string.regist_vcode_hint);
            mBinding.tvCountryPhone.setVisibility(View.GONE);
            mBinding.llPhoneCheck.setVisibility(View.GONE);
            mBinding.llPhoneUncheck.setVisibility(View.VISIBLE);
            mBinding.llEmailCheck.setVisibility(View.VISIBLE);
            mBinding.llEmailUncheck.setVisibility(View.GONE);
            TYPE = TYPE_EMAIL;
        } else {
            mBinding.edtAccountEmail.setVisibility(View.GONE);
            mBinding.edtAccountPhone.setVisibility(View.VISIBLE);
            mBinding.rlEmail.setVisibility(View.GONE);
            mBinding.rlPhone.setVisibility(View.VISIBLE);
            mBinding.edtVcode.setHint(R.string.regist_vcode_hint);
            mBinding.tvCountryPhone.setVisibility(View.VISIBLE);
            mBinding.llPhoneCheck.setVisibility(View.VISIBLE);
            mBinding.llPhoneUncheck.setVisibility(View.GONE);
            mBinding.llEmailCheck.setVisibility(View.GONE);
            mBinding.llEmailUncheck.setVisibility(View.VISIBLE);
            TYPE = TYPE_PHONE;
        }
    }


    /*type
        0-phone，1-mail
            email
    String
            邮箱
    phone
            String
    电话
            pwd
    String
            密码
    repwd
            String
    重复密码
            capcha
    String
    验证码 手机就是手机的 邮箱就是邮箱的
            country
    String
    国家编码 手机的时候用
    countryKey
            String
    国家标志
            agreeServiceRule
    Number
0 不同意 1 同意*/

    /**
     * 注册接口
     */
    private void getRegist(int type, String email, String phone, String pwd, String repwd, String capcha, String country, String countryKey, String invitationCode, int agreeServiceRule) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        if (type == TYPE_EMAIL) {
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("passwdSecurityLevel", StringFormatUtils.getStengthParam(pwd, email));
        } else {
            jsonObject.addProperty("phone", phone);
            jsonObject.addProperty("passwdSecurityLevel", StringFormatUtils.getStengthParam(pwd, phone));

        }
        jsonObject.addProperty("pwd", pwd);
        jsonObject.addProperty("repwd", repwd);
        jsonObject.addProperty("capcha", capcha);
        jsonObject.addProperty("country", country);
        jsonObject.addProperty("countryKey", countryKey);
        jsonObject.addProperty("agreeServiceRule", agreeServiceRule);
        jsonObject.addProperty("agreeServiceRule", agreeServiceRule);
        if (!TextUtils.isEmpty(invitationCode)) {
            jsonObject.addProperty("invitationCode", invitationCode);
        }
        Http.getHttpService().regist(jsonObject)
                .compose(new CommonTransformer<Object>())
                .subscribe(new CommonSubscriber<Object>(this) {
                    @Override
                    public void onNext(Object loginBean) {
                        LogUtils.e("regist", " suc ---" + loginBean.toString());
                        showToast(getContext().getResources().getString(R.string.regist_suc));
                        if (TYPE == TYPE_EMAIL) {
                            UserLoginUtil.saveLoginedAccount(mBinding.edtAccountEmail.getText().toString().replace(" ", ""));
                        } else {
                            UserLoginUtil.saveLoginedAccount(mBinding.edtAccountPhone.getText().toString().replace(" ", ""));
                        }
                        EventWrapper.post(Event.create(R.id.event_regist_setaccount));
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                finish();
                            }

                        }, 2000);

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("regist", " fail ---");

                    }
                });

    }


    /* verifyType
             Number
 2
     验证类型 0-邮箱；1-手机号
             phoneCountryCode
     String
             CN
     手机号国家码，如果是手机会需要
             templateType
     Number
 1
     短信或邮箱模版类型（测试统一 ：templateType = 1）
     account
             String
 18858110927
     账号*/
    private void getVcode(int verifyType, String phoneCountryCode, int templateType, String account) {
        int type = 0;
        if (verifyType == TYPE_EMAIL) {
            type = 1;
        } else {
            type = 2;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("verifyType", type);
        jsonObject.addProperty("phoneCountryCode", phoneCountryCode);
        jsonObject.addProperty("templateType", templateType);
        jsonObject.addProperty("account", account);
        Http.getHttpService().getVcode(jsonObject).compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        LogUtils.e("regist", " vcode suc ---");
                        mBinding.tvGetvcode.startPhone();
                        showToast(mContext.getResources().getString(R.string.common_send_suc));
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        LogUtils.e("regist", "vcode fail fail ---" + e.toString());
                    }
                });
    }


    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_regist_countrycheck:
                CounrtyAreasBean.Area a = event.getParam(CounrtyAreasBean.Area.class);
                if (a != null) {
                    countryArea = a;
                }
                mBinding.tvCountryPhone.setText("+" + countryArea.getCode());
                break;
        }
    }

    /**
     * 密码输入框强弱提示
     */
    private class PswTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s)) {
                mBinding.tvPassType.setVisibility(View.GONE);
                return;
            }
            String account = TYPE == TYPE_PHONE ? mBinding.edtAccountPhone.getText().toString().replace(" ", "") : mBinding.edtAccountEmail.getText().toString().replace(" ", "");
            if (StringFormatUtils.checkStrengthISHeigh(s.toString(), account) == 0) {
                mBinding.tvPassType.setVisibility(View.VISIBLE);
                mBinding.tvPassType.setText(R.string.password_strength_low);
            } else if (StringFormatUtils.checkStrengthISHeigh(s.toString(), account) == 1) {
                mBinding.tvPassType.setVisibility(View.VISIBLE);
                mBinding.tvPassType.setText(R.string.password_strength_low2);
            } else if (StringFormatUtils.checkStrengthISHeigh(s.toString(), account) == 2) {
                mBinding.tvPassType.setVisibility(View.VISIBLE);
                mBinding.tvPassType.setText(R.string.password_strength_heigh2);
            } else if (StringFormatUtils.checkStrengthISHeigh(s.toString(), account) >= 3) {
                mBinding.tvPassType.setVisibility(View.VISIBLE);
                mBinding.tvPassType.setText(R.string.password_strength_heigh);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

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
}
