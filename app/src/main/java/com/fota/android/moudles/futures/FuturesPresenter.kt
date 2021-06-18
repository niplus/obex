package com.fota.android.moudles.futures

import android.os.Bundle
import android.util.Log
import com.fota.android.R
import com.fota.android.app.*
import com.fota.android.common.bean.exchange.ExchangeBody
import com.fota.android.common.bean.exchange.ExchangeEntity
import com.fota.android.commonlib.http.BaseHttpEntity
import com.fota.android.commonlib.http.exception.ApiException
import com.fota.android.commonlib.http.rx.CommonSubscriber
import com.fota.android.commonlib.http.rx.CommonTransformer
import com.fota.android.commonlib.http.rx.NothingTransformer
import com.fota.android.commonlib.utils.Pub
import com.fota.android.core.base.BtbMap
import com.fota.android.http.ContractAssetBean
import com.fota.android.http.Http
import com.fota.android.moudles.exchange.index.ExchangeFragment
import com.fota.android.moudles.exchange.index.ExchangePresenter
import com.fota.android.moudles.exchange.index.ExchangeTradeView
import com.fota.android.moudles.futures.bean.ConditionSocketParam
import com.fota.android.moudles.futures.bean.SpotIndex
import com.fota.android.moudles.futures.money.FuturesMoneyBean
import com.fota.android.socket.SocketAdditionEntity
import com.fota.android.socket.WebSocketEntity
import com.fota.android.socket.params.SocketEntrustParam
import com.google.gson.Gson
import com.google.gson.JsonObject

open class FuturesPresenter(view: ExchangeTradeView?) : ExchangePresenter(view) {
    /**
     * 选择的币种的对应列表的选中项
     */
    var selectContact: FutureContractBean? = null
        private set
    var selectParent: ContractAssetBean? = null
        private set

    //private String key;
    var allList: List<ContractAssetBean?>? = null
    private var fromContact: FutureContractBean? = null
    private var fromKey: String? = null
    override fun removeChannel() {
//        super.removeChannel()
//        client.removeChannel(SocketKey.MineEntrustReqType, this)
//        client.removeChannel(SocketKey.DELIVERY_TIME_CHANGED, this)
//        client.removeChannel(SocketKey.POSITION_LINE, this)
//        client.removeChannel(SocketKey.TradeWeiTuoReqType, this)
//        client.removeChannel(SocketKey.FUTURE_TOP, this)
    }

    override fun removeChildren() {
//        client.removeChannel(SocketKey.MinePositionReqType, this)
//        client.removeChannel(SocketKey.MineEntrustReqType_CONTRACT, this)
//        client.removeChannel(SocketKey.TradeDealReqType, this)
    }

    override fun getExtras(bundle: Bundle) {
        if (bundle == null) {
            return
        }
        if (bundle.containsKey(BundleKeys.MODEL)) {
            fromContact = bundle.getSerializable(BundleKeys.MODEL) as FutureContractBean?
        }
        if (bundle.containsKey(BundleKeys.KEY)) {
            fromKey = bundle.getString(BundleKeys.KEY)
        }
    }

    override fun submit(fundCode: String) {
        if (selectContact == null) {
            return
        }
        model.contractId = selectContact!!.getContractId()
        model.contractName = selectContact!!.getContractName()
        model.lever = selectContact!!.getLever()
        val body = ExchangeBody()
        //        body.setTradeToken(fundCode);
        body.obj = model
        Http.getExchangeService().makeContractOrder(body)
                .compose(NothingTransformer())
                .subscribe(object :
                    CommonSubscriber<BaseHttpEntity?>(FotaApplication.getInstance()) {
                    override fun onNext(t: BaseHttpEntity?) {
                        if (view != null) {
                            showTopInfo(model)
                            view?.notifyFromPresenter(ExchangeFragment.ORDER_SUCCESS)
                        }
                    }
                })
    }

    fun preciseMargin() {
        if (selectContact == null) {
            return
        }
        model.contractId = selectContact!!.getContractId()
        model.contractName = selectContact!!.getContractName()
        val body = ExchangeBody()
        body.obj = model
        Http.getExchangeService().preciseMargin(body)
                .compose(CommonTransformer())
                .subscribe(object : CommonSubscriber<BtbMap?>() {
                    override fun onNext(map: BtbMap?) {
                        view?.setPreciseMargin(map)
                    }

                    override fun onError(e: ApiException) {
                        //super.onError(e);
                    }
                })
    }

    /**
     * 下单成功
     */
    protected fun showTopInfo(model: ExchangeEntity?) {
        view?.showTopInfo(view?.getXmlString(R.string.order_success))
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
                        if (view != null) {
                            view!!.stopProgressDialog()
//                            view!!.cancelSuccess()
                            model.isCanceled = true
                            showTopInfo(modelPost)
                        }
                    }
                })
    }

    override fun reqUsdtList() {}
    //从传值过来的地方处理
    //选中上次选中的
    //都没有选中第一个
    val contactList: Unit
        get() {
            Http.getExchangeService().contractTree
                    .compose(CommonTransformer())
                    .subscribe(object : CommonSubscriber<List<ContractAssetBean?>?>() {
                        override fun onNext(list: List<ContractAssetBean?>?) {
                            if (view != null) {
                                view?.refreshComplete()
                            }
                            allList = list

                            allList?.forEach {
                                it!!.content.forEach { contract ->
                                    if (contract.contractType == 3) {
                                        if (contract.getContractName().contains("永续")) {
                                            contract.setContractName(
                                                contract.getContractName().replace(
                                                    "永续", " ${
                                                        view?.context?.getString(
                                                            R.string.perp
                                                        )
                                                    }"
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            //从传值过来的地方处理
                            if (fromKey != null && fromContact != null) {
                                val parent = findContractParent(fromKey!!, list)
                                if (parent != null) {
                                    val item = findContractItem(fromContact, parent)
                                    if (item != null) {
                                        fromKey = null
                                        fromContact = null
                                        setSelectContact(parent, item)
                                        return
                                    }
                                }
                            }
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
                            //都没有选中第一个
                            if (Pub.isListExists(list)) {
                                setSelectContact(list!![0], list[0]!!.content[0])
                            }
                        }

                        override fun onError(e: ApiException) {
                            super.onError(e)
                            if (view != null) {
                                view?.refreshComplete()
                            }
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
        client.removeChannel(SocketKey.MARKET_SPOTINDEX, this)
        val socketEntity = WebSocketEntity<SocketEntrustParam>()
        val param = SocketEntrustParam(assetName)
//        param.id =
        socketEntity.param = param
        socketEntity.reqType = SocketKey.MARKET_SPOTINDEX
        client.addChannel(socketEntity, this)
    }

    fun setSelectContact(selectParent: ContractAssetBean?, selectContact: FutureContractBean?) {
        //jiang 切换基准币种，需要重置精度
        if (selectParent != this.selectParent) {
            setBasePrecision("-1")
        }
        this.selectParent = selectParent
        this.selectContact = selectContact

        client.removeChannel(SocketKey.MARKET_SPOTINDEX, this)

        getAdditonalSpot(selectContact!!.assetName)

        view?.onSelectView()
        view?.refreshCurrency()

        getContractAccount(selectContact.getContractId())
        getDepthFive(type, Pub.GetInt(selectContact.getContractId()))
        getNowTicker(type, Pub.GetInt(selectContact.getContractId()))
        getTimeLineDatas(type, Pub.GetInt(selectContact.getContractId()), "1m")
        addConditionChannel()
        //jiang chart loading
        if (view != null) {
            view!!.setOverShowLoading(0, true)
            view!!.setOverShowLoading(1, true)
            getKlineDatas(2, Pub.GetInt(selectContact.getContractId()), types[currentPeriodIndex])
        }
    }

     fun addConditionChannel(){
         if (selectContact == null) return
        //委托 http获取到实时委托之后开始订阅实时委托推送
        val socketEntity = WebSocketEntity<ConditionSocketParam>()
        val param = ConditionSocketParam(null, 1, 10, selectContact!!.contractId.toInt(), null)
        socketEntity.param = param
        socketEntity.reqType = SocketKey.CONDITION_ORDER
        socketEntity.setHandleType(1)
        socketEntity.type = 2
        client.addChannel(socketEntity, this)
    }

    override fun getType(): Int {
        return ConstantsReq.TRADE_TYPE_CONTACT
    }

    fun removeSocket(){
        client.removeChannel(SocketKey.MineEntrustReqType, this)
        client.removeChannel(SocketKey.DELIVERY_TIME_CHANGED, this)
        client.removeChannel(SocketKey.POSITION_LINE, this)
        client.removeChannel(SocketKey.TradeWeiTuoReqType, this)
        client.removeChannel(SocketKey.FUTURE_TOP, this)
        client.removeChannel(SocketKey.MinePositionReqType, this)
        client.removeChannel(SocketKey.MineEntrustReqType_CONTRACT, this)
        client.removeChannel(SocketKey.TradeDealReqType, this)
    }

    fun resumeAddChannel(){
        if (selectContact == null) return
        getContractAccount(selectContact!!.getContractId())
        getDepthFive(type, Pub.GetInt(selectContact!!.getContractId()))
        getNowTicker(type, Pub.GetInt(selectContact!!.getContractId()))
        getTimeLineDatas(type, Pub.GetInt(selectContact!!.getContractId()), "1m")
        addConditionChannel()
    }

    /**
     * 权益、保证金、保证金率
     */
    fun getContractAccount(contactId: String?) {
        val map = BtbMap()
        map.p("contractId", contactId)
        client.removeChannel(SocketKey.FUTURE_TOP, this)
        addChannel()
    }

    private fun addChannel() {
        val socketEntity = WebSocketEntity<BtbMap>()
        socketEntity.reqType = SocketKey.FUTURE_TOP
        val params = BtbMap()
        params["contractId"] = selectContact!!.getContractId()
        socketEntity.param = params
        client.addChannel(socketEntity, this)
    }

    override fun getView(): FutureTradeView? {
        if (super.getView() == null) return null
        return super.getView() as FutureTradeView
    }

    /**
     * 0-BTC指数，1-ETH指数，2-EOS指数，3-BCH指数，4-ETC指数，5-LTC指数
     * 这种转换最好在后端
     *
     * @param key
     * @return
     */
    private fun getSymbolByKey(key: String): Int {
        if ("BTC" == key) {
            return 0
        }
        if ("ETH" == key) {
            return 1
        }
        if ("EOS" == key) {
            return 2
        }
        if ("BCH" == key) {
            return 3
        }
        if ("ETC" == key) {
            return 4
        }
        return if ("LTC" == key) {
            5
        } else 0
    }

    override fun onUpdateImplSocket(
        reqType: Int,
        jsonString: String,
        additionEntity: SocketAdditionEntity<*>?
    ) {
        if (view == null) {
            return
        }
        if (isSocketChaos(reqType, jsonString, additionEntity)) {
            return
        }
        when (reqType) {
            SocketKey.TradeWeiTuoReqType -> {
                if (selectContact == null) {
                    return
                }
                val depthMap = FotaApplication.getInstance().depthMap
                val key = "2-" + selectContact!!.getContractId()
                val bean = depthMap[key] ?: return
                onNextDepth(bean)
            }
            SocketKey.TradeSuccessNotiification -> {
            }
            SocketKey.DELIVERY_TIME_CHANGED, SocketKey.POSITION_LINE -> view?.onRefreshDeliveryOrHold()
            SocketKey.FUTURE_TOP -> {
                if (jsonString == null || view == null) {
                    return
                }
                val map = GsonSinglon.getInstance().fromJson(
                    jsonString,
                    FutureTopInfoBean::class.java
                )
                view?.setContractAccount(map)
            }
            SocketKey.MARKET_SPOTINDEX->{
                val spotIndex = Gson().fromJson<SpotIndex>(jsonString, SpotIndex::class.java)
                view?.onSpotUpdate(spotIndex)
            }
            else -> {
                super.onUpdateImplSocket(reqType, jsonString, additionEntity)
                return
            }
        }
    }

    fun setFromHq(assetName: String?, model: FutureContractBean?) {
        fromKey = assetName
        fromContact = model
    }

    override fun detachView() {
        super.detachView()
        removeChannel()
    }

    override fun onHide() {
        removeChannel()
    }

    /**
     * 设置杠杆
     *
     * @param assetId
     * @param assetName
     * @param lever
     */
    fun setLever(assetId: Int, assetName: String?, lever: Int) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("assetId", assetId)
        jsonObject.addProperty("assetName", assetName)
        jsonObject.addProperty("lever", lever)
        val jsonObject2 = JsonObject()
        jsonObject2.addProperty("data", "[$jsonObject]")
        Http.getHttpService().setContractLever(jsonObject2)
                .compose(NothingTransformer<BaseHttpEntity>())
                .subscribe(object :
                    CommonSubscriber<BaseHttpEntity?>(FotaApplication.getInstance()) {
                    override fun onNext(`object`: BaseHttpEntity?) {
                        if (view == null) {
                            return
                        }
                        onRefresh()
                        view?.onLeverChange()
                    }

                    override fun onError(e: ApiException) {
                        super.onError(e)
                    }
                })
    }
}