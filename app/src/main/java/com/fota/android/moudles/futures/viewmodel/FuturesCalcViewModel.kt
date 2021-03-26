package com.fota.android.moudles.futures.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.databinding.ObservableField
import com.fota.android.commonlib.base.BaseView
import com.fota.android.core.mvvmbase.BaseViewModel
import java.math.BigDecimal
import java.math.RoundingMode

class FuturesCalcViewModel: BaseViewModel() {

    val openPrice = ObservableField<String>("")
    val closePrice = ObservableField<String>("")
    val volume = ObservableField<String>("")

    val deposit = ObservableField("USDT")
    val profit = ObservableField("USDT")
    val returnRate = ObservableField("%")

    val coinName = ObservableField("")

    var currentPrice = "0.00"

    var lever = 1
    var isBuy = true

    //获取保证金
    private fun getDeposit(volume: String?, price: String?, lever: Int): String {
        if (!hasValue(volume) || !hasValue(price)){
            return ""
        }

         return BigDecimal(volume).multiply(BigDecimal(price)).divide(BigDecimal(lever), 2, RoundingMode.HALF_UP).toPlainString()
    }

    //获取收益
    private fun getProfit(openPrice: String?, closePrice: String?, volume: String?): String{
        if (!hasValue(openPrice) || !hasValue(closePrice) || !hasValue(volume)){
            return ""
        }

        if (BigDecimal(closePrice).subtract(BigDecimal(openPrice)).compareTo(BigDecimal(0)) == 0){
            return "0"
        }


        val result = BigDecimal(closePrice).subtract(BigDecimal(openPrice)).multiply(BigDecimal(volume)).setScale(2, RoundingMode.HALF_UP)
        return if (!isBuy) result.negate().toPlainString() else result.toPlainString()
    }

    //获取收益率
    private fun getProfitRate(profit: String, deposit: String): String{
        if (!hasValue(profit) || !hasValue(deposit))
            return ""
        return BigDecimal(profit).divide(BigDecimal(deposit),2, RoundingMode.HALF_UP).multiply(BigDecimal(100)).toPlainString()
    }

    private fun hasValue(value: String?): Boolean{
        return !(value.isNullOrEmpty() || BigDecimal(value).compareTo(BigDecimal(0)) == 0)
    }

    fun calc(){
        val deposit = getDeposit(volume.get(), openPrice.get(), lever)
        this.deposit.set(deposit + "USDT")
        val profit = getProfit(openPrice.get(), closePrice.get(), volume.get())
        this.profit.set(profit + "USDT")
        returnRate.set(getProfitRate(profit, deposit) + "%")

    }
}