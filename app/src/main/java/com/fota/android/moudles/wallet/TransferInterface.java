package com.fota.android.moudles.wallet;

/**
 *  划转的接口
 */
public interface TransferInterface {
    //最大划转
    public void allIn();

    //点击提交按钮
    public void submit();

    //服务端确认是否划转受限
    public void onConfirm();

    //最后 提交 到服务端
    public void finalDo();

    //需要弹出密码输入框
    public void pwdDialogShow();

    //finalDo之后，成功提交服务端，之后的弹窗提示与跳转
    public void afterTransferDialog();
}
