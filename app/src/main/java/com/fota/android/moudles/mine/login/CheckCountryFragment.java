package com.fota.android.moudles.mine.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fota.android.R;
import com.fota.android.commonlib.utils.FileUtils;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentCheckcountryBinding;
import com.fota.android.moudles.mine.login.bean.CounrtyAreasBean;
import com.fota.android.widget.sortrecyclerview.PinyinComparator;
import com.fota.android.widget.sortrecyclerview.PinyinUtils;
import com.fota.android.widget.sortrecyclerview.SideBar;
import com.fota.android.widget.sortrecyclerview.SortAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 选择国家
 */
public class CheckCountryFragment extends BaseFragment {
    private String from = "";
    FragmentCheckcountryBinding mBinding;
    CounrtyAreasBean counrtyAreasBean = null;

    private SortAdapter adapter;
    LinearLayoutManager manager;

    private List<CounrtyAreasBean.Area> SourceDateList;

    /**
     * 根据拼音来排列RecyclerView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    List<CounrtyAreasBean.Area> filterDateList = new ArrayList<>();

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkcountry, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        String areas = FileUtils.ReadDayDayString(mContext, "areas.txt");
        if (!TextUtils.isEmpty(areas)) {
            counrtyAreasBean = new Gson().fromJson(areas, CounrtyAreasBean.class);
        }
        initViews();
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        from = bundle.getString("from");
    }

    private void initViews() {
        pinyinComparator = new PinyinComparator();

        mBinding.sideBar.setTextView(mBinding.dialog);

        //设置右侧SideBar触摸监听
        mBinding.sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }

            }
        });

        SourceDateList = filledData(counrtyAreasBean.getAreas());


        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        filterDateList.addAll(SourceDateList);
        //RecyclerView社置manager
        manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(manager);
        adapter = new SortAdapter(mContext, filterDateList);
        mBinding.recyclerView.setAdapter(adapter);
        //item点击事件
        adapter.setOnItemClickListener(new SortAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                showToast(filterDateList.get(position).getName_zh());
                if ("regist".equals(from)) {
                    Event event = Event.create(R.id.event_regist_countrycheck);
                    event.putParam(CounrtyAreasBean.Area.class, filterDateList.get(position));
                    EventWrapper.post(event);
                } else if ("login".equals(from)) {
                    Event event = Event.create(R.id.event_login_countrycheck);
                    event.putParam(CounrtyAreasBean.Area.class, filterDateList.get(position));
                    EventWrapper.post(event);
                } else if ("bindphone".equals(from)) {
                    Event event = Event.create(R.id.event_bindphone_countrycheck);
                    event.putParam(CounrtyAreasBean.Area.class, filterDateList.get(position));
                    EventWrapper.post(event);
                } else if ("identity".equals(from)) {
                    Event event = Event.create(R.id.event_identity_countrycheck);
                    event.putParam(CounrtyAreasBean.Area.class, filterDateList.get(position));
                    EventWrapper.post(event);
                }

                finish();
            }
        });

        //根据输入框输入值的改变来过滤搜索
        mBinding.filterEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    /**
     * 为RecyclerView填充数据
     *
     * @param date
     * @return
     */
    private List<CounrtyAreasBean.Area> filledData(List<CounrtyAreasBean.Area> date) {
        List<CounrtyAreasBean.Area> mSortList = new ArrayList<>();

        for (int i = 0; i < date.size(); i++) {
            CounrtyAreasBean.Area area = date.get(i);
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(date.get(i).getName_zh());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                area.setLetters(sortString.toUpperCase());
            } else {
                area.setLetters("#");
            }

            mSortList.add(area);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新RecyclerView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {


        if (TextUtils.isEmpty(filterStr)) {
            filterDateList.addAll(SourceDateList);
        } else {
            filterDateList.clear();
            for (CounrtyAreasBean.Area sortModel : SourceDateList) {
                String name = sortModel.getName_zh();
                if (name.indexOf(filterStr.toString()) != -1 ||
                        PinyinUtils.getFirstSpell(name).startsWith(filterStr.toString())
                        //不区分大小写
                        || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr.toString())
                        || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr.toString())
                        ) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateList(filterDateList);
    }
}
