package com.fota.android.widget.popwin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fota.android.R;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.widget.btbwidget.FotaButton;

public class CommomDialog extends Dialog implements View.OnClickListener {

    private DialogModel model;
    private TextView contentTxt;
    private TextView titleTxt;
    private FotaButton submitTxt;
    private FotaButton cancelTxt;
    private LinearLayout ll_submit;
    private LinearLayout ll_cancel;
    private LinearLayout ll_surecancle;
    private ImageView icon;
    //jiang

    public CommomDialog(Context context, DialogModel model) {
        super(context, R.style.matchDialog);
        this.model = model;
    }

    @Override
    protected void onStart() {
        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common);
        initView();
    }

    private void initView() {
        ll_surecancle = (LinearLayout) findViewById(R.id.ll_surecancle);
        ll_submit = (LinearLayout) findViewById(R.id.ll_submit);
        ll_cancel = (LinearLayout) findViewById(R.id.ll_cancel);
        contentTxt = (TextView) findViewById(R.id.content);
        titleTxt = (TextView) findViewById(R.id.title);
        submitTxt = (FotaButton) findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        cancelTxt = (FotaButton) findViewById(R.id.cancel);
        icon = (ImageView) findViewById(R.id.icon);


        cancelTxt.setOnClickListener(this);
        contentTxt.setText(model.getMessage());

        submitTxt.setButtonStyle(true);
        cancelTxt.setButtonStyle(false);

        if (!TextUtils.isEmpty(model.getSureText())) {
            submitTxt.setText(model.getSureText());
        } else {
            ll_submit.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(model.getCancelText())) {
            cancelTxt.setText(model.getCancelText());
        } else {
            ll_cancel.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(model.getTitle())) {
            titleTxt.setText(model.getTitle());
        }
        UIUtil.setVisibility(titleTxt, !TextUtils.isEmpty(model.getTitle()));

        if (TextUtils.isEmpty(model.getSureText()) && TextUtils.isEmpty(model.getCancelText())) {
            ll_surecancle.setVisibility(View.GONE);
        }

        View view = findViewById(R.id.dialog_root);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.isCanCancelOnTouchOutside())
                    dismiss();
            }
        });
        setCancelable(model.isCancelable());
        if (model.getGravityCenter() != -1) {
            contentTxt.setGravity(model.getGravityCenter());
        }
        if (model.getIcon() > 0) {
            icon.setVisibility(View.VISIBLE);
            icon.setImageResource(model.getIcon());
        } else {
            icon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                if (model != null && model.getCancelClickListen() != null) {
                    model.getCancelClickListen().onClick(this, 0);
                }
                if (model.isClickAutoDismiss())
                    dismiss();
                break;
            case R.id.submit:
                if (model != null) {
                    model.getSureClickListen().onClick(this, 0);
                }
                if (model.isClickAutoDismiss())
                    dismiss();
                break;
        }
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm);
    }

    @Override
    public void show() {
        super.show();
    }
}