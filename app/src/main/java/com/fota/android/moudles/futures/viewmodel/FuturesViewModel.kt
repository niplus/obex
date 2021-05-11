package com.fota.android.moudles.futures.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.moudles.futures.model.FuturesRepository
import com.ndl.lib_common.base.Response

class FuturesViewModel: BaseViewModel() {

    companion object{
        /**
         * 条件单 现价单
         */
        const val CONDITION = 0
        const val LIMIT = 1
        const val MARKET = 2
    }

    var type = CONDITION

    private val respository = FuturesRepository()
     val stopOrderLiveData = MutableLiveData<Response<String>>()

    /**
     * 条件单下单
     */
    fun conditionOrder(contractId: Int, orderType: Int, triggerPrice: String, algoPrice: String?, quantity: String, orderDirection: Int){
        launchUI {
            respository.conditionOrder(contractId, orderType, triggerPrice, algoPrice, quantity, orderDirection)
        }
    }

    fun stopOrder(contractId: Int,
                  buyAlgoPrice: String?, buyAlgoValue: String?, buyOrderType: Int?, buyTriggerPrice: String?,
                  sellAlgoPrice: String?, sellAlgoValue: String?, sellOrderType: Int?, sellTriggerPrice: String?){
        launchUI {
            val result = respository.stopOrder(contractId, buyAlgoPrice, buyAlgoValue, buyOrderType, buyTriggerPrice,
            sellAlgoPrice, sellAlgoValue, sellOrderType, sellTriggerPrice)
            stopOrderLiveData.value = result
            Log.i("nidongliang", "result: $result")
        }

    }
}