package com.fota.android.moudles.futures.viewmodel

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fota.android.app.ConstantsReq
import com.fota.android.app.FotaApplication
import com.fota.android.app.SocketKey
import com.fota.android.common.bean.BeanChangeFactory
import com.fota.android.common.bean.exchange.ExchangeBody
import com.fota.android.common.bean.exchange.ExchangeEntity
import com.fota.android.common.bean.home.DepthBean
import com.fota.android.commonlib.http.BaseHttpEntity
import com.fota.android.commonlib.http.exception.ApiException
import com.fota.android.commonlib.http.rx.CommonSubscriber
import com.fota.android.commonlib.http.rx.CommonTransformer
import com.fota.android.commonlib.http.rx.NothingTransformer
import com.fota.android.commonlib.utils.Pub
import com.fota.android.core.base.BtbMap
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.http.ContractAssetBean
import com.fota.android.http.Http
import com.fota.android.moudles.futures.FutureContractBean
import com.fota.android.moudles.futures.FutureTopInfoBean
import com.fota.android.moudles.futures.money.FuturesMoneyBean
import com.fota.android.moudles.market.bean.ChartLineEntity
import com.fota.android.moudles.market.bean.FutureItemEntity
import com.fota.android.moudles.market.bean.MarketKLineBean
import com.fota.android.moudles.market.bean.MarketTimeLineBean
import com.fota.android.socket.IWebSocketSubject
import com.fota.android.socket.WebSocketEntity
import com.fota.android.socket.params.SocketEntrustParam
import com.fota.android.socket.params.SocketMarketParam
import com.guoziwei.fota.model.HisData
import java.util.*
import kotlin.collections.ArrayList

class FutureViewModel : BaseViewModel() {

    /**
     * 选择的币种的对应列表的选中项
     */
    var selectContact: FutureContractBean? = null
    var selectParent: ContractAssetBean? = null

    //private String key;
    var allList: List<ContractAssetBean?>? = null
    private var fromContact: FutureContractBean? = null
    private var client: IWebSocketSubject = FotaApplication.getInstance().client

    private var model: ExchangeEntity? = null

    var contractListLiveData = MutableLiveData<List<ContractAssetBean>>()
    var selectContract = ObservableField<FutureContractBean>()

    var time15Data = MutableLiveData<List<HisData>>()
    var klineData = MutableLiveData<List<HisData>>()
    var depthLiveData = MutableLiveData<DepthBean>()

    //是否限制小数位
    var isLimit = false
    //当前价格
    var currentPrice = "0.00"

    //杠杆
    val level: String
        get() = if (selectContract.get() != null) {
            selectContract.get()!!.lever
        } else "10"

    //当前价格小数位
    val pricePrecision: Int
        get() = if (selectParent != null) {
            selectParent!!.contractTradePricePrecision
        } else 2

    val amountPrecision: Int
        get() = if (selectParent != null) {
            selectParent!!.contractTradeAmountPrecision
        } else 2

    var topInfo: FutureTopInfoBean? = null

//    fun removeChannel(){
//        client.removeChannel(SocketKey.MineEntrustReqType, this)
//        client.removeChannel(SocketKey.DELIVERY_TIME_CHANGED, this)
//        client.removeChannel(SocketKey.POSITION_LINE, this)
//        client.removeChannel(SocketKey.TradeWeiTuoReqType, this)
//        client.removeChannel(SocketKey.FUTURE_TOP, this)
//    }

     fun submit(fundCode: String) {
        if (selectContact == null) {
            return
        }
        model!!.contractId = selectContact!!.contractId
        model!!.contractName = selectContact!!.contractName
        model!!.lever = selectContact!!.lever
        val body = ExchangeBody()
        body.obj = model
        Http.getExchangeService().makeContractOrder(body)
            .compose(NothingTransformer())
            .subscribe(object :
                CommonSubscriber<BaseHttpEntity?>(FotaApplication.getInstance()) {
                override fun onNext(t: BaseHttpEntity?) {
//                    if (view != null) {
//                        showTopInfo(model)
//                        view?.notifyFromPresenter(ExchangeFragment.ORDER_SUCCESS)
//                    }
                }
            })
    }

    fun preciseMargin() {
        if (selectContact == null) {
            return
        }
        model!!.contractId = selectContact!!.contractId
        model!!.contractName = selectContact!!.contractName
        val body = ExchangeBody()
        body.obj = model
        Http.getExchangeService().preciseMargin(body)
            .compose(CommonTransformer())
            .subscribe(object : CommonSubscriber<BtbMap?>() {
                override fun onNext(map: BtbMap?) {
//                    view?.setPreciseMargin(map)
                }

                override fun onError(e: ApiException) {
                    //super.onError(e);
                }
            })
    }

    fun removeMoney(model: FuturesMoneyBean) {
        val modelPost = ExchangeEntity()
        modelPost.priceType = 2
        modelPost.totalAmount = model.amount
        modelPost.setIsBuy(!model.isBuy)
        modelPost.contractId = model.contractId
        modelPost.contractName = model.contractName
        val body = ExchangeBody()
        body.obj = modelPost
        Http.getExchangeService().makeContractOrder(body)
            .compose(NothingTransformer())
            .subscribe(object :
                CommonSubscriber<BaseHttpEntity?>(FotaApplication.getInstance()) {
                override fun onNext(baseHttpEntity: BaseHttpEntity?) {
//                    if (view != null) {
//                        model.isCanceled = true
//                        showTopInfo(modelPost)
//                    }
                }
            })
    }

    fun getContactList(){
        Http.getExchangeService().contractTree
            .compose(CommonTransformer())
            .subscribe(object : CommonSubscriber<List<ContractAssetBean>>() {
                override fun onNext(list: List<ContractAssetBean>) {
                    allList = list

                    //都没有选中第一个
                    if (!list.isNullOrEmpty() && selectContract.get() == null) {
                        Log.i(
                            "nidongliang",
                            "select: ${list[0].content[0].contractName}, ${list[0].content[0].assetName}"
                        )
                        setSelectContact(list[0], list[0].content[0])
                        selectContract.set(list[0].content[0])
                    }

                    contractListLiveData.value = list
                    //选中上次选中的
                    if (selectParent != null && selectContact != null) {
                        val parent = findContractParent(selectParent!!.name, list)
                        if (parent != null) {
                            val item = findContractItem(selectContact, parent)
                            if (item != null) {
                                setSelectContact(parent, item)
                                return
                            }
                        }
                    }

                }

                override fun onError(e: ApiException) {
                    super.onError(e)
//                    if (view != null) {
//                        view?.refreshComplete()
//                    }
                }
            })
    }

    private fun findContractItem(fromContact: FutureContractBean?, parent: ContractAssetBean?): FutureContractBean? {
        if (parent != null && fromContact != null) {
            if (Pub.isListExists(parent.content)) {
                for (item in parent.content) {
                    if (fromContact == item) {
                        return item
                    }
                }
            }
        }
        return null
    }

    private fun findContractParent(fromKey: String, list: List<ContractAssetBean?>?): ContractAssetBean? {
        if (Pub.isListExists(list)) {
            for (assetBean in list!!) {
                if (fromKey == assetBean?.name) {
                    return assetBean
                }
            }
        }
        return null
    }

    /**
     * 指数的deal数据请求
     */
    open fun getAdditonalSpot(assetName: String) {
        val socketEntity = WebSocketEntity<SocketEntrustParam>()
        val param = SocketEntrustParam(assetName)
//        param.id =
        socketEntity.param = param
        socketEntity.reqType = SocketKey.MARKET_SPOTINDEX
//        client.addChannel(socketEntity, this)
    }

    fun setSelectContact(selectParent: ContractAssetBean?, selectContact: FutureContractBean?) {
        //jiang 切换基准币种，需要重置精度
        if (selectParent != this.selectParent) {
//            setBasePrecision("-1")
        }
        this.selectParent = selectParent
        this.selectContact = selectContact

//        client.removeChannel(SocketKey.MARKET_SPOTINDEX, this)

        getAdditonalSpot(selectContact!!.assetName)

//        view?.onSelectView()
//        view?.refreshCurrency()
        //jiang 0818
//        getContractDelivery();
        getContractAccount(selectContact.getContractId())
//        getDepthFive(type, Pub.GetInt(selectContact.getContractId()))
//        getNowTicker(type, Pub.GetInt(selectContact.getContractId()))
//        getTimeLineDatas(type, Pub.GetInt(selectContact.getContractId()), "1m")
        //jiang chart loading
//        if (view != null) {
//            view!!.setOverShowLoading(0, true)
//            view!!.setOverShowLoading(1, true)
//            getKlineDatas(2, Pub.GetInt(selectContact.getContractId()), ExchangePresenter.types[currentPeriodIndex])
//        }
    }


    /**
     * 权益、保证金、保证金率
     */
    private fun getContractAccount(contactId: String?) {
        val map = BtbMap()
        map.p("contractId", contactId)
//        client.removeChannel(SocketKey.FUTURE_TOP, this)
        addChannel()
        //        Http.getExchangeService().getContractAccount(map)
//                .compose(new CommonTransformer<FutureTopInfoBean>())
//                .subscribe(new CommonSubscriber<FutureTopInfoBean>() {
//                    @Override
//                    public void onNext(FutureTopInfoBean map) {
//                        getView().setContractAccount(map);
//                        addChannel();
//                    }
//                });
    }

    private fun addChannel() {
        val socketEntity = WebSocketEntity<BtbMap>()
        socketEntity.reqType = SocketKey.FUTURE_TOP
//        client.addChannel(socketEntity, this)
    }

    fun getKlineDatas(type: Int, id: Int, period: String) {
        val param = SocketMarketParam(id, type, periodTypeSwitch(period))
//        client.removeChannel(SocketKey.HangQingKlinePushReqType, this@BaseTradePresenter, param)
        //clear first
        FotaApplication.getInstance().resetAppKLineData(type, null, null)
        val currentTime = System.currentTimeMillis()
        val endTime = currentTime.toString() + ""
        val startTime: String = fetchSincTime(500, period, currentTime)
        Http.getMarketService().getKlineDatas(createPageParam(type, id, startTime, endTime, period))
            .compose(CommonTransformer())
            .subscribe(object : CommonSubscriber<MarketKLineBean>(FotaApplication.getInstance()) {
                override fun onNext(bean: MarketKLineBean) {
//                    if (getView() != null) {
                    //委托 http之后开始订阅推送
                    val socketEntity = WebSocketEntity<SocketMarketParam>()
                    socketEntity.setParam(SocketMarketParam(id, type, periodTypeSwitch(period)))
                    if (bean.type == 2) {
                        socketEntity.param.assetName = bean.assetName
                        socketEntity.param.contractType = bean.contractType
                    }
                    socketEntity.reqType = SocketKey.HangQingKlinePushReqType
//                        client.addChannel(socketEntity, this@BaseTradePresenter)
                    val datas: List<ChartLineEntity> = ArrayList()
                    val spots: List<ChartLineEntity> = ArrayList()
                    BeanChangeFactory.iterateKDataList(datas, spots, type, bean.line)
                    FotaApplication.getInstance().resetAppKLineData(type, datas, spots)
//                        getView().resetFreshKlineData()
//                        getView().setOverShowLoading(1, false)
//                    }
                }

                override fun onError(e: ApiException) {
                    super.onError(e)
//                    if (getView() != null) {
//                        getView().setOverShowLoading(1, false)
//                        getView().onNoDataCallBack(1)
//                    }
                }
            })
    }

    fun getTimeLineDatas(type: Int, id: Int, period: String?) {
//        type = type
//        this.entityId = id
        val param = SocketMarketParam(id, type, periodTypeSwitch(period!!))
//        client.removeChannel(
//            SocketKey.HangQingFenShiTuZheXianTuReqType,
//            this@BaseTradePresenter,
//            param
//        )
        //jiang 1227 fix
//        if (type == 2) {
//            client.removeChannel(SocketKey.DELIVERY_TIME_CHANGED, this@BaseTradePresenter)
//            client.removeChannel(SocketKey.POSITION_LINE, this@BaseTradePresenter)
//        }
        val currentTime = System.currentTimeMillis()
        val endTime = currentTime.toString() + ""
        val startTime = fetchSincTime(96, period, currentTime)
        Http.getMarketService().getTimeLineDatas(
            createPageParam(
                type, id, startTime, endTime, periodTypeSwitch(
                    period
                )!!
            )
        )
            .compose(CommonTransformer())
            .subscribe(object :
                CommonSubscriber<MarketTimeLineBean>(FotaApplication.getInstance()) {
                override fun onNext(bean: MarketTimeLineBean) {
//                    if (getView() != null) {
                    // http之后开始订阅推送
//                        socketSubsribe(bean, period)
                    val future = FutureItemEntity(bean.name)
                    future.entityType = bean.type
                    future.entityId = bean.id
                    val datas = future.datas
                    val spots: List<ChartLineEntity> = java.util.ArrayList()
                    BeanChangeFactory.iterateKDataList(datas, spots, type, bean.line)
                    FotaApplication.getInstance().resetAppTimeLineData(type, datas, spots)
                    FotaApplication.getInstance().updateAppHoldingInfo(bean, 0)

                    val chartList = FotaApplication.getInstance().getListFromTimesByType(2)
                    val timeData = mutableListOf<HisData>()
                    for (i in chartList.indices) {
                        val m = chartList[i] ?: continue
                        val data = BeanChangeFactory.createNewHisData(m)
                        timeData.add(data)
                    }
                    time15Data.value = timeData
//                        getView().resetFreshTimelineData()
//                        getView().setOverShowLoading(0, false)
//                    }
                }

                override fun onError(e: ApiException) {
                    super.onError(e)
//                    if (getView() != null) {
//                        getView().setOverShowLoading(0, false)
//                        getView().onNoDataCallBack(0)
//                    }
                }
            })
    }

    private var period: String? = null
    private fun fetchSincTime(max: Int, type: String, currentTime: Long): String {
        var result = "0"
        when (type) {
            "1m" -> {
                result = (currentTime - max * 60 * 1000).toString() + ""
                period = 1.toString() + ""
            }
            "15m" -> {
                result = (currentTime - max * 15 * 60 * 1000).toString() + ""
                period = 2.toString() + ""
            }
            "1h" -> {
                result = (currentTime - max * 60 * 60 * 1000).toString() + ""
                period = 3.toString() + ""
            }
            "1d" -> {
                val temp = max * 24 * 60 * 60 * 1000L
                result = (currentTime - temp).toString() + ""
                period = "4"
            }
            "5m" -> {
                result = (currentTime - max * 5 * 60 * 1000).toString() + ""
                period = "5"
            }
            "30m" -> {
                result = (currentTime - max * 1800000L).toString() + ""
                period = "6"
            }
            "4h" -> {
                result = (currentTime - max * 14400000L).toString() + ""
                period = "7"
            }
            "6h" -> {
                result = (currentTime - max * 21600000L).toString() + ""
                period = "8"
            }
            "1w" -> {
                result = (currentTime - max * 604800000L).toString() + ""
                period = "9"
            }
            else -> {
            }
        }
        return result
    }

    private fun createPageParam(
        type: Int,
        id: Int,
        startTime: String,
        endTime: String,
        resolution: String
    ): BtbMap {
        val paramsMap = BtbMap()
        paramsMap["resolution"] = resolution
        paramsMap["id"] = id.toString() + ""
        paramsMap["startTime"] = startTime
        paramsMap["endTime"] = endTime
        paramsMap["type"] = type.toString() + ""
        return paramsMap
    }

    private fun periodTypeSwitch(type: String): String? {
        var result = "0"
        when (type) {
            "1m" -> result = "1"
            "15m" -> result = "2"
            "1h" -> result = "3"
            "1d" -> result = "4"
            "5m" -> result = "5"
            "30m" -> result = "6"
            "4h" -> result = "7"
            "6h" -> result = "8"
            "1w" -> result = "9"
            else -> {
            }
        }
        return result
    }


    private fun isPrecisionNotEqual(precision: String?): Boolean {
//        return if (precision == null || "" == precision) {
//            "-1" != basePrecision
//        } else {
//            precision != basePrecision
//        }
        return false
    }

    val type = ConstantsReq.TRADE_TYPE_CONTACT
//    protected fun onNextDepth(bean: DepthBean) {
////        if (getView() == null) {
////            return
////        }
////        if (bean == null) {
////            getView().onRefreshDepth(null, null, null, 0f)
////            return
////        }
//        val depthMap = FotaApplication.getInstance().depthMap
//        val key: String = "$type-${selectContract.get()!!.contractId}"
//        depthMap[key] = bean
//        val limitSells = BeanChangeFactory.getSellEntrustBeans(bean.asks, 5)
//        val limitBuys = BeanChangeFactory.getEntrustBeans(bean.bids, 5)
//        if (limitSells != null) {
//            limitSells.sort()
//            limitSells.reverse()
//        }
//        limitBuys?.sort()
//        if (getView() != null) {
//            if ("-1" == basePrecision) {
//                getView().onRefreshDepth(
//                    limitBuys,
//                    limitSells,
//                    if (bean.pricisionList == null) null else bean.pricisionList,
//                    bean.valuation
//                )
//            } else {
//                getView().onRefreshDepth(limitBuys, limitSells, null, bean.valuation)
//            }
//        }
//    }

}