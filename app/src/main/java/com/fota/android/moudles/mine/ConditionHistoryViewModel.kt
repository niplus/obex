package com.fota.android.moudles.mine

import androidx.lifecycle.MutableLiveData
import com.fota.android.commonlib.base.BaseView
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.moudles.futures.bean.ConditionOrdersBean
import com.fota.android.moudles.futures.model.FuturesRepository

class ConditionHistoryViewModel: BaseViewModel() {
    private val respository = FuturesRepository()
    val conditionOrderLiveData = MutableLiveData<ConditionOrdersBean>()
    val conditionHistoryOrderLiveData = MutableLiveData<ConditionOrdersBean>()

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
            val result = respository.getConditionHistoryOrder(0, 100)

            if (result.code == 0){
                conditionHistoryOrderLiveData.value = result.data
            }
        }
    }
}