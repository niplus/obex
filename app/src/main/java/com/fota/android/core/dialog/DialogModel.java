package com.fota.android.core.dialog;

import android.content.DialogInterface;
import android.view.View;

/**
 * @author fjw
 * @date 2017/11/9
 */
public class DialogModel {

    public DialogModel() {
    }

    /**
     * 只是提示 没有消息
     *
     * @param message
     */
    public DialogModel(String message) {
        setMessage(message);
    }

    /**
     * 只是提示
     * 确定有事件
     *
     * @param message
     * @param sureClickListen
     */
    public DialogModel(String message, DialogInterface.OnClickListener sureClickListen) {
        setMessage(message);
        setSureClickListen(sureClickListen);
    }

    String title;

    String layoutID;

    View view;

    String message;

    DialogInterface.OnClickListener sureClickListen;

    DialogInterface.OnClickListener cancelClickListen;

    DialogInterface.OnClickListener otherClickListen;

    //DialogInterface.OnCancelListener mOnCancelListener;

    String sureText;

    String cancelText;

    String otherText;

    int icon;

    /**
     * 根据他默认的来吧  默认是可以取消的
     */
    boolean cancelable = true;

    boolean canCancelOnTouchOutside = true;

    boolean clickAutoDismiss = true; //默认点击按钮弹窗自动取消
    int gravityCenter = -1; //是否居中

    public boolean isCanCancelOnTouchOutside() {
        return canCancelOnTouchOutside;
    }

    public DialogModel setCanCancelOnTouchOutside(boolean canCancelOnTouchOutside) {
        this.canCancelOnTouchOutside = canCancelOnTouchOutside;
        return this;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public DialogModel setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public DialogModel setMessage(String message) {
        this.message = message;
        return this;
    }


//    public DialogInterface.OnCancelListener getOnCancelListener() {
//        return mOnCancelListener;
//    }
//
//    public DialogModel setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
//        mOnCancelListener = onCancelListener;
//        return this;
//    }

    public View getView() {
        return view;
    }

    public DialogModel setView(View view) {
        this.view = view;
        return this;
    }

    public String getSureText() {
        return sureText;
    }

    public DialogModel setSureText(String sureText) {
        this.sureText = sureText;
        return this;
    }

    public String getCancelText() {
        return cancelText;
    }

    public DialogModel setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public String getOtherText() {
        return otherText;
    }

    public DialogModel setOtherText(String otherText) {
        this.otherText = otherText;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public DialogModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getLayoutID() {
        return layoutID;
    }

    public DialogModel setLayoutID(String layoutID) {
        this.layoutID = layoutID;
        return this;
    }

    public DialogInterface.OnClickListener getSureClickListen() {
        return sureClickListen;
    }

    public DialogModel setSureClickListen(DialogInterface.OnClickListener sureClickListen) {
        this.sureClickListen = sureClickListen;
        return this;
    }

    public DialogInterface.OnClickListener getCancelClickListen() {
        return cancelClickListen;
    }

    public DialogModel setCancelClickListen(DialogInterface.OnClickListener cancelClickListen) {
        this.cancelClickListen = cancelClickListen;
        return this;
    }

    public DialogInterface.OnClickListener getOtherClickListen() {
        return otherClickListen;
    }

    public DialogModel setOtherClickListen(DialogInterface.OnClickListener otherClickListen) {
        this.otherClickListen = otherClickListen;
        return this;
    }

    public boolean isClickAutoDismiss() {
        return clickAutoDismiss;
    }

    public DialogModel setClickAutoDismiss(boolean clickAutoDismiss) {
        this.clickAutoDismiss = clickAutoDismiss;
        return this;
    }

    public int getGravityCenter() {
        return gravityCenter;
    }

    public DialogModel setGravityCenter(int gravityCenter) {
        this.gravityCenter = gravityCenter;
        return this;
    }

    public DialogModel setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public int getIcon() {
        return icon;
    }
}