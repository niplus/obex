package com.fota.android.moudles

data class InviteRecordBean(
    val inviteCode: String,
    val inviteUrl: String,
    val todayCount: Int,
    val totalCount: Int,
    val yesterdayCount: Int
)