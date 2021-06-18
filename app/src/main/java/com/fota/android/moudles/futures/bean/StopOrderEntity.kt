package com.fota.android.moudles.futures.bean

data class StopOrderEntity(
    val contractId: Int,
    val stopLessOrder: StopLessOrder?,
    val stopProfitOrder: StopLessOrder?
)

data class StopLessOrder(
    val algoPrice: String?,
    val algoValue: String?,
    val orderType: Int?,
    val triggerPrice: String?
)

data class StopProfitOrder(
    val algoPrice: String,
    val algoValue: String,
    val orderType: Int,
    val triggerPrice: String
)