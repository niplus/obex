package com.fota.android.moudles.futures.view

import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fota.android.R
import com.fota.android.app.ConstantsReq
import com.fota.android.app.FotaApplication
import com.fota.android.common.bean.BeanChangeFactory
import com.fota.android.commonlib.base.AppConfigs
import com.fota.android.commonlib.utils.Pub
import com.fota.android.commonlib.utils.UIUtil
import com.fota.android.core.mvvmbase.BaseFragment
import com.fota.android.databinding.FragmentFutureBinding
import com.fota.android.moudles.futures.viewmodel.FutureViewModel
import com.fota.android.moudles.market.bean.ChartLineEntity
import com.fota.android.moudles.market.bean.FutureItemEntity
import com.fota.android.utils.ToastUtils
import com.fota.android.utils.divide
import com.fota.android.utils.mul
import com.guoziwei.fota.chart.view.fota.FotaBigKLineBarChartView
import com.guoziwei.fota.chart.view.fota.ImBeddedTimeLineBarChartView
import com.guoziwei.fota.model.HisData
import com.ndl.lib_common.utils.LiveDataBus
import java.math.BigDecimal
import kotlin.math.pow


/**
 *
 */
open class FutureFragment : BaseFragment<FragmentFutureBinding, FutureViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_future
    }

    override fun initData() {
        viewModel.apply {
            contractListLiveData.observe(this@FutureFragment, Observer {
                //将永续替换
                allList?.forEach {
                    it!!.content.forEach { contract ->
                        if (contract.contractType == 3 && contract.contractName.contains("永续")) {
                            contract.contractName = contract.contractName.replace(
                                "永续", getString(
                                    com.fota.android.R.string.perp
                                )
                            )
                        }
                    }
                }

                getTimeLineDatas(ConstantsReq.TRADE_TYPE_CONTACT, selectContract.get()!!.contractId.toInt(), "1m")
            })

            time15Data.observe(this@FutureFragment, Observer {
                freshChartView(it)
            })
            klineData.observe(this@FutureFragment, Observer {
                resetFreshKlineData(it)
            })

            selectContract.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    val contractPrecision = viewModel.selectParent!!.contractPrecision
                    dataBinding.rightContainer.setSmallLargePrecision(contractPrecision.split(','))
                }
            })

            getContactList()
        }
    }

    /**
     * 获取主题色
     *
     * @param attr
     * @return
     */
    private fun getThemeColor(attr: Int): Int {
        return Pub.getColor(context, attr)
    }

    override fun initComp() {
        dataBinding.apply {
            model = viewModel

            kline.setChartType(FotaBigKLineBarChartView.ChartType.FUTURE)
            tline.setChartType(ImBeddedTimeLineBarChartView.ChartType.FUTURE)

            val color =
                if (AppConfigs.isWhiteTheme()) getThemeColor(R.attr.font_color) else getThemeColor(
                    R.attr.font_color3
                )
            futuresTvDate.setTextColor(color)
            UIUtil.setRectangleBorderBg(futuresTvLever, color)
            futuresTvLever.setTextColor(color)

            //初始化数量拖动条
            fprogress.isLever = false
            fprogress.leverChangeListener = label@{ rate: Int ->
                if (!FotaApplication.getLoginSrtatus()) {
                    return@label
                }
                val price: String = if (viewModel.isLimit) {
                    price2.text.toString()
                } else {
                    viewModel.currentPrice
                }
                val moneyUnit = price.divide(viewModel.level, viewModel.pricePrecision)
                val amountTotal = viewModel.topInfo!!.available.divide(moneyUnit, viewModel.amountPrecision)
                val minValue: String = (1f / 10.0.pow(viewModel.amountPrecision.toDouble())).toString()

                //如果保证金不够则不让滑动，并且提示保证金不足
                if (BigDecimal(amountTotal) < BigDecimal(minValue)) {
                    fprogress.init(false)
                    ToastUtils.showToast(getString(R.string.insufficient_margin))
                    return@label
                }
                val amount = amountTotal.mul((rate / 100f).toString())
                amount2.setText(
                    BigDecimal(amount).setScale(
                        viewModel.amountPrecision,
                        BigDecimal.ROUND_HALF_UP
                    ).toPlainString()
                )
                null
            }

            LiveDataBus.getBus<FutureItemEntity>("trade").observe(this@FutureFragment,  Observer{ entity->
                if (entity.entityType == 2) {
                    if (viewModel.contractListLiveData.value != null) {
                        viewModel.contractListLiveData.value!!.forEach {
                            it.content.forEach { model->
                                if (model.contractId == entity.entityId.toString()){
                                    viewModel.setSelectContact(it, model)
                                    return@Observer
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    override fun createViewModel(): FutureViewModel {
        return ViewModelProvider(this).get(FutureViewModel::class.java)
    }

    //刷新分时图
    private fun freshChartView(chartList: List<HisData>?) {
        if (chartList == null) {
            dataBinding.tline.initData(null, null)
            return
        }
        dataBinding.imbedNarrowChart.initData(chartList, null)
        val entity = FotaApplication.getInstance().holdingEntity
        dataBinding.tline.setmDigits(entity.decimal)
        dataBinding.tline.initData(chartList, null)
        if (entity.holdingPrice != -1.0) {
            dataBinding.tline.setLimitLine(entity.holdingPrice, entity.holdingDescription)
        }
        if (chartList.isNotEmpty()) {
            val hour24Close = chartList[0].close
            dataBinding.tline.setLastClose(hour24Close)
        }
    }

    fun resetFreshKlineData(klineData: List<HisData>) {
        val chartList = FotaApplication.getInstance().getListFromKlinesByType(2)
        val holdingEntity = FotaApplication.getInstance().holdingEntity
        if (chartList == null) {
            dataBinding.kline.initData(null, null)
            return
        }
//        klineDataConvert(chartList)
        dataBinding.kline.setNeedMoveToLast(true)
        dataBinding.kline.setmDigits(holdingEntity!!.decimal)
        //        mHeadBinding.kline.initData(klineData, spotData);
        dataBinding.kline.initData(klineData, null)
        if (holdingEntity.holdingPrice != -1.0) {
            dataBinding.kline.setLimitLine(
                holdingEntity.holdingPrice,
                holdingEntity.holdingDescription
            )
        }
        if (viewModel.time15Data.value != null && viewModel.time15Data.value!!.isNotEmpty()) {
            val hour24Close = viewModel.time15Data.value!![0].close
            dataBinding.kline.setLastClose(hour24Close)
        }
    }

}