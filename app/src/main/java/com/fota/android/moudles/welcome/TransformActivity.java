package com.fota.android.moudles.welcome;

import android.content.Intent;
import android.os.Bundle;

import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.core.base.BaseActivity;
import com.fota.android.moudles.main.MainActivity;

/**
 * Created by Administrator on 2018/3/28.
 * 闪屏页
 * 切换语言之后跳转 我的页面
 */

public class TransformActivity extends BaseActivity {
    com.airbnb.lottie.LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        enterHomeActivity();
    }


    protected void enterHomeActivity() {
        int index = FotaApplication.containerToobar(ConstantsPage.MineFragment);
        if (index < 0) {
            index = 0;
        }
        Intent intent = new Intent(TransformActivity.this, MainActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
        finish();
//        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

}
