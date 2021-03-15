package com.fota.android.moudles.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentSettingLanguageBinding;
import com.fota.android.utils.UserLoginUtil;


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

        boolean chinese = AppConfigs.isChinaLanguage();
        setLanguageUI(chinese);
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
                switchLanguage(0);
                UserLoginUtil.setJpushTag();
                break;
            case R.id.rl_english:
                switchLanguage(1);
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
        FotaApplication.getInstance().switchLanguage();
        setLanguageUI(i == 0 ? true : false);

        notify(R.id.event_language_changed);
        getHoldingActivity().recreate();
        EventWrapper.post(Event.create(R.id.event_main_changelanguage));
    }
}
