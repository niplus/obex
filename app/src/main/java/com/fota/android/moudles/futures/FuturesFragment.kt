package com.fota.android.moudles.futures

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.fota.android.R
import com.fota.android.app.BundleKeys
import com.fota.android.app.FotaApplication
import com.fota.android.common.bean.BeanChangeFactory
import com.fota.android.common.bean.exchange.CurrentPriceBean
import com.fota.android.common.bean.home.EntrustBean
import com.fota.android.common.listener.IFuturesUpdateFragment
import com.fota.android.commonlib.base.AppConfigs
import com.fota.android.commonlib.utils.*
import com.fota.android.core.base.BaseFragmentAdapter
import com.fota.android.core.base.BtbMap
import com.fota.android.moudles.exchange.index.ExchangeFragment
import com.fota.android.moudles.futures.complete.FuturesCompleteFragment
import com.fota.android.moudles.futures.money.FuturesMoneyBean
import com.fota.android.moudles.futures.money.FuturesMoneyListFragment
import com.fota.android.moudles.futures.order.FuturesOrdersFragment
import com.fota.android.moudles.market.FullScreenKlineActivity
import com.fota.android.moudles.market.bean.ChartLineEntity
import com.fota.android.utils.*
import com.fota.android.utils.apputils.TradeUtils
import com.fota.android.utils.apputils.TradeUtils.ExchangePasswordListener
import com.fota.android.widget.btbwidget.FotaTextWatch
import com.fota.android.widget.dialog.LeverDialog
import com.fota.android.widget.popwin.FutureTopWindow
import com.fota.android.widget.popwin.PasswordDialog
import com.fota.android.widget.popwin.SpinerPopWindow3
import com.guoziwei.fota.chart.view.fota.FotaBigKLineBarChartView
import com.guoziwei.fota.chart.view.fota.ImBeddedTimeLineBarChartView
import com.guoziwei.fota.model.HisData
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.pow

class FuturesFragment : ExchangeFragment(), IFuturesUpdateFragment, FutureTradeView {
    private var spotPrice: String? = null
    private var topInfo: FutureTopInfoBean? = null
    private var popupTopWindow: FutureTopWindow? = null
    private var preciseMargin: BtbMap? = null
    private var isLeverChange = false

    /**
     *
     * 【线上】【合约交易】保证金需求目前显示为实时刷新，应为下拉刷新
     */
    private var onRefreshDepthReqed = false
    //chart relative
    /**
     * 现货指数数据
     * Spot -- T 15m
     */
    val spot15Data: List<HisData> = ArrayList(100)

    /**
     * 现货指数数据
     * Spot--K
     */
    val spotData: MutableList<HisData> = ArrayList(100)
    override fun updateInstance(assetName: String, model: FutureContractBean, isBuy: Boolean) {
        this.isBuy = isBuy
        presenter.setBasePrecision("-1")
        clearEditText()
        refreshBuy()
        presenter.setFromHq(assetName, model)
        onRefresh()
    }

    override fun createPresenter(): FuturesPresenter {
        return FuturesPresenter(this)
    }

    override fun getPresenter(): FuturesPresenter {
        return super.getPresenter() as FuturesPresenter
    }

    override fun isFutures(): Boolean {
        return true
    }

    override fun onInitView(view: View) {
        super.onInitView(view)
        mHeadBinding.kline.setChartType(FotaBigKLineBarChartView.ChartType.FUTURE)
        mHeadBinding.tline.setChartType(ImBeddedTimeLineBarChartView.ChartType.FUTURE)
        //        mHeadBinding.tline.setChartType(ImBeddedTimeLineBarChartView.ChartType.USDT);
//        mHeadBinding.tline.setChartType(FotaBigTimeLineBarChartView.ChartType.SPOT);
        val color =
            if (AppConfigs.isWhiteTheme()) getThemeColor(R.attr.font_color) else getThemeColor(
                R.attr.font_color3
            )
        mHeadBinding.futuresTvDate.setTextColor(color)
        UIUtil.setRectangleBorderBg(mHeadBinding.futuresTvLever, color)
        mHeadBinding.futuresTvLever.setTextColor(color)
        mHeadBinding.fprogress.isLever = false
        mHeadBinding.fprogress.leverChangeListener = label@{ rate: Int ->
            if (!FotaApplication.getLoginSrtatus()) {
                return@label
            }
            val price: String = if (isLimit) {
                mHeadBinding.price2.text.toString()
            } else {
                currentPrice
            }
            val moneyUnit = price.divide(level, pricePrecision)
            val amountTotal = topInfo!!.available.divide(moneyUnit, amountPrecision)
            val minValue: String = (1f / 10.0.pow(amountPrecision.toDouble())).toString()

            //如果保证金不够则不让滑动，并且提示保证金不足
            if (BigDecimal(amountTotal) < BigDecimal(minValue)) {
                mHeadBinding.fprogress.init(false)
                ToastUtils.showToast(getString(R.string.insufficient_margin))
                return@label
            }
            val amount = amountTotal.mul((rate / 100f).toString())
            mHeadBinding.amount2.setText(
                BigDecimal(amount).setScale(
                    amountPrecision,
                    BigDecimal.ROUND_HALF_UP
                ).toPlainString()
            )
            null
        }
        //GradientDrawableUtils.setBoardColor(mHeadBinding.futuresTvDate, color);
    }

    override fun initViewPager() {
        //jiang
        mHeadBinding.rightContainer.setUnit(2)
        fragments = ArrayList()
        fragments.add(FuturesMoneyListFragment())
        fragments.add(FuturesOrdersFragment())
        fragments.add(FuturesCompleteFragment())
        val title: MutableList<String> = ArrayList()
        title.add(getXmlString(R.string.exchange_money))
        title.add(getXmlString(R.string.exchange_order))
        title.add(getXmlString(R.string.exchange_complete))
        val baseFragmentAdapter = BaseFragmentAdapter(
            childFragmentManager,
            fragments, title
        )
        mHeadBinding.viewPager.adapter = baseFragmentAdapter
        mHeadBinding.viewPagerTitle.initTitles(title)
        mHeadBinding.viewPagerTitle.bindViewpager(mHeadBinding.viewPager)
        mHeadBinding.viewPager.offscreenPageLimit = 2
    }

    override fun initListenter() {
        //super.initListenter();
        priceTextchange = object : FotaTextWatch() {
            override fun onTextChanged(s: String) {
                validMaxValue()
                if (isLeverChange) {
                    isLeverChange = false
                    return
                }
                if (!FotaApplication.getLoginSrtatus()) {
                    return
                }
                val price: String = if (isLimit) {
                    mHeadBinding.price2.text.toString()
                } else {
                    currentPrice
                }
                if (topInfo == null) {
                    return
                }
                val moneyUnit = price.divide(level, pricePrecision)
                val amountTotal = topInfo!!.available.divide(moneyUnit, amountPrecision)
                val rate = mHeadBinding.amount2.text.toString().divide(amountTotal, 2)
                mHeadBinding.fprogress.progress = Math.round(java.lang.Float.valueOf(rate) * 100)
            }
        }
        mHeadBinding.price2.addTextChangedListener(priceTextchange)
        mHeadBinding.amount2.addTextChangedListener(priceTextchange)
    }
    //    @Override
    //    protected void validMaxInfo() {
    //        validMaxMinPrice();
    //        validMaxValue();
    //    }
    /**
     * 和价格挂钩
     * 和可用保证金挂钩
     */
    private fun validMaxValue() {
        var priceOk = true
        if (isLimit) {
            priceOk = Pub.GetDouble(mHeadBinding.price2.text.toString()) > 0
        }
        val valueOk = Pub.GetDouble(mHeadBinding.amount2.text.toString()) > 0
        if (UserLoginUtil.havaUser() && priceOk && valueOk) {
            insertFormInfo()
            //获取保证金需要
            presenter.preciseMargin()
        } else {
            mHeadBinding.maxAmount2.text = "--"
            mHeadBinding.preciseMargin.text = "--"
        }
    }

    override fun changeWidth() {
        mHeadBinding.priceType2.post {
            val textWith = UIUtil.getTextWidth(
                context,
                getXmlString(R.string.exchange_his_price),
                14
            ).toInt()
            val width = UIUtil.dip2px(context, 12.0) + textWith
            val lpPrice = mHeadBinding.priceType2.layoutParams
            lpPrice.width = width
            mHeadBinding.priceType.layoutParams = lpPrice
            val lpMoney = mHeadBinding.amount2Tip.layoutParams
            lpMoney.width = width
            mHeadBinding.amount2Tip.layoutParams = lpMoney
        }
    }

    /**
     * 最高限卖价
     * 和买卖方向挂钩
     * 和现货指数挂钩
     */
    private fun validMaxMinPrice() {
        val priceDiv =
            if (isBuy) Pub.GetDouble(spotPrice) * 1.05 else Pub.GetDouble(spotPrice) * 0.95
        mHeadBinding.maxUsdt2Tip.text =
            if (isBuy) getXmlString(R.string.exchange_heigh_price) else getXmlString(
                R.string.exchange_low_price
            )
        // Buy DOWN  Sell FLOOR
        mHeadBinding.maxUsdt2.text = Pub.getPriceFormat(
            priceDiv,
            contractMaxMinPricePrecision,
            if (isBuy) RoundingMode.DOWN else RoundingMode.UP
        )
    }

    private val price: Double
        private get() {
            val price = Pub.GetDouble(mHeadBinding.price2.text.toString())
            return if (!isLimit) {
                Pub.GetDouble(mHeadBinding.rightContainer.currentPrice)
            } else price
        }

    override fun getMaxMoney(): Double {
        if (topInfo == null) {
            return 0.0
        }
        return if (isBuy) Pub.GetDouble(topInfo!!.maxAskAmount) else Pub.GetDouble(topInfo!!.maxBidAmount)
    }

    override fun refreshCurrency() {
        if (presenter.selectContact == null) {
            return
        }
        mHeadBinding.money2Unit.text = presenter.selectContact!!.getAssetName()
        mHeadBinding.amount2.filters = arrayOf<InputFilter>(
            DecimalDigitsInputFilter(
                amountPrecision
            )
        )
        mHeadBinding.price2.filters =
            arrayOf<InputFilter>(DecimalDigitsInputFilter(pricePrecision))

        //jiang
        mHeadBinding.rightContainer.resetTicker()
        mHeadBinding.rightContainer.setType(2, presenter.selectContact!!.getContractName())
        val futureContractBean = presenter.selectContact

//        Log.i("nidongliang", "futureBean: $futureContractBean")
//        if (futureContractBean.getContractType() == 3){
//            mHeadBinding.futuresCurrency.text = "${futureContractBean.symbol} ${getString(R.string.perp)}"
//        }else{
            mHeadBinding.futuresCurrency.text = futureContractBean!!.getContractName()
//        }

        mHeadBinding.futuresTvLever.text = "X" + presenter.selectContact!!.getLever()
        if (Pub.isListExists(fragments)) {
            if (fragments[0] is FuturesMoneyListFragment) {
                (fragments[0] as FuturesMoneyListFragment).adapterNotifyDataSetChanged()
            }
        }
        Glide.with(context).load(presenter.selectParent!!.iconUrl)
            .into(mHeadBinding.futureIcon)
        if (Pub.isStringEmpty(mHeadBinding.amount2.text.toString())) {
            mHeadBinding.amount2.setText(defaultAmount)
        }
        mHeadBinding.fprogress.init(true)
    }

    override fun onRefreshTicker(price: CurrentPriceBean) {
        super.onRefreshTicker(price)
        currentPrice = price.price?:"0.0"
        //受市场价影响
        if (!isLimit) {
            //暂时不需要设置
            //mHeadBinding.price2.setText(mHeadBinding.price.getText().toString());
        }
        if (mHeadBinding.imbedSpot.visibility != View.VISIBLE) {
            mHeadBinding.imbedSpot.visibility = View.VISIBLE
        }
    }

    var currentPrice = "0.00"
        private set

    fun getPriceByAsset(id: String?): String {

//        for (ContractAssetBean bean : getPresenter().getAllList()) {
//            for (FutureContractBean model : bean.getContent()) {
//                if (id.equals(model.getContractId())){
//                    return model.
//                }
//            }
//        }
//        getPresenter().getAllList();
        return ""
    }

    override fun getAssetId(): String {
        return if (presenter == null || presenter.selectContact == null) {
            ""
        } else presenter.selectContact!!.contractId
    }

    override fun onRefreshTimelineData() {
        resetFreshTimelineData()
    }

    override fun resetFreshTimelineData() {
        setDelievery()
        val chartList = FotaApplication.getInstance().getListFromTimesByType(2)
        val spotList = FotaApplication.getInstance().getListFromTimesByType(1)
        //        freshChartView(chartList, spotList);
        //合约去掉现货指数
        freshChartView(chartList, null)
        //当前现货指数
        if (spotList != null && spotList.size > 0) {
            val length = spotList.size
            val price = spotList[length - 1].close
            spotPrice = price.toString() + ""
            var tempSpotPrice: String? = ""
            val entity = FotaApplication.getInstance().holdingEntity
            tempSpotPrice = Pub.getPriceStringForLengthRound(price, entity.decimal)
            //更新下文本的内容
            UIUtil.setText(mHeadBinding.imbedTickerSpot, tempSpotPrice)
        }
        validMaxMinPrice()
    }

    private fun freshChartView(
        chartList: List<ChartLineEntity>?,
        spotList: List<ChartLineEntity>?
    ) {
        if (chartList == null) {
            onNoDataCallBack(0)
            return
        }
        time15Data.clear()
        for (i in chartList.indices) {
            val m = chartList[i] ?: continue
            val data = BeanChangeFactory.createNewHisData(m)
            time15Data.add(data)
        }
        spotData.clear()
        if (spotList != null) {
            for (i in spotList.indices) {
                val m = spotList[i]
                val data = BeanChangeFactory.createNewHisData(m)
                spotData.add(data)
            }
        }
        mHeadBinding.imbedNarrowChart.initData(time15Data, spotData)
        val entity = FotaApplication.getInstance().holdingEntity
        mHeadBinding.tline.setmDigits(entity.decimal)
        mHeadBinding.tline.initData(time15Data, spotData)
        if (entity.holdingPrice != -1.0) {
            mHeadBinding.tline.setLimitLine(entity.holdingPrice, entity.holdingDescription)
        }
        if (time15Data != null && time15Data.size > 0) {
            val hour24Close = time15Data[0].close
            mHeadBinding.tline.setLastClose(hour24Close)
        }
    }

    override fun onRefreshKlineData(isAdd: Boolean) {
        val chartList = FotaApplication.getInstance().getListFromKlinesByType(2)
        if (chartList == null || chartList.size == 0) {
            return
        }
        val holdingEntity = FotaApplication.getInstance().holdingEntity
        if (holdingEntity != null && holdingEntity.holdingPrice != -1.0) {
            mHeadBinding.kline.setLimitLine(
                holdingEntity.holdingPrice,
                holdingEntity.holdingDescription
            )
        }
        if (isAdd) { //add 直接重刷
            klineDataConvert(chartList)
            //            mHeadBinding.kline.addData(klineData, spotData);
            mHeadBinding.kline.addData(klineData, null)
            if (time15Data != null && time15Data.size > 0) {
                val hour24Close = time15Data[0].close
                mHeadBinding.kline.setLastClose(hour24Close)
            }
        } else {
            var lastData = -1f
            var lastSpot = -1f
            var volume = 0f
            val length = chartList.size
            val m = chartList[length - 1]
            val data = BeanChangeFactory.createNewHisData(m)
            lastData = data.close.toFloat()
            volume = data.vol
            val spotList = FotaApplication.getInstance().getListFromKlinesByType(1)
            if (spotList != null && spotList.size > 0) {
                val spotSize = spotList.size
                val m1 = spotList[spotSize - 1]
                val data1 = BeanChangeFactory.createNewHisData(m1)
                lastSpot = data1.close.toFloat()
            }
            mHeadBinding.kline.refreshData(data, volume, lastSpot)
        }
    }

    override fun resetFreshKlineData() {
        val chartList = FotaApplication.getInstance().getListFromKlinesByType(2)
        val holdingEntity = FotaApplication.getInstance().holdingEntity
        if (chartList == null) {
            onNoDataCallBack(1)
            return
        }
        klineDataConvert(chartList)
        mHeadBinding.kline.setNeedMoveToLast(true)
        mHeadBinding.kline.setmDigits(holdingEntity!!.decimal)
        //        mHeadBinding.kline.initData(klineData, spotData);
        mHeadBinding.kline.initData(klineData, null)
        if (holdingEntity != null && holdingEntity.holdingPrice != -1.0) {
            mHeadBinding.kline.setLimitLine(
                holdingEntity.holdingPrice,
                holdingEntity.holdingDescription
            )
        }
        if (time15Data != null && time15Data.size > 0) {
            val hour24Close = time15Data[0].close
            mHeadBinding.kline.setLastClose(hour24Close)
        }
    }

    override fun notifyFromPresenter(action: Int) {
        when (action) {
            ORDER_SUCCESS -> mHeadBinding.amount2.setText(defaultAmount)
            else -> super.event(action)
        }
    }

    override fun klineDataConvert(chartList: List<ChartLineEntity>) {
        super.klineDataConvert(chartList)
        val spotList = FotaApplication.getInstance().getListFromKlinesByType(1)
        spotData.clear()
        if (spotList != null) {
            for (i in spotList.indices) {
                val m = spotList[i]
                val data = BeanChangeFactory.createNewHisData(m)
                spotData.add(data)
            }
        }
    }

    override fun fullScreen() {
        if (presenter == null || presenter.selectContact == null) {
            return
        }
        val intent = Intent(mActivity, FullScreenKlineActivity::class.java)
        val args = Bundle()
        args.putString("symbol", presenter.selectContact!!.getContractName())
        val id = Pub.GetInt(presenter.selectContact!!.getContractId())
        args.putInt("id", id)
        args.putInt("type", 2)
        args.putInt("period", currentPeriodIndex)
        intent.putExtras(args)
        startActivity(intent)
    }

    override fun onRefreshDeliveryOrHold() {
        val holdingEntity = FotaApplication.getInstance().holdingEntity
        if (holdingEntity != null && holdingEntity.holdingPrice != -1.0) {
            mHeadBinding.kline.setLimitLine(
                holdingEntity.holdingPrice,
                holdingEntity.holdingDescription
            )
            mHeadBinding.tline.setLimitLine(
                holdingEntity.holdingPrice,
                holdingEntity.holdingDescription
            )
        }
        setDelievery()
        //由3 交割中 -- 2正常，need onRefresh ，然后复原
        if (holdingEntity!!.isStatusChange) {
            onRefresh()
            holdingEntity.isStatusChange = false
        }

//        entityId = holdingEntity.getId();
//        futureStr = holdingEntity.getName();
//        FutureItemEntity entity = new FutureItemEntity(futureStr);
//        entity.setEntityId(entityId);
//        entity.setEntityType(entityType);
//        getPresenter().setBean(entity);
    }

    private fun setDelievery() {
        var tips: String? = ""
        val holdingEntity = FotaApplication.getInstance().holdingEntity
        val days = holdingEntity.futureLimtDays
        if (holdingEntity.status == 3) {
            tips = getString(R.string.exchange_order_trading)
        } else if (!TextUtils.isEmpty(days)) {
            tips = String.format(getString(R.string.market_deadline), days)
            var textResource = R.string.days
            when (holdingEntity.deliveryType) {
                1 -> textResource = R.string.days
                2 -> textResource = R.string.hours
                3 -> textResource = R.string.minutes
                4 -> textResource = R.string.seconds
            }
            tips += CommonUtils.getResouceString(context, textResource)
        }
        mHeadBinding.futuresTvDate.text = tips
    }

    var leverDialog: LeverDialog? = null
    override fun onClick(v: View) {
        when (v.id) {
            R.id.futures_tv_lever -> {
                if (!FotaApplication.getLoginSrtatus()) {
                    FtRounts.toLogin(requireContext())
                    return
                }
                leverDialog = LeverDialog(requireContext())
                leverDialog!!.onClickListener = View.OnClickListener {
                    presenter.setLever(
                        Integer.valueOf(assetId),
                        assetName,
                        leverDialog!!.getLever()
                    )
                }
                leverDialog!!.show()
                leverDialog!!.setLever(Integer.valueOf(level))
            }
            R.id.img_type_change2 -> {
                isKline = !isKline
                if (isKline) {
                    mHeadBinding.imgTypeChange2.setImageResource(
                        Pub.getThemeResource(
                            context,
                            R.attr.chart_time_line
                        )
                    )
                } else {
                    mHeadBinding.imgTypeChange2.setImageResource(
                        Pub.getThemeResource(
                            context,
                            R.attr.chart_kline
                        )
                    )
                }
                changeKtline()
            }
            R.id.amount2_tip -> {
                mHeadBinding.amount2.requestFocus()
                KeyBoardUtils.openKeybord(mHeadBinding.amount2, context)
            }
            R.id.futures_top_info -> showTopPop()
            R.id.futures_change_currency, R.id.futures_currency -> showPopWindow()
            R.id.select_buy2 -> {
                isBuy = true
                refreshBuy()
            }
            R.id.select_sell2 -> {
                isBuy = false
                refreshBuy()
            }
            R.id.btn_buy_sell2 -> {
                if (!UserLoginUtil.havaUser()) {
                    UserLoginUtil.checkLogin(context)
                    return
                }
                if (isLimit) {
                    if (Pub.GetDouble(mHeadBinding.price2.text.toString()) <= 0) {
                        showToast(R.string.exchange_toast_inputprice)
                        return
                    }
                }
                if (Pub.GetDouble(mHeadBinding.amount2.text.toString()) <= 0) {
                    showToast(R.string.future_empty_value)
                    return
                }
                tradeToPresenter(UserLoginUtil.getCapital())
            }
            R.id.price_type2, R.id.price_other_2 -> {
                isLimit = !isLimit
                refreshPriceType()
            }
            R.id.iv_calc -> {
                val calcIntent = Intent(requireContext(), FuturesCalcActivity::class.java)
                calcIntent.putExtra("coinName", presenter.selectContact!!.getAssetName())
                calcIntent.putExtra("amountPercision", amountPrecision)
                startActivity(calcIntent)
            }
            else -> super.onClick(v)
        }
    }
    private fun showTopPop() {
        mHeadBinding.futuresTopInfoArrow.reverse()
        if (popupTopWindow == null) {
            popupTopWindow = FutureTopWindow(context)
        }
        popupTopWindow!!.setCloseListener { mHeadBinding.futuresTopInfoArrow.reset() }
        popupTopWindow!!.showAsDropDown(
            mHeadBinding.futuresTopInfo, topInfo
        )
    }

    override fun onRefreshDepth(
        buys: List<EntrustBean>,
        sells: List<EntrustBean>,
        precisions: List<String>?,
        dollarEvaluation: Float?
    ) {
        super.onRefreshDepth(buys, sells, precisions, dollarEvaluation)
        //受市场价影响
//        if (!isLimit) {
//            mHeadBinding.price2.setText(mHeadBinding.price.getText().toString());
//        }
        if (!onRefreshDepthReqed) {
            validMaxValue()
            onRefreshDepthReqed = true
        }
    }

    /**
     * 0-对手价，1-指定价
     * 刷新市价和现价
     */
    override fun refreshPriceType() {
        UIUtil.setVisibility(mHeadBinding.priceOther2, !isLimit)
        UIUtil.setVisibility(mHeadBinding.price2, isLimit)
        UIUtil.setVisibility(mHeadBinding.price2Unit, isLimit)
        UIUtil.setText(
            mHeadBinding.priceTypeTv2,
            if (isLimit) getString(R.string.exchange_limit_price) else getString(
                R.string.exchange_his_price
            )
        )
        validMaxValue()
    }

    override fun tradeToPresenter(fundCode: String) {
        //1-限价单, 2-市价单
        insertFormInfo()
        presenter.submit(fundCode)
    }

    private fun insertFormInfo() {
        presenter.model.priceType = if (isLimit) 1 else 2
        if (isLimit) {
            presenter.model.price = mHeadBinding.price2.text.toString()
        } else {
            presenter.model.price = null
        }
        presenter.model.entrustValue = mHeadBinding.amount2.text.toString()
    }

    override fun refreshBuy() {
        GradientDrawableUtils.setBgAlpha(mHeadBinding.selectBuy2, 30)
        GradientDrawableUtils.setBgAlpha(mHeadBinding.selectSell2, 30)
        GradientDrawableUtils.setBgColor(
            mHeadBinding.selectBuy2, if (isBuy) AppConfigs.getUpColor() else Pub.getColor(
                context, R.attr.bg_color
            )
        )
        mHeadBinding.selectBuy2.setTextColor(
            if (isBuy) AppConfigs.getUpColor() else Pub.getColor(
                context, R.attr.font_color4
            )
        )
        GradientDrawableUtils.setBgColor(
            mHeadBinding.selectSell2, if (!isBuy) AppConfigs.getDownColor() else Pub.getColor(
                context, R.attr.bg_color
            )
        )
        mHeadBinding.selectSell2.setTextColor(
            if (!isBuy) AppConfigs.getDownColor() else Pub.getColor(
                context, R.attr.font_color4
            )
        )
        UIUtil.setRoundCornerBg(mHeadBinding.btnBuySell2, AppConfigs.getColor(isBuy))
        presenter.model.setIsBuy(isBuy)
        mHeadBinding.priceOther2.text =
            if (isBuy) getXmlString(R.string.exchange_optimal) else getXmlString(R.string.exchange_optimal)
        mHeadBinding.btnBuySell2.text =
            if (isBuy) getXmlString(R.string.exchange_buy_wishheigh) else getXmlString(
                R.string.exchange_sell_wishlow
            )
        validMaxMinPrice()

        //validMaxValue(); 直接设置setPreciseMargin(preciseMargin);
        setPreciseMargin(preciseMargin)
    }

    /**
     * 获取当前价格的小数位数
     *
     * @return
     */
    override fun getPricePrecision(): Int {
        return if (presenter.selectParent != null) {
            presenter.selectParent!!.contractTradePricePrecision
        } else 2
    }

    /**
     * 获取当前价格的小数位数
     *
     * @return
     */
    override fun getDefaultAmount(): String {
        return if (presenter.selectParent != null) {
            presenter.selectParent!!.defaultAmount
        } else ""
    }

    /**
     * 获取当前数量的小数位数
     *
     * @return
     */
    override fun getAmountPrecision(): Int {
        return if (presenter.selectParent != null) {
            presenter.selectParent!!.contractTradeAmountPrecision
        } else 2
    }

    /**
     * 最低现买价
     *
     * @return
     */
    val contractMaxMinPricePrecision: Int
        get() = if (presenter.selectParent != null) {
            presenter.selectParent!!.contractMaxMinPricePrecision
        } else 1

    /**
     * 获取当前数量的小数位数
     *
     * @return
     */
    val contractMaxValuePrecision: Int
        get() = 3

    override fun showPopWindow() {
        mHeadBinding.futuresChangeCurrencyArrow.reverse()
        if (!Pub.isListExists(presenter.allList)) {
            return
        }
        val popupWindow = SpinerPopWindow3(context)
        popupWindow.setOnPopListener { ftKeyValue, position ->
            val model = ftKeyValue as FutureContractBean
            KeyBoardUtils.closeKeybord(context)
            clearEditText()
            presenter.setSelectContact(model.getParent(), model)


        }
        popupWindow.setCloseListener { mHeadBinding.futuresChangeCurrencyArrow.reset() }
        popupWindow.showAsDropDown(
            mHeadBinding.futuresTitle,
            presenter.allList,
            presenter.selectParent,
            presenter.selectContact
        )
    }

    override fun onRefresh() {
        super.onRefresh()
        onRefreshDepthReqed = false
        if (!UserLoginUtil.havaUser()) {
            topInfo = null
            UIUtil.setText(mHeadBinding.preciseMargin, "--")
        }
        setTopInfo()
        presenter.contactList
    }

    override fun setContractAccount(map: FutureTopInfoBean) {
        //保证金率
        topInfo = map
        setTopInfo()
        if (popupTopWindow != null) {
            popupTopWindow!!.setData(map)
        }
        //validMaxValue();
    }

    private fun setTopInfo() {
        if (topInfo == null) {
            mHeadBinding.futuresRights.text = "--"
            mHeadBinding.futuresFdYk.text = "--"
            return
        }
        mHeadBinding.futuresRights.text = topInfo!!.total
        mHeadBinding.futuresFdYk.text = topInfo!!.floatProfit
    }

    fun removeMoney(model: FuturesMoneyBean?) {
        TradeUtils.getInstance()
            .validPassword(context, mRequestCode, object : ExchangePasswordListener {
                override fun noPassword() {
                    presenter.removeMoney(model!!, UserLoginUtil.getCapital())
                }

                override fun showPasswordDialog() {
                    if (holdingActivity.isFinishing) {
                        return
                    }
                    val dialog = PasswordDialog(context)
                    dialog.setListener { fundCode ->
                        TradeUtils.getInstance().changePasswordToToken(
                            context, fundCode
                        ) { token -> presenter.removeMoney(model!!, token) }
                    }
                    dialog.show()
                }
            })
    }

    override fun setContractDelivery(map: BtbMap) {}
    override fun setPreciseMargin(map: BtbMap?) {
        preciseMargin = map
        if (map == null) {
            UIUtil.setText(mHeadBinding.preciseMargin, "--")
            return
        }
        if (isBuy) {
            UIUtil.setText(mHeadBinding.preciseMargin, map["bid"], "--")
        } else {
            UIUtil.setText(mHeadBinding.preciseMargin, map["ask"], "--")
        }
    }

    override fun onLeverChange() {
        isLeverChange = true
        leverDialog!!.dismiss()
        val price: String = if (isLimit) {
            mHeadBinding.price2.text.toString()
        } else {
            currentPrice
        }
        val moneyUnit = price.divide(leverDialog!!.getLever().toString() + "", pricePrecision)
        val amountTotal = topInfo!!.available.divide(moneyUnit, amountPrecision)
        val amount = amountTotal.mul((mHeadBinding.fprogress.lever.toFloat() / 100f).toString())

//        Log.i("nidongliang", "unit: " + moneyUnit + "aamount: " + amountTotal + "")
        mHeadBinding.amount2.setText(
            BigDecimal(amount).setScale(
                amountPrecision,
                BigDecimal.ROUND_HALF_UP
            ).toPlainString()
        )
    }

    override fun clearEditText() {
        mHeadBinding.price2.setText("")
        mHeadBinding.amount2.setText("")
        mHeadBinding.exchangeChangeCurrencyArrow.requestFocus()
        //        KeyBoardUtils.closeKeybord(mHeadBinding.price2, getContext());
//        KeyBoardUtils.closeKeybord(mHeadBinding.amount2, getContext());
    }

    override fun onClickItem(bean: EntrustBean) {
        isLimit = true
        refreshPriceType()
        validMaxValue()
        mHeadBinding.price2.setText(bean.price)
    }

    override fun onTickClick(currentPrice: CurrentPriceBean) {
        isLimit = true
        refreshPriceType()
        //validMaxValue();
        mHeadBinding.price2.setText(currentPrice.price)
    }

    val level: String
        get() = if (presenter.selectContact != null) {
            presenter.selectContact!!.getLever()
        } else "10"

    companion object {
        fun newInstance(
            assetName: String?,
            model: FutureContractBean?,
            isBuy: Boolean
        ): FuturesFragment {
            val args = Bundle()
            args.putString(BundleKeys.KEY, assetName)
            args.putSerializable(BundleKeys.MODEL, model)
            args.putBoolean("isBuy", isBuy)
            val fragment = FuturesFragment()
            fragment.arguments = args
            return fragment
        }
    }
}