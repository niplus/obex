package com.fota.android.moudles.market.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.fota.android.moudles.market.MarketListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiang on 2018/08/09.
 */
public class MarketFragmentAdapter extends FragmentStatePagerAdapter {

    protected List<MarketListFragment> mFragmentList;

    protected List<String> mTitles;

    private int current;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public MarketFragmentAdapter(FragmentManager fm) {
        this(fm, null, null);
    }

    public MarketFragmentAdapter(FragmentManager fm, List<MarketListFragment> fragmentList, List<String> mTitles) {
        super(fm);
        if(fragmentList == null) {
            mFragmentList = new ArrayList<>();
        } else {
            mFragmentList = fragmentList;
        }

        this.mTitles = mTitles;
    }

    public void add(MarketListFragment fragment) {
        if (isEmpty()) {
            mFragmentList = new ArrayList<>();
        }
        mFragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        //        Logger.i("BaseFragmentAdapter position=" +position);
        return isEmpty() ? null : mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return isEmpty() ? 0 : mTitles.size();
    }

    public boolean isEmpty() {
        return mFragmentList == null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    public void setList(List<MarketListFragment> fragments) {
        this.mFragmentList = fragments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
