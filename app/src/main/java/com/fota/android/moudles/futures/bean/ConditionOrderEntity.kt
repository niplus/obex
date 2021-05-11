package com.fota.android.moudles.futures.bean

data class ConditionOrderEntity(
    val contractId: Int,
    val orderType: Int,
    val triggerPrice: String,
    val algoPrice: String?,
    val quantity: String,
    val orderDirection: Int
)
