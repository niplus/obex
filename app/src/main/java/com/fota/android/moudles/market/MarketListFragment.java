package com.fota.android.moudles.market;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.StringUtils;
import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.http.Http;
import com.fota.android.moudles.home.HomeRepository;
import com.fota.android.moudles.market.bean.CardFavorParamBean;
import com.fota.android.moudles.market.bean.CardItemParamBean;
import com.fota.android.moudles.market.bean.ChartLineEntity;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.fota.android.moudles.mine.login.FotaLoginActivity;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.RecyclerViewUtils;
import com.fota.android.widget.recyclerview.ViewHolder;
import com.guoziwei.fota.chart.view.fota.LittlePageTimeView;
import com.guoziwei.fota.model.HisData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketListFragment extends MvpListFragment<BaseListPresenter>
        implements BaseListView {
    /**
     * 过滤之后的map
     * btc -
     * btc指数、btc当周、btc当月、btc当季数据
     */
    final Map<String, List<HisData>> dataMap = new HashMap<>();

    /**
     * card sort
     */
    List<FutureItemEntity> futureList = new ArrayList<>();

    /**
     * list sort
     */
    List<FutureItemEntity> groupListDatas = new ArrayList<>();

    /**
     * btc-eth...
     */
    private List<String> notCardGroup = new ArrayList<>();

    /**
     * ALL +
     * coinTitles.add("BTC");
     * coinTitles.add("ETH");
     * coinTitles.add("EOS");
     * coinTitles.add("BCH");
     * coinTitles.add("ETC");
     * coinTitles.add("LTC");
     * coinTitles.add("USDK");
     */
    private String symbol;

    public void setNoDataError(boolean noDataError) {
        this.noDataError = noDataError;
    }

    protected boolean noDataError = false;

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);

        HomeRepository.Companion.getMarketInfoLivedata().observe(this, new Observer<List<FutureItemEntity>>() {
            @Override
            public void onChanged(List<FutureItemEntity> futureItemEntities) {
                refreshData(symbol);
            }
        });
    }

    public void setCard(boolean card) {
        if (getView() == null) return;
        isCard = card;

        if (!isCard) {
            RecyclerViewUtils.initRecyclerView(mRecyclerView, getContext());
            refreshDataByGroup();
            adapter = new EasyAdapter<FutureItemEntity, ViewHolder>(getContext(), R.layout.item_future_time_list) {
                @Override
                public void convert(ViewHolder holder, final FutureItemEntity model, int position) {
                    String add = "";
                    if (model.getEntityType() == 1) {
                        add = mContext.getResources().getString(R.string.market_index);
                    }
                    holder.setBackgroundColor(R.id.item_future, Pub.getColor(getContext(), R.attr.reverse_bg));
                    holder.setText(R.id.txt_future_name, model.getFutureName().replace("永续", " " + getString(R.string.perp)) + add);
                    holder.setVisible(R.id.market_list_divide_line, !model.isShowTopMargin());
                    if (model.isShowTopMargin()) {
                    } else if (model.isBottom()) {
                    } else {
                        holder.setBackgroundRes(R.id.future_corner, R.drawable.ft_bg_color);
                    }
                    if (StringUtils.isEmpty(model.getVolume())) {
                        holder.setVisible(R.id.ll_market_volume, false);
                    } else {
                        holder.setVisible(R.id.ll_market_volume, true);
                        holder.setText(R.id.txt_future_volume, model.getVolume());
                    }
                    if (model.getEntityType() != 3) {
                        holder.setText(R.id.txt_future_price, model.getLastPrice() == null ? "- -" : model.getUscPrice());
//                        holder.setVisible(R.id.txt_dollar_price, false);
                    } else {
//                        holder.setVisible(R.id.txt_dollar_price, true);
                        String temp = model.getUscPrice();
                        if (!TextUtils.isEmpty(temp)) {
                            String[] arrs = temp.split("/");
                            if (arrs != null && arrs.length == 2) {
                                holder.setText(R.id.txt_future_price, arrs[0]);
                                holder.setText(R.id.txt_dollar_price, arrs[1]);
                            } else if(arrs != null && arrs.length == 1) {
                                holder.setText(R.id.txt_future_price, arrs[0]);
                                holder.setText(R.id.txt_dollar_price, "--");
                            } else {
                                holder.setText(R.id.txt_future_price, "--");
                                holder.setText(R.id.txt_dollar_price, "--");
                            }
                        }
                    }
                    holder.setText(R.id.txt_future_trend, model.getTrend());
                    UIUtil.setBtnShapeBgColor(holder.getView(R.id.txt_future_trend), AppConfigs.getColor(!model.getTrend().contains("-")));
                    bindHolder(holder, model);
                }
            };
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setPadding(0, 0, 0, 0);
        } else {
            initLayoutManger();
            initMainAdapter();
            mRecyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
        //改动了MVP的recycleView方式，需要自己添加headerView
        mRecyclerView.addHeaderView(headView);
    }

    /**
     * 卡片 切 列表， 需要分组
     * 按BTC-ETH..
     */
    protected void refreshDataByGroup() {
        if (groupListDatas == null) {
            return;
        }
        if (groupListDatas.size() <= 0) {
            for (int i = 0; i < futureList.size(); i++) {
                FutureItemEntity each = futureList.get(i);
                addGroupData(true, "title", each);
            }

            if (groupListDatas.size() > 0) {
                int index = groupListDatas.size();
                groupListDatas.get(index - 1).setBottom(true);
            }
        }
    }

    private boolean addGroupData(boolean showTop, String title, FutureItemEntity each) {
        each.setShowTopMargin(showTop);
        showTop = false;
        each.setGroupTitle(title);
        each.setBottom(false);
        groupListDatas.add(each);
        return showTop;
    }

    private boolean isCard = true;

    public static MarketListFragment newInstance() {
        Bundle args = new Bundle();
//        args.putString("symbol", symbol);
        MarketListFragment fragment = new MarketListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        notCardGroup.add("BTC");
        notCardGroup.add("ETH");
        notCardGroup.add("SPOT");

    }

    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_notitle_xml;
    }

    /**
     * 定制样式
     */
    protected void initLayoutManger() {
        if (mRecyclerView == null) {
            return;
        }

        GridLayoutManager layoutManager;
        layoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        int recycleBg = Pub.getColor(getContext(), R.attr.reverse_bg);
        mRecyclerView.setBackgroundColor(recycleBg);
        mRecyclerView.setPadding(UIUtil.dip2px(getContext(), 8), 0, UIUtil.dip2px(getContext(), 8), 0);
    }

    @Override
    public void showNoData() {
        super.showNoData();
        //no data need change bg_color --
        //1204 更新 不用更新了
        noDataError = true;
        int recycleBg = Pub.getColor(getContext(), R.attr.reverse_bg);
        mRecyclerView.setBackgroundColor(recycleBg);
        if (adapter != null) {
            adapter.putList(null);
        }
    }

    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<FutureItemEntity, ViewHolder>(getContext(), R.layout.item_future_time_chart) {
            @Override
            public void convert(ViewHolder holder, final FutureItemEntity model, int position) {
                String add = "";
                if (model.getEntityType() == 1) {
                    add = mContext.getResources().getString(R.string.market_index);
                }
                View root = holder.getConvertView();
                holder.setBackgroundColor(R.id.item_future_chart, Pub.getColor(getContext(), R.attr.reverse_bg));
                holder.setText(R.id.txt_future_name, model.getFutureName().replace("永续", " " + getString(R.string.perp)) + add);
                holder.setText(R.id.txt_last_price, model.getLastPrice());
                holder.setText(R.id.txt_up_down, model.getTrend());
                holder.setTextColor(R.id.txt_last_price, AppConfigs.getColor(!model.getTrend().contains("-")));
                holder.setTextColor(R.id.txt_up_down, AppConfigs.getColor(!model.getTrend().contains("-")));
                bindHolder(holder, model);

                LittlePageTimeView timeLineView = holder.getView(R.id.time_line);
                if (timeLineView != null) {
                    timeLineView.setIndex(model.getEntityType() == 1);
                    timeLineView.setDateFormat("HH:mm");
                    //无map
                    if (dataMap.get(model.getFutureName()) == null) {
                        return;
                    }

                    timeLineView.initData(dataMap.get(model.getFutureName()));
                } else {
//                    timeLineView.initData(dataMap.get(model.getFutureName()));
                }
            }
        };
    }

    private void bindHolder(ViewHolder holder, final FutureItemEntity model) {
        if (model.isFavorite())
            holder.setImageResource(R.id.img_favorite, Pub.getThemeResource(getContext(), R.attr.common_star_red));
        else
            holder.setImageResource(R.id.img_favorite, Pub.getThemeResource(getContext(), R.attr.common_star_grey));

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TradeMarketKlineActivity.class);
                Bundle args = new Bundle();
                args.putString("symbol", model.getFutureName());
                args.putInt("id", model.getEntityId());
                args.putInt("type", model.getEntityType());
                intent.putExtras(args);
                startActivity(intent);
            }
        });
        //收藏
        holder.setOnClickListener(R.id.ll_img_container, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserLoginUtil.havaUser()) {
                    Intent intent = new Intent(mContext, FotaLoginActivity.class);
                    getActivity().startActivity(intent);
                    return;
                }

                if (model.isFavorite()) {//取消
                    doFavorCheck(0, model.getEntityId() + "", model.getEntityType() + "", model.getFutureName());
                } else {//收藏
                    doFavorCheck(1, model.getEntityId() + "", model.getEntityType() + "", model.getFutureName());
                }
            }
        });
    }

    /**
     * 收藏 与 取消
     */
    private void doFavorCheck(final int operation, String cardId, String cardType, final String cardName) {
        CardFavorParamBean paramBean = new CardFavorParamBean(operation);
        paramBean.getData().add(new CardItemParamBean(cardId, cardType, cardName));
        Http.getMarketService().postFavor(paramBean)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(BaseHttpEntity result) {
                        if (MarketListFragment.this != null && MarketListFragment.this.isAdded()) {
                            for (FutureItemEntity each : futureList) {
                                if (cardName.equals(each.getFutureName())) {
                                    each.setFavorite(operation != 0);
                                    if (each.isFavorite()) {
                                        each.setCollectTime(System.currentTimeMillis());
//                                        L.e("favor", each.getFutureName()+":"+each.getCollectTime());
                                    } else {
                                        each.setCollectTime(0);
                                    }
                                    break;
                                }
                            }

                            if ("FAVOR".equals(symbol)) {
                                refreshData(symbol);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                            showToast(operation == 0 ? R.string.market_favor_cancel_suc : R.string.market_favor_success);
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        if (MarketListFragment.this != null && MarketListFragment.this.isAdded()) {
                            showToast(operation == 0 ? R.string.market_favor_cancel_fail : R.string.market_favor_fail);
                        }
                    }
                });
    }

    /**
     * parentFragment call
     * refresh
     * <p>
     * typeOrDetail "BTC";"ETH");"EOS");"BCH");"ETC");"LTC");"USDK" + ALL
     * FAVOR;HOT
     * <p>
     * 修改 typeOrDetail 值可能为 FAVOR、HOT、ALL、SPOT
     */
    public void refreshData(String typeOrDetail) {
        this.symbol = typeOrDetail;
        // or 正在滑动
        if (mRecyclerView != null && mRecyclerView.getScrollState() != 0) {
            return;
        }
        if (adapter != null) {
            futureList.clear();
            groupListDatas.clear();
            List<FutureItemEntity> applicationCache = HomeRepository.Companion.getMarketInfoLivedata().getValue();
            //btc-eth-...
            if ("FAVOR".equals(typeOrDetail)) {
                for (FutureItemEntity each : applicationCache) {
                    if (each.isFavorite()) {
                        futureList.add(each);
                    }
                }
                Collections.sort(futureList);
            } else if ("HOT".equals(typeOrDetail)) {
                for (FutureItemEntity futureItemEntity : applicationCache) {
                    if (futureItemEntity.isHot()) {
                        futureList.add(futureItemEntity);
                    }
                }
            } else if ("SPOT".equals(typeOrDetail)) {
                for (FutureItemEntity each : applicationCache) {//现货
                    if (each.getEntityType() == 3) {
                        futureList.add(each);
                    }
                }
            } else if ("INDEX".equals(typeOrDetail)) {
                for (FutureItemEntity each : applicationCache) {//指数
                    if (each.getEntityType() == 1) {
                        futureList.add(each);
                    }
                }
            } else if (!"ALL".equals(typeOrDetail)) {//不是全部 -- 改动 合约
                for (FutureItemEntity each : applicationCache) {
                    if (each.getEntityType() == 2) {
                        futureList.add(each);
                    }
                }
            } else {
                futureList.addAll(applicationCache);
            }
            dataMap.clear();
            for (FutureItemEntity each : futureList) {
                List<HisData> tempDatas = new ArrayList<>();
                for (ChartLineEntity eachKline : each.getDatas()) {
                    HisData hisData = createNewHisData(eachKline);
                    tempDatas.add(hisData);
                }
                dataMap.put(each.getFutureName(), tempDatas);
            }
            if (isCard) {
                adapter.putList(futureList);
                //showNoData
                showNoDataIfNeed(futureList);
            } else {
                refreshDataByGroup();
                adapter.putList(groupListDatas);
                //showNoData
                showNoDataIfNeed(groupListDatas);
            }
        }
    }

    //showNoData
    protected void showNoDataIfNeed(List<FutureItemEntity> futureList) {
        if (futureList.size() == 0) {
            showNoData();
        }
//        && !"FAVOR".equals(typeOrDetail)--favor的情况，有数据就该展示数据的啊
        if (futureList.size() > 0) {
            noDataError = false;
            int recycleBg = Pub.getColor(getContext(), R.attr.reverse_bg);
            mRecyclerView.setBackgroundColor(recycleBg);
            showdata();
        }
    }

    private HisData createNewHisData(ChartLineEntity m) {
        HisData data = new HisData();

        data.setClose(m.getClose());
        data.setOpen(m.getOpen());
        data.setVol(m.getVolume());
        data.setDate(m.getTime());
        return data;
    }

    @Override
    protected boolean setRefreshEnable() {
        return false;
    }

    @Override
    protected BaseListPresenter createPresenter() {
        return new BaseListPresenter(this);
    }

    @Override
    public void emptyButtonReloadEvent() {
        EventWrapper.post(Event.create(R.id.event_market_reload));
    }
}
