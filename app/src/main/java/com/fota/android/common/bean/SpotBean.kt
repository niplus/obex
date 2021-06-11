package com.fota.android.common.bean

data class SpotBean(
    val code: Int,
    val `data`: Data,
    val handleType: Int,
    val `param`: String,
    val reqType: Int
)

data class Data(
    val averagePrice: String,
    val coinList: List<Coin>,
    val usdtSpotIndex: String
)

data class Coin(
    val cnName: String,
    val enName: String,
    val iconUrl: String,
    val price: String
)