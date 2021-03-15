package com.fota.android.core.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.fota.android.widget.popwin.CommomDialog;

/**
 * @author fjw
 * @date 2016/8/24
 */
public class DialogUtils {

    public static void showDialog(final Context context, DialogModel model) {
        if (context == null) {
            return;
        }
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return ;
        }
        CommomDialog dialog = new CommomDialog(context, model);
        dialog.show();
        //jiang
        dialog.setCanceledOnTouchOutside(model.canCancelOnTouchOutside);
    }

    public static CommomDialog getDialog(final Context context, DialogModel model) {
        if (context == null) {
            return null;
        }
        CommomDialog dialog = new CommomDialog(context, model);
        return dialog;
    }


//    public static AlertDialog getDialog(final Context context, DialogModel model) {
//        if (context == null) {
//            return null;
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setCancelable(model.isCancelable());
//
//        if (!TextUtils.isEmpty(model.getTitle())) {
//            builder.setTitle(model.getTitle());
//        } else {
//            builder.setTitle(context.getResources().getString(R.string.warm_prompt));
//        }
//        if (model.getView() != null) {
//            builder.setView(model.getView());
//        }
//
//        if (!TextUtils.isEmpty(model.getMessage())) {
//            builder.setMessage(model.getMessage());
//        }
//
//        if (TextUtils.isEmpty(model.getSureText())) {
//            model.setSureText(context.getResources().getString(R.string.sure));
//        }
//
//        if (model.getOnCancelListener() != null) {
//            builder.setOnCancelListener(model.getOnCancelListener());
//        }
//
//        if (model.getSureClickListen() != null) {
//            builder.setPositiveButton(model.getSureText(), model.getSureClickListen());
//        } else {
//            builder.setPositiveButton(model.getSureText(), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//        }
//
//        if (model.getCancelText() != null) {
//            builder.setNegativeButton(model.getCancelText(), model.getCancelClickListen());
//        } else {
//            builder.setNegativeButton(model.getCancelText(), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//        }
//
//        if (model.getOtherText() != null) {
//            builder.setNeutralButton(model.getOtherText(), model.getOtherClickListen());
//        } else {
//            builder.setNeutralButton(model.getOtherText(), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//        }
//        AlertDialog dialog = builder.create();
//        return dialog;
//    }

    public static View getDefaultStyleMsgTV(Context context, String msg) {
        if (context == null || msg == null)
            return null;
        TextView tvMsg = new TextView(context);
        tvMsg.setText(msg);
        tvMsg.setPadding(10, 10, 10, 10);
        tvMsg.setTextColor(Color.parseColor("#000000"));
        tvMsg.setGravity(Gravity.CENTER);
        tvMsg.setTextSize(18);
        return tvMsg;
    }
}



