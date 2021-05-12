package com.fota.android.moudles.home

import android.content.Intent
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fota.android.BR
import com.fota.android.R
import com.fota.android.app.Constants
import com.fota.android.app.ConstantsPage
import com.fota.android.common.bean.home.MenuBean
import com.fota.android.commonlib.base.AppConfigs
import com.fota.android.core.base.SimpleFragmentActivity
import com.fota.android.core.mvvmbase.BaseFragment
import com.fota.android.databinding.FragmentHomeBinding
import com.fota.android.databinding.ItemHomeCoinBinding
import com.fota.android.databinding.ItemHomeMenuBinding
import com.fota.android.databinding.ItemMainCoinBinding
import com.fota.android.moudles.futures.bean.ToTradeEvent
import com.fota.android.moudles.market.TradeMarketKlineActivity
import com.fota.android.moudles.market.bean.FutureItemEntity
import com.fota.android.utils.FtRounts
import com.fota.android.utils.StringFormatUtils
import com.fota.android.utils.UserLoginUtil
import com.fota.android.utils.dp2px
import com.fota.android.widget.recyclerview.SmartRefreshLayoutUtils
import com.ndl.lib_common.base.BaseAdapter
import com.ndl.lib_common.base.MyViewHolder
import com.ndl.lib_common.utils.LiveDataBus
import com.ndl.lib_common.utils.showSnackMsg
import com.ndl.lib_common.widget.banner.ImageEngine
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 首页
 */
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    private val menuList by lazy {
     mutableListOf(
            MenuBean(R.mipmap.icon_home_recharge, getString(R.string.recharge_coin)),
            MenuBean(R.mipmap.icon_home_community, getString(R.string.join_community)),
            MenuBean(R.mipmap.icon_home_invite, getString(R.string.commission_page_title)),
            MenuBean(R.mipmap.icon_home_guide, getString(R.string.new_guide)),
            MenuBean(R.mipmap.icon_home_grid, getString(R.string.grid_strategy)),
            MenuBean(R.mipmap.icon_home_activity, getString(R.string.event_details)),
            MenuBean(R.mipmap.icon_home_helper, getString(R.string.mine_help)),
            MenuBean(R.mipmap.icon_home_more, getString(R.string.more))
    )
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    private val mainCoinList = mutableListOf<FutureItemEntity>()
    private val futureCoinList = mutableListOf<FutureItemEntity>()
    private val spotCoinList = mutableListOf<FutureItemEntity>()

    private val bottomAdapter = object : BaseAdapter<ItemHomeCoinBinding, FutureItemEntity>(
            futureCoinList,
            R.layout.item_home_coin,
            BR.coin
    ){
        override fun onBindViewHolder(holder: MyViewHolder<ItemHomeCoinBinding>, position: Int) {
            super.onBindViewHolder(holder, position)
            holder.dataBinding.apply {
                timeLine.closePriceList = data[position].datas.map { it.close }.toMutableList()
                if (data[position].trend.contains("+")){
                    val colorValue = ContextCompat.getColor(requireContext(), R.color.trend_up)
                    tvTrend.setTextColor(colorValue)
                    timeLine.color = colorValue
                }else{
                    val colorValue = ContextCompat.getColor(requireContext(), R.color.trend_down)
                    tvTrend.setTextColor(colorValue)
                    timeLine.color = colorValue
                }

                if (data[position].entityType == 3){
                    tvName.text = data[position].futureName
                    tvFuture.visibility = View.GONE
                }else{
                    tvName.text = data[position].assetName
                    tvFuture.visibility = View.VISIBLE
                }

                root.setOnClickListener {
                    LiveDataBus.getBus<ToTradeEvent>("trade").value = ToTradeEvent(data[position], true)
                }
            }


        }
    }

    override fun initData() {
        viewModel!!.apply {
            bannerLiveData.observe(this@HomeFragment, Observer { bannerList ->
                if (bannerList.isNotEmpty()) {
                    dataBinding.banner.paths = bannerList.map {
                        it.pictureStoregedUrl
                    }.toMutableList()
                }

                dataBinding.refreshLayout.finishRefresh()
            })

            futureListLiveData.observe(this@HomeFragment, Observer {
                mainCoinList.clear()
                futureCoinList.clear()
                spotCoinList.clear()
                it.forEach { entity ->
                    if (entity.entityType == 3) {
                        spotCoinList.add(entity)
                    } else {
                        if (mainCoinList.size != 3)
                            mainCoinList.add(entity)
                        futureCoinList.add(entity)
                    }
                    dataBinding.rvMainSpot.adapter?.notifyDataSetChanged()
                    bottomAdapter.notifyDataSetChanged()
                }

            })

            LiveDataBus.getBus<List<FutureItemEntity>>("HangQing").observe(
                    this@HomeFragment,
                    Observer {
                        mainCoinList.clear()
                        futureCoinList.clear()
                        spotCoinList.clear()
                        it.forEach { entity ->
                            if (entity.entityType == 3) {
                                //合约列表
                                spotCoinList.add(entity)
//                        //主币种
                            } else {
                                if (mainCoinList.size != 3)
                                    mainCoinList.add(entity)
                                futureCoinList.add(entity)
                            }
                            dataBinding.rvMainSpot.adapter?.notifyDataSetChanged()
                            bottomAdapter.notifyDataSetChanged()
                        }
                    })

            getBanner()
            getCoinData()
        }
    }

    override fun initComp() {
        dataBinding.apply {
            //初始化banner
            banner.imageEngine = object : ImageEngine {
                override fun loadImage(imageView: ImageView, path: String) {
                    Glide.with(requireActivity()).load(path).into(imageView)
                }
            }

            //初始化notice
            val noticeList = mutableListOf(
                    "第一条",
                    "第二条",
                    "第三条",
                    "第四条"
            )
            marqueeView.startWithList(noticeList)

            //初始化menu
            rvMenu.layoutManager = GridLayoutManager(requireContext(), 4)
            rvMenu.adapter = object : BaseAdapter<ItemHomeMenuBinding, MenuBean>(
                    menuList,
                    R.layout.item_home_menu,
                    BR.menu
            ){
                override fun onBindViewHolder(
                        holder: MyViewHolder<ItemHomeMenuBinding>,
                        position: Int
                ) {
                    super.onBindViewHolder(holder, position)
                    holder.dataBinding.root.setOnClickListener {
                        when(menuList[position].name){

//                            MenuBean(R.mipmap.icon_home_recharge, getString(R.string.recharge_coin)),
//                            MenuBean(R.mipmap.icon_home_community, getString(R.string.join_community)),
//                            MenuBean(R.mipmap.icon_home_invite, getString(R.string.commission_page_title)),
//                            MenuBean(R.mipmap.icon_home_guide, getString(R.string.new_guide)),
//                            MenuBean(R.mipmap.icon_home_grid, getString(R.string.grid_strategy)),
//                            MenuBean(R.mipmap.icon_home_activity, getString(R.string.event_details)),
//                            MenuBean(R.mipmap.icon_home_helper, getString(R.string.mine_help)),
//                            MenuBean(R.mipmap.icon_home_more, getString(R.string.more))
                            getString(R.string.recharge_coin) -> {
                                if (!UserLoginUtil.havaUser()) {
                                    FtRounts.toQuickLogin(requireContext())
                                    return@setOnClickListener
                                }
                                SimpleFragmentActivity.gotoFragmentActivity(
                                        requireContext(),
                                        ConstantsPage.RechargeMoneyFragment
                                )
                            }
                            getString(R.string.join_community) -> {
                                FtRounts.toWebView(
                                        requireContext(),
                                        getString(R.string.join_community) ,
                                        Constants.JOIN_COMMUNITY
                                )
                            }
                            getString(R.string.commission_page_title) -> {
                                if (!UserLoginUtil.havaUser()) {
                                    FtRounts.toQuickLogin(requireContext())
                                    return@setOnClickListener
                                }

                                val userId = UserLoginUtil.getId()
                                if (userId != "0") FtRounts.toWebView(
                                        requireContext(),
                                        "",
                                        "https://invite.cboex.com/#/invite?userId=" + userId + "&language=" + MMKV.defaultMMKV()!!
                                                .decodeString(
                                                        "language"
                                                )
                                )
                            }
                            getString(R.string.new_guide) -> {
                                FtRounts.toWebView(requireContext(),  getString(R.string.new_guide), Constants.URL_GUIDE)
                            }
                            getString(R.string.grid_strategy), getString(R.string.more) -> {
                                showSnackMsg(getString(R.string.coming_soon))
                            }
                            getString(R.string.event_details) -> {
                                FtRounts.toWebView(requireContext(), getString(R.string.event_details), Constants.URL_ACTIVITY)
                            }
                            getString(R.string.mine_help) -> {
                                //                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.HelpFragment);
                                if (AppConfigs.isChinaLanguage()) {
                                    FtRounts.toWebView(
                                            requireContext(),
                                            resources.getString(R.string.mine_help),
                                            Constants.URL_HELPCENTER_CH
                                    )
                                } else {
                                    FtRounts.toWebView(
                                            requireContext(),
                                            resources.getString(R.string.mine_help),
                                            Constants.URL_HELPCENTER_EN
                                    )
                                }
                            }
                        }
                    }
                }
            }
            rvMenu.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position > 3) {
                        outRect.top = requireContext().dp2px(20).toInt()
                    }
                }
            })

            rvMainSpot.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
            )
            rvMainSpot.adapter = object : BaseAdapter<ItemMainCoinBinding, FutureItemEntity>(
                    mainCoinList,
                    R.layout.item_main_coin,
                    BR.coin
            ){
                override fun onBindViewHolder(
                        holder: MyViewHolder<ItemMainCoinBinding>,
                        position: Int
                ) {
                    super.onBindViewHolder(holder, position)
                    holder.dataBinding.apply {
                        timeLine.closePriceList = data[position].datas.map { it.close }.toMutableList()

                        if (data[position].trend.contains("+")){
                            val colorValue = ContextCompat.getColor(
                                    requireContext(),
                                    R.color.trend_up
                            )
                            tvPrice.setTextColor(colorValue)
                            tvTrend.setTextColor(colorValue)
                            timeLine.color = colorValue
                        }else{
                            val colorValue = ContextCompat.getColor(
                                    requireContext(),
                                    R.color.trend_down
                            )
                            tvPrice.setTextColor(colorValue)
                            tvTrend.setTextColor(colorValue)
                            timeLine.color = colorValue
                        }

                        root.setOnClickListener {
                            val itemData = data[position]
                            val intent = Intent(activity, TradeMarketKlineActivity::class.java)
                            val args = Bundle()
                            args.putString("symbol", itemData.futureName)
                            args.putInt("id", itemData.entityId)
                            args.putInt("type", itemData.entityType)
                            intent.putExtras(args)
                            startActivity(intent)
                        }
                    }
                }
            }

            rvCoinList.layoutManager = LinearLayoutManager(requireContext())
            rvCoinList.adapter = bottomAdapter

            tvFutureTab.setOnClickListener {
                bottomAdapter.refreshData(futureCoinList)
                tvFutureTab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                tvFutureTab.setTypeface(null, Typeface.BOLD)
                tvSpotTab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                tvSpotTab.setTypeface(null, Typeface.NORMAL)
            }

            tvSpotTab.setOnClickListener {
                bottomAdapter.refreshData(spotCoinList)
                tvFutureTab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                tvFutureTab.setTypeface(null, Typeface.NORMAL)
                tvSpotTab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                tvSpotTab.setTypeface(null, Typeface.BOLD)
            }

            tvLogin.setOnClickListener {
                if (!UserLoginUtil.havaUser()) {
                    FtRounts.toQuickLogin(requireContext())
                }
            }

            ivFastBuy.setOnClickListener {
                showSnackMsg(getString(R.string.coming_soon))
            }

            banner.onBannerClick = {
                FtRounts.toWebView(requireContext(), "", viewModel.bannerLiveData.value!![it].hyperlink)
            }

            ivMessage.setOnClickListener {
                SimpleFragmentActivity.gotoFragmentActivity(context, ConstantsPage.NoticeCenterFragment)
            }

            SmartRefreshLayoutUtils.initHeader(refreshLayout, context)
            refreshLayout.setOnRefreshListener(OnRefreshListener {
                viewModel.getBanner()
                viewModel.getCoinData()
            })

            MainScope().launch {
                while (true) {
                    delay(3000)
                    if (banner.viewpager2.currentItem == banner.viewpager2.adapter?.itemCount?.minus(1) ?: 0) {
                        banner.viewpager2.currentItem = 0
                    } else
                        banner.viewpager2.currentItem = banner.viewpager2.currentItem + 1
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (UserLoginUtil.havaUser()) {
            val account = UserLoginUtil.getLoginedAccount()
            if (!TextUtils.isEmpty(account) && account.contains("@")) {
                dataBinding.tvLogin.setText(hideEmaile(account))
            } else if (!TextUtils.isEmpty(account)) {
                dataBinding.tvLogin.setText(hidePhone(account))
            }
        } else {
            dataBinding.tvLogin.setText(getString(R.string.mine_login))
        }

        SmartRefreshLayoutUtils.refreshHeadLanguage(dataBinding.refreshLayout, requireContext())
    }

    private fun hideEmaile(str: String): String? {
        return StringFormatUtils.getHideEmail(str)
    }

    private fun hidePhone(str: String): String? {
        return StringFormatUtils.getHidePhone(str)
    }

    override fun createViewModel(): HomeViewModel {
        return ViewModelProvider(this).get(HomeViewModel::class.java)
    }

}