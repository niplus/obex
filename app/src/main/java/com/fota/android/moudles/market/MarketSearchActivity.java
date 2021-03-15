package com.fota.android.moudles.market;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BaseActivity;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.fota.android.utils.StatusBarUtil;
import com.fota.android.widget.btbwidget.SearchEdittext;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.RecyclerViewUtils;
import com.fota.android.widget.recyclerview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.LEFT;
import static com.fota.android.app.ConstantsReq.MARKET_SEARCH_REQUEST;

/**
 * Created by jiang on 2018/08/12.
 * market search
 */

public class MarketSearchActivity extends BaseActivity implements TextWatcher, View.OnClickListener {

    private SearchEdittext searchView;
    //    private SearchEdittext searchView;
    private RecyclerView listView;
    private EasyAdapter adapter;
    private LinearLayout tabBar;

    private List<FutureItemEntity> all = new ArrayList<>();
    private List<FutureItemEntity> filterDatas = new ArrayList<>();
    private String searchText;

    private List<String> coinTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markets_search);
        tabBar = findViewById(R.id.bar_markets_search);

        StatusBarUtil.setPaddingSmart(getContext(), tabBar);
        searchView = findViewById(R.id.markets_search);
        Drawable drawableLeft = getResources().getDrawable(Pub.getThemeResource(getContext(), R.attr.common_search_small));
        searchView.setCompoundDrawablePadding(UIUtil.dip2px(getContext(),10));
//        drawableLeft.setBounds(0, 0, ScreenUtils.convertDip2Px(10), 0);
        Drawable drawableRight = getResources().getDrawable(Pub.getThemeResource(getContext(), R.attr.icon_x));
        searchView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null);
        searchView.addTextChangedListener(this);
        TextView cancel = findViewById(R.id.search_cancel);
        listView = findViewById(R.id.search_recycler_view);
        cancel.setOnClickListener(this);

        initRecycleView();
        coinTitles.add(getString(R.string.common_all));
        coinTitles.add("BTC");
        coinTitles.add("ETH");
        coinTitles.add("USDT");
    }

    private void initRecycleView() {
        all = FotaApplication.getInstance().getMarketsCardsList();
        RecyclerViewUtils.initRecyclerView(listView, getContext());
        adapter = new EasyAdapter<FutureItemEntity, ViewHolder>(getContext(), R.layout.item_simple_search) {
            @Override
            public void convert(ViewHolder holder, final FutureItemEntity model, int position) {
                LinearLayout root = (LinearLayout) holder.getConvertView();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                root.setLayoutParams(params);
                root.setGravity(LEFT|CENTER_VERTICAL);
                root.setBackgroundColor(Pub.getColor(getContext(), R.attr.search_bg_color));
//                root.setPadding(ScreenUtils.convertDip2Px(16), ScreenUtils.convertDip2Px(12), ScreenUtils.convertDip2Px(16), ScreenUtils.convertDip2Px(12));

                TextView textView = root.findViewById(R.id.text1);
                textView.setGravity(LEFT|CENTER_VERTICAL);
                String add = "";
                if(model.getEntityType() == 1) {
                    add = mContext.getResources().getString(R.string.market_index);
                }
                holder.setText(R.id.text1, model.getFutureName() + add);
                textView.setTextColor(Pub.getColor(getContext(), R.attr.font_color));
                textView.setBackgroundColor(Pub.getColor(getContext(), R.attr.search_bg_color));
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), TradeMarketKlineActivity.class);
                        Bundle args = new Bundle();
                        args.putString("symbol", model.getFutureName());
                        args.putInt("id", model.getEntityId());
                        args.putInt("type", model.getEntityType());
                        intent.putExtras(args);
                        searchText = model.getFutureName();
                        startActivityForResult(intent, MARKET_SEARCH_REQUEST);
                    }
                });
            }
        };
        listView.setAdapter(adapter);
        adapter.putList(all);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MARKET_SEARCH_REQUEST) {
            Event event = Event.create(R.id.event_market_search_back);
            String param = "ALL";
            if (searchText.contains("USDT")) {
                param = "USDT";
            } else {
                for (String each : coinTitles) {
                    if (searchText.startsWith(each)) {
                        param = each;
                        break;
                    }
                }
            }
            event.putParam(String.class, param);
            EventWrapper.post(event);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_cancel) {
//            Skip.toMain(getContext(), 1);
            finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        filterData(s.toString());
    }

    private void filterData(String s) {
        filterDatas.clear();
        String str = s.toUpperCase();
        for (FutureItemEntity each : all) {
            if (each.getFutureName().contains(str)) {
                filterDatas.add(each);
                continue;
            }
        }
        adapter.putList(filterDatas);
        adapter.notifyDataSetChanged();
    }
}
