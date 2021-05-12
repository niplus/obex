package com.fota.android.moudles.futures.bean

/**
 * 条件单
 * @property items List<Order>
 * @property item List<Order>  用于存放socket推送的数据
 * @property pageNo Int
 * @property pageSize Int
 * @property total Int
 * @constructor
 */
data class ConditionOrdersBean(
    val items: List<Order>,
    val item: List<Order>,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int
)

data class Order(
    val algoPrice: String,
    val contractId: Int,
    val contractName: String,
    val contractType: Int,
    val gmtCreate: Long,
    val gmtModified: Long,
    val id: String,
    val orderStatus: Int,
    val orderType: Int,
    val quantity: String,
    val triggerPrice: String,
    val triggerType: Int,
    val userId: Int,
    val orderDirection: Int
)