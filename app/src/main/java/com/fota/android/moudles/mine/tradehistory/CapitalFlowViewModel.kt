package com.fota.android.moudles.mine.tradehistory

import androidx.lifecycle.MutableLiveData
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.moudles.futures.model.FuturesRepository
import com.fota.android.moudles.mine.bean.FundDataBean

class CapitalFlowViewModel: BaseViewModel() {
    private val repository = FuturesRepository()
    val fundDataLiveData = MutableLiveData<FundDataBean>()
    fun getFundData(){
        launchUI {
            val result = repository.getFundData()
            fundDataLiveData.value = result.data
        }
    }
}