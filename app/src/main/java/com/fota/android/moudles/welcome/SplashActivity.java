package com.fota.android.moudles.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fota.android.BuildConfig;
import com.fota.android.R;
import com.fota.android.app.Constants;
import com.fota.android.core.base.BaseActivity;
import com.fota.android.moudles.main.MainActivity;
import com.fota.android.utils.apputils.MenuUtils;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2018/3/28.
 * 闪屏页
 */

public class SplashActivity extends BaseActivity {
    GifImageView splashView;
    private GifDrawable gifDrawable;

    @Override
    protected void onStart() {
        super.onStart();
        gifDrawable.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gifDrawable.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);
        splashView = findViewById(R.id.gif_iv);

        gifDrawable = (GifDrawable) splashView.getDrawable();
        gifDrawable.setLoopCount(1);


//        splashView.post(new Runnable() {
//            @Override
//            public void run() {
//                int height = splashView.getHeight();
//                int width = splashView.getWidth();
//
//                int screenWidth = UIUtil.getScreenWidth(getContext());
//
//                int newHeight = height * screenWidth / width;
//                UIUtil.setWidth(splashView,screenWidth);
//                UIUtil.setHeight(splashView,newHeight);
//
//            }
//        });
        //jiang Application已经获取
//        DiffTimeUtils.getTime();
        //Appbar Menu与加载
        MenuUtils.getAppBar(getContext());
        if (BuildConfig.DEBUG) {
            if (Constants.DEBUG) {
                showToast("be careful：now is debug");
            } else {
                showToast("be careful：now is debug, but url is release");
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enterHomeActivity();
            }
        }, gifDrawable.getDuration()+1000);

//        AnimationDrawable animationDrawable;
//        animationDrawable = (AnimationDrawable) splashView.getBackground();
//        animationDrawable.start();
//        int verisonCode = DeviceUtils.getVersionCode();
//        int odlCode = SharedPreferencesUtil.getInstance().get(SharedPreferencesUtil.Key.VERSIONCODE, 0);
////        //第一次开启或版本更新显示特性介绍
//        if (verisonCode > odlCode) {//版本号升级则显示引导页
//            SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.VERSIONCODE, verisonCode);
//        }
        setJustWhiteBarTxt();
    }

    private void enterHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
