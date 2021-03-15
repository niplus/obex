package com.fota.android.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.fota.android.R;
import com.fota.android.widget.btbwidget.ClearEdittext;
import com.fota.android.widget.btbwidget.FotaButton;

/**
 * google验证弹窗
 */
public class GoogleCheckDialog extends Dialog implements View.OnClickListener {

    private FotaButton submitTxt;
    private FotaButton cancelTxt;
    private ClearEdittext edittext;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    public GoogleCheckDialog(@NonNull Context context, onNoOnclickListener noClick, onYesOnclickListener yesClick) {
        super(context);
        noOnclickListener = noClick;
        yesOnclickListener = yesClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_google);


        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        submitTxt = (FotaButton) findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        cancelTxt = (FotaButton) findViewById(R.id.cancel);
        cancelTxt.setOnClickListener(this);
        edittext = findViewById(R.id.edt);
        cancelTxt.setButtonStyle(false);

    }

    @Override
    protected void onStart() {
        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.submit:
                yesOnclickListener.onYesClick(edittext.getText().toString());
                dismiss();
                break;
        }
    }


    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick(String googleCode);
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }


}
