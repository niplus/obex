package com.fota.android.core.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.utils.UserLoginUtil;


public class SimpleFragmentActivity extends BaseActivity {

    private Bundle args;

    public static void gotoFragmentActivity(Context context, String fragmentClass) {
        gotoFragmentActivity(context, fragmentClass, null);
    }

    /**
     * 跳转到指定Fragment的界面
     *
     * @param context
     * @param fragmentClass
     * @param args
     */
    public static void gotoFragmentActivity(Context context, String fragmentClass, Bundle args) {
        if (Pub.isStringEmpty(fragmentClass)) {
            return;
        }
        try {
            BaseFragment fragment = (BaseFragment) Class.forName(fragmentClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        KeyBoardUtils.closeKeybord(context);
        if (FotaApplication.containerToobar(fragmentClass) >= 0) {
            FtRounts.toMain(context, fragmentClass, args);
            return;
        }
        if (UserLoginUtil.havaUser() || ConstantsPage.withoutLoginFragment.contains(fragmentClass)) {
            Intent intent = new Intent(context, SimpleFragmentActivity.class);
            if (args != null) {
                intent.putExtra(BundleKeys.KEY_FRAGMENT_ARGUMENTS, args);
            }
            intent.putExtra(BundleKeys.KEY_FRAGMENT_CLASS, fragmentClass);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } else {
            FtRounts.toQuickLogin(context, fragmentClass);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container2);
        try {
            if (null == savedInstanceState) {
                Intent intent = getIntent();
                BaseFragment fragment = (BaseFragment) Class.forName(intent.getStringExtra(BundleKeys.KEY_FRAGMENT_CLASS)).newInstance();
                args = intent.getBundleExtra(BundleKeys.KEY_FRAGMENT_ARGUMENTS);
                if (args != null) {
                    fragment.setArguments(args);
                }
                getFragmentManagerDelegate().setAnimEnable(false);
                //删除过页面特效
                getFragmentManagerDelegate().addFragment(getContainerId(), fragment,
                        false, 0, 0, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean finishWhenNoFragment() {
        return true;
    }

}
