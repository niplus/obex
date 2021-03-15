package com.fota.android.widget.popwin;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BaseDialog;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.widget.btbwidget.FotaButton;
import com.fota.android.widget.btbwidget.FotaEditText;
import com.fota.android.widget.btbwidget.VCodeButton;

public class PasswordDialog extends BaseDialog {


    private View forgetFundCodeLayout;
    private TextView forgetFundCode;

    private View fundCodeLine;
    private EditText fundCode;
    private View fundCodeLayout;
    private FotaButton btSure;
    private CheckBox cbEyePsw;
    private TextView fundCodeError;

    private EditText ems_code;
    private VCodeButton sendEmsCode;
    private View emsTip;
    private View emsLayout;
    private View emsLine;
    private TextView emsError;


    private View googleTip;
    private FotaEditText googleEdit;
    private View googleLine;
    private TextView googleError;

    boolean isShowFundCode = true;
    boolean isShowEmsCode = false;
    boolean isShowGoogleCode = false;


    public PasswordDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int width = UIUtil.getScreenWidth(getContext());
        setContentView(R.layout.dialog_password);
        initView();

    }




    private void initView() {
        //忘记资金密码
        forgetFundCodeLayout = findViewById(R.id.forget_fund_code_layout);
        forgetFundCode = (TextView) findViewById(R.id.forget_fund_code);
        forgetFundCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.CapitalForgetFragment);
            }
        });


        //资金密码
        fundCodeLayout = findViewById(R.id.fund_code_layout);
        fundCodeLine = findViewById(R.id.fund_code_line);
        fundCode = (EditText) findViewById(R.id.fund_code);
        fundCodeError = (TextView) findViewById(R.id.fund_code_error);


        btSure = (FotaButton) findViewById(R.id.bt_sure);
        cbEyePsw = (CheckBox) findViewById(R.id.cb_eye_psw);

        //短信模块
        emsTip = findViewById(R.id.ems_tip);
        emsLayout = findViewById(R.id.ems_layout);
        emsLine = findViewById(R.id.ems_line);
        ems_code = (EditText) findViewById(R.id.ems_code);
        emsError = (TextView) findViewById(R.id.ems_error);

        sendEmsCode = (VCodeButton) findViewById(R.id.send_ems_code);
        sendEmsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmsCode.sendReq();
            }
        });


        //谷歌验证码
        googleTip = findViewById(R.id.google_tip);
        googleEdit = findViewById(R.id.google_edit);
        googleLine = findViewById(R.id.google_line);
        googleError = (TextView) findViewById(R.id.google_error);

        View outside = findViewById(R.id.outside);

        outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        //确定
        btSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fundCodeStr = fundCode.getText().toString();
                if (listener != null) {
                    listener.onClick(fundCodeStr);
                }
                if (moreListener != null) {
                    String ems = ems_code.getText().toString();
                    String googlePassword = googleEdit.getText().toString();
                    moreListener.onClick(fundCodeStr, ems, googlePassword);
                }

                dismiss();
            }
        });

        //
        btSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fundCodeStr = fundCode.getText().toString();
                if (listener != null) {
                    listener.onClick(fundCodeStr);
                }
                if (moreListener != null) {
                    String ems = ems_code.getText().toString();
                    String googlePassword = googleEdit.getText().toString();
                    moreListener.onClick(fundCodeStr, ems, googlePassword);
                }

                dismiss();
            }
        });


        UIUtil.bindPassWord(cbEyePsw, fundCode);

        bindValid(btSure, fundCode, ems_code, googleEdit);

        refreshView();
        setCancelable(true);

    }


    @Override
    protected boolean customerValid() {
        boolean isOk = true;
        if (isShowFundCode) {
            if (!Pub.isStringEmpty(fundCode.getText().toString())
                    && fundCode.getText().toString().length() < 6) {
                fundCodeError.setText(R.string.common_fund_code_need);
                isOk = false;
            } else {
                fundCodeError.setText("");
            }
        }

        if (isShowEmsCode) {
            if (!Pub.isStringEmpty(ems_code.getText().toString())
                    && ems_code.getText().toString().length() < 6) {
                emsError.setText(R.string.common_ems_code_need);
                isOk = false;
            } else {
                emsError.setText("");
            }
        }

        if (isShowGoogleCode) {
            if (!Pub.isStringEmpty(googleEdit.getText().toString())
                    && googleEdit.getText().toString().length() < 6) {
                googleError.setText(R.string.common_google_need);
                isOk = false;
            } else {
                googleError.setText("");
            }
        }
        return isOk;
    }

    public void refreshView() {
        UIUtil.setVisibility(fundCode, isShowFundCode);
        UIUtil.setVisibility(fundCodeLine, isShowFundCode);
        UIUtil.setVisibility(fundCodeLayout, isShowFundCode);
        UIUtil.setVisibility(forgetFundCodeLayout, isShowFundCode);
        UIUtil.setVisibility(forgetFundCodeLayout, isShowFundCode);


        UIUtil.setVisibility(ems_code, isShowEmsCode);
        UIUtil.setVisibility(emsLayout, isShowEmsCode);
        UIUtil.setVisibility(emsLine, isShowEmsCode);
        UIUtil.setVisibility(emsTip, isShowEmsCode);
        UIUtil.setVisibility(emsError, isShowEmsCode);

        UIUtil.setVisibility(googleEdit, isShowGoogleCode);
        UIUtil.setVisibility(googleLine, isShowGoogleCode);
        UIUtil.setVisibility(googleTip, isShowGoogleCode);
        UIUtil.setVisibility(googleError, isShowGoogleCode);

    }

    OnSureClickListener listener;

    OnMoreSureClickListener moreListener;


    public void setListener(OnSureClickListener listener) {
        this.listener = listener;
    }

    public void setMoreListener(OnMoreSureClickListener listener) {
        this.moreListener = listener;
    }


    public interface OnMoreSureClickListener {
        void onClick(String fundCode, String ems, String google);
    }

    public interface OnSureClickListener {
        void onClick(String fundCode);
    }


    public void setShowFundCode(boolean showFundCode) {
        isShowFundCode = showFundCode;

    }

    public void setShowEmsCode(boolean showEmsCode) {
        isShowEmsCode = showEmsCode;

    }

    public void setShowGoogleCode(boolean showGoogleCode) {
        isShowGoogleCode = showGoogleCode;
    }
}