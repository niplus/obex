package com.fota.android.moudles.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.SharedPreferencesUtil;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentSettingLanguageBinding;
import com.fota.android.moudles.welcome.SplashActivity;
import com.fota.android.utils.UserLoginUtil;
import com.ndl.lib_common.utils.LiveDataBus;
import com.tencent.mmkv.MMKV;

import java.util.Locale;


/**
 * Created by jiang
 * language 设置
 */

public class LanguageFragment extends BaseFragment implements View.OnClickListener {
    FragmentSettingLanguageBinding mBinding;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_language, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        mBinding.setView(this);

        String language = MMKV.defaultMMKV().decodeString("language", "");

        Log.i("+++++++++++++++++", "laguage: " + language);
        if (language.equals("")) {
            //第一次安装
            if ((Integer)SharedPreferencesUtil.getInstance().get("Languege", -1) == -1){
                if (Locale.getDefault().getLanguage().equals("en")){
                    mBinding.imgChinese.setVisibility(View.GONE);
                    mBinding.imgEnglish.setVisibility(View.VISIBLE);
                    mBinding.imgTraditionalChinese.setVisibility(View.GONE);
                }else{
                    if (Locale.getDefault().getCountry().equals("CN")) {
                        mBinding.imgChinese.setVisibility(View.VISIBLE);
                        mBinding.imgEnglish.setVisibility(View.GONE);
                        mBinding.imgTraditionalChinese.setVisibility(View.GONE);
                    } else {
                        mBinding.imgChinese.setVisibility(View.GONE);
                        mBinding.imgEnglish.setVisibility(View.GONE);
                        mBinding.imgTraditionalChinese.setVisibility(View.VISIBLE);
                    }
                }
            }else {
                //适配老版本
                boolean chinese = AppConfigs.isChinaLanguage();
                setLanguageUI(chinese);
            }
        }else {
            switch(language){
                case "zh":
                    mBinding.imgChinese.setVisibility(View.VISIBLE);
                    mBinding.imgEnglish.setVisibility(View.GONE);
                    mBinding.imgTraditionalChinese.setVisibility(View.GONE);
                    break;
                case "en":
                    mBinding.imgChinese.setVisibility(View.GONE);
                    mBinding.imgEnglish.setVisibility(View.VISIBLE);
                    mBinding.imgTraditionalChinese.setVisibility(View.GONE);
                    break;
                case "tw":
                    mBinding.imgChinese.setVisibility(View.GONE);
                    mBinding.imgEnglish.setVisibility(View.GONE);
                    mBinding.imgTraditionalChinese.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void setLanguageUI(boolean isChinese) {
        if (isChinese) {
            mBinding.imgChinese.setVisibility(View.VISIBLE);
            mBinding.imgEnglish.setVisibility(View.GONE);
        } else {
            mBinding.imgChinese.setVisibility(View.GONE);
            mBinding.imgEnglish.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public String setAppTitle() {
        return getResources().getString(R.string.mine_language);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_chinese:
//                switchLanguage(0);
                switchLanguage("zh");
                mBinding.imgChinese.setVisibility(View.VISIBLE);
                mBinding.imgEnglish.setVisibility(View.GONE);
                mBinding.imgTraditionalChinese.setVisibility(View.GONE);
                UserLoginUtil.setJpushTag();
                break;
            case R.id.rl_english:
                mBinding.imgChinese.setVisibility(View.GONE);
                mBinding.imgEnglish.setVisibility(View.VISIBLE);
                mBinding.imgTraditionalChinese.setVisibility(View.GONE);
                switchLanguage("en");
                UserLoginUtil.setJpushTag();
                break;
            case R.id.rl_traditional_chinese:
                switchLanguage("tw");
                mBinding.imgChinese.setVisibility(View.GONE);
                mBinding.imgEnglish.setVisibility(View.GONE);
                mBinding.imgTraditionalChinese.setVisibility(View.VISIBLE);
                UserLoginUtil.setJpushTag();
                break;
        }
    }

    /**
     * @param i 0 chinese; 1 english
     */
    private void switchLanguage(int i) {
        AppConfigs.setLanguege(i);
        AppConfigs.themeOrLangChanged = true;
        //jiang
//        FotaApplication.getInstance().switchLanguage();
        setLanguageUI(i == 0 ? true : false);

        notify(R.id.event_language_changed);
        getHoldingActivity().recreate();
        EventWrapper.post(Event.create(R.id.event_main_changelanguage));
    }

    private void switchLanguage(String language){
        MMKV.defaultMMKV().encode("language", language);
//        notify(R.id.event_language_changed);
//        getHoldingActivity().recreate();
//        EventWrapper.post(Event.create(R.id.event_main_changelanguage));
//
//        MMKV.defaultMMKV().encode("login", true);
//        finish();
//        System.exit(0);
//        android.os.Process.killProcess(android.os.Process.myPid());
        if (FotaApplication.getLoginSrtatus())
            MMKV.defaultMMKV().encode("isLogin", true);
        Intent intent = new Intent(requireContext(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        LiveDataBus.INSTANCE.getBus("recreate").setValue("true");
        finish();

//        ActivityManager am = (ActivityManager)getActivity().getSystemService(ACTIVITY_SERVICE);
//        am.restartPackage("com.android.nfc");

//
//
//        ActivityManager  manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
//        manager.restartPackage("com.fota.android");
    }
}
