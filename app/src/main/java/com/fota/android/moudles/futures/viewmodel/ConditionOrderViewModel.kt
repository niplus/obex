package com.fota.android.moudles.futures.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.moudles.futures.bean.ConditionOrdersBean
import com.fota.android.moudles.futures.model.FuturesRepository
import com.ndl.lib_common.base.Response

class ConditionOrderViewModel: BaseViewModel() {

    private val respository = FuturesRepository()

    val conditionOrderLiveData = MutableLiveData<ConditionOrdersBean>()
    val cancelOrderLiveData = MutableLiveData<Response<String>>()

    fun getConditionOrder(){
        launchUI {
            val result = respository.getConditionOrder(0, 100)

            if (result.code == 0){
                conditionOrderLiveData.value = result.data
            }
        }
    }

    fun getConditionHisToryOrder(){
        launchUI {
            val result = respository.getConditionOrder(0, 100)

            if (result.code == 0){
                conditionOrderLiveData.value = result.data
            }
        }
    }

    fun cancelConditionOrder(orderId: String){
        launchUI {
            val result = respository.cancelConditionOrder(orderId)
            cancelOrderLiveData.value = result
        }
    }
}