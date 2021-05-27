package com.fota.android.moudles.mine.bean

data class FundDataBean(
    val item: List<Item>,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int
)

data class Item(
    val amount: String,
    val contractId: Int,
    val contractName: String,
    val contractType: Int,
    val gmtCreate: Long,
    val symbol: String,
    val type: Int,
    val userId: Int
)