package com.fota.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.base.MyActivityManager;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.moudles.main.MainActivity;
import com.fota.android.moudles.main.bean.PushBean;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

public class JGReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onReceive(Context context, Intent intent) {
        L.i("[MyReceiver] onReceive - " + intent.getAction() + ", extras:  start");

        try {
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                L.a("[MyReceiver] onReceive - " + intent.getAction() + ", extras:  start");
                //send the Registration Id to your server...
                UserLoginUtil.setJpushTag();

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
                Bundle bundle1 = intent.getExtras();
                String msg2 = bundle1.getString("cn.jpush.android.EXTRA");
                if (TextUtils.isEmpty(msg2))
                    return;
                PushBean pushBean = new Gson().fromJson(msg2, PushBean.class);
                if (CommonUtils.isAppForeground(FotaApplication.getInstance())) {
                    if (pushBean != null) {
                        showDialog(context, pushBean);
                        JPushInterface.clearNotificationById(FotaApplication.getInstance(), notifactionId);
                    }
                }

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
                Bundle bundle2 = intent.getExtras();
                String msg2 = bundle.getString("cn.jpush.android.EXTRA");
                if (TextUtils.isEmpty(msg2))
                    return;
                PushBean pushBean = new Gson().fromJson(msg2, PushBean.class);
                if (pushBean != null && !TextUtils.isEmpty(pushBean.getGotoUrl())) {
                    if (MyActivityManager.getInstance().isOpenActivity(MainActivity.class)) {
                        FtRounts.getPageFromH5(context, pushBean.getGotoUrl());
                    } else {
                        Bundle mainbundle = new Bundle();
                        mainbundle.putString("pushTo", pushBean.getGotoUrl());
                        FtRounts.toMain(context, ConstantsPage.MainActivity, mainbundle, false);

                    }
//                        SimpleFragmentActivity.gotoFragmentActivity(context, NoticeCenterFragment.class);
                }

//                //打开自定义的Activity
//                Intent i = new Intent(context, TestActivity.class);
//                i.putExtras(bundle);
//                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                context.startActivity(i);

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
//                if (connected == true) {
//                    UserLoginUtil.setJpushTag();
//                }
                UserLoginUtil.setJpushTag();
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras:  error");

        }

    }

    private void showDialog(final Context context, final PushBean pushBean) {
        if (MyActivityManager.getInstance().getCurrentActivity() == null) {
            return;
        }
        if (MyActivityManager.getInstance().getCurrentActivity().isFinishing()) {
            return;
        }
        DialogUtils.showDialog(MyActivityManager.getInstance().getCurrentActivity(), new DialogModel()
                        .setMessage(pushBean.getContent())
                        .setTitle(TextUtils.isEmpty(pushBean.getTitle()) ? context.getResources().getString(R.string.notific_title) : pushBean.getTitle())
                        .setSureText(context.getResources().getString(R.string.common_jump))
                        .setCancelText(context.getResources().getString(R.string.cancel))
                        .setSureClickListen(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FtRounts.getPageFromH5(context, pushBean.getGotoUrl());
                                dialogInterface.dismiss();
//                        SimpleFragmentActivity.gotoFragmentActivity(context, NoticeCenterFragment.class);
                            }
                        }).setCancelClickListen(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
        );
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
//        if (MainActivity.isForeground) {
//            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//
//                }
//
//            }
//            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//        }
    }
}

