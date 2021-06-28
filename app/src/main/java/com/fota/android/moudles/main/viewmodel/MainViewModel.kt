package com.fota.android.moudles.main.viewmodel

import com.fota.android.app.FotaApplication
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.http.WebSocketClient1
import com.fota.android.moudles.exchange.index.ExchangeFragment
import com.fota.android.moudles.futures.FuturesFragment
import com.fota.android.moudles.home.HomeFragment
import com.fota.android.moudles.main.model.UserRepository
import com.fota.android.moudles.market.MarketFragment
import com.fota.android.moudles.mine.MineFragment
import com.fota.android.utils.UserLoginUtil

class MainViewModel: BaseViewModel() {

    private val userRepository = UserRepository()

    var fragments = mutableListOf(
        HomeFragment(),
        MarketFragment(),
        ExchangeFragment(),
        FuturesFragment(),
        MineFragment()
    )

    init {
//        val client = FotaApplication.getInstance().client as WebSocketClient
//        client.openWebSocket()

        WebSocketClient1.openWebsocket()
        checkToken()
    }

    private fun checkToken(){
        launchUI {
            userRepository.checkToke(UserLoginUtil.getTokenUnlogin())
        }
    }
}