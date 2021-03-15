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

public class TransformActivity2 extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        enterHomeActivity();
    }


    protected void enterHomeActivity() {
        int index = FotaApplication.containerToobar(ConstantsPage.MineFragment);
        if (index < 0) {
            index = 0;
        }
        Intent intent = new Intent(TransformActivity2.this, MainActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
        finish();
    }
}
