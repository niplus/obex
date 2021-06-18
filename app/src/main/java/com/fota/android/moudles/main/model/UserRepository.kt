package com.fota.android.moudles.main.model

import com.fota.android.http.ApiService
import com.fota.android.http.Http
import com.ndl.lib_common.base.BaseRepository


/**
 * 登录登出和用户信息等
 */
class UserRepository: BaseRepository() {

    suspend fun checkToke(token: String){
        apiCall {
            Http.getRetrofit().create(ApiService::class.java).loginTokenCheck(token)
        }
    }
}