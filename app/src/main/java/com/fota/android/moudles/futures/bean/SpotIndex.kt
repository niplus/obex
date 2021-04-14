package com.fota.android.moudles.futures.bean

data class SpotIndex(
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