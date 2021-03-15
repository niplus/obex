package com.fota.android.moudles.welcome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.SharedPreferencesUtil;
import com.fota.android.core.base.BaseActivity;
import com.fota.android.moudles.main.MainActivity;
import com.fota.android.moudles.welcome.adapter.GuideViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导页
 */
public class GuideActivity extends BaseActivity {
    ViewPager viewPager;
    TextView tv_enter;
    ImageView imv_txt1,imv_txt2,imv_txt3,imv_txt4,imv_bg4;
    private GuideViewPagerAdapter adapter;
    private List<View> views;

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        setContentView(R.layout.activity_guide);
        viewPager = findViewById(R.id.vp_guide);

        views = new ArrayList<View>();

        View view1 = LayoutInflater.from(this).inflate(R.layout.guide2_view1, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.guide2_view2, null);
        View view3 = LayoutInflater.from(this).inflate(R.layout.guide2_view3, null);
        View view4 = LayoutInflater.from(this).inflate(R.layout.guide2_view4, null);
        tv_enter = view4.findViewById(R.id.tv_enter);
        imv_txt1 = view1.findViewById(R.id.imv_txt);
        imv_txt2 = view2.findViewById(R.id.imv_txt);
        imv_txt3 = view3.findViewById(R.id.imv_txt);
        imv_txt4 = view4.findViewById(R.id.imv_txt);
        imv_bg4 = view4.findViewById(R.id.imv_bg);

        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);

        adapter = new GuideViewPagerAdapter(views);
        viewPager.setAdapter(adapter);

        tv_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterMainActivity();
            }
        });
        if (AppConfigs.getLanguegeInt() == 0){
            imv_txt1.setImageResource(R.mipmap.guide2_txt1_cn);
            imv_txt2.setImageResource(R.mipmap.guide2_txt2_cn);
            imv_txt3.setImageResource(R.mipmap.guide2_txt3_cn);
            imv_txt4.setImageResource(R.mipmap.guide2_txt4_cn);
            imv_bg4.setImageResource(R.mipmap.guide2_bg4);

        }else if (AppConfigs.getLanguegeInt()==1){
            imv_txt1.setImageResource(R.mipmap.guide2_txt1_en);
            imv_txt2.setImageResource(R.mipmap.guide2_txt2_en);
            imv_txt3.setImageResource(R.mipmap.guide2_txt3_en);
            imv_txt4.setImageResource(R.mipmap.guide2_txt4_en);
            imv_bg4.setImageResource(R.mipmap.guide2_bg4_en);
//            tv_enter.setText("Trade Now");
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }

    }

    private void enterMainActivity() {
        Intent intent = new Intent(this,
                MainActivity.class);
        startActivity(intent);
        SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.FIRST_OPEN, true);
        finish();
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
    }

    public boolean checkPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
