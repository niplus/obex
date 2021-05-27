package com.fota.android.moudles

class InviteListBean : ArrayList<InviteListBeanItem>()

data class InviteListBeanItem(
    val acceptAccount: String,
    val inviteDate: String,
    val lastDayDealAmount: String,
    val lastDayTradeVol: String,
    val userLevel: String
)