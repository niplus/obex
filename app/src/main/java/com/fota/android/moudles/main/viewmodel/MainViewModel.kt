package com.fota.android.moudles.main.viewmodel

import com.fota.android.app.FotaApplication
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.moudles.main.model.UserRepository
import com.fota.android.socket.WebSocketClient
import com.fota.android.utils.UserLoginUtil

class MainViewModel: BaseViewModel() {

    private val userRepository = UserRepository()

    init {
        val client = FotaApplication.getInstance().client as WebSocketClient
        client.openWebSocket()

        checkToken()
    }

    private fun checkToken(){
        launchUI {
            userRepository.checkToke(UserLoginUtil.getTokenUnlogin())
        }
    }
}