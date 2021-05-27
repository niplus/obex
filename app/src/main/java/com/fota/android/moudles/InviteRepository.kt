package com.fota.android.moudles

import com.fota.android.http.ApiService
import com.fota.android.http.Http
import com.ndl.lib_common.base.BaseRepository
import com.ndl.lib_common.base.Response


class InviteRepository: BaseRepository() {

    suspend fun getInviteInfo(): Response<InviteBean>{
        return apiCall {
            Http.getRetrofit().create(ApiService::class.java).getInviteInfo()
        }
    }

    suspend fun getInviteRecord(): Response<InviteRecordBean>{
        return apiCall {
            Http.getRetrofit().create(ApiService::class.java).getInviteRecord()
        }
    }

    suspend fun getInviteList(): Response<InviteListBean>{
        return apiCall {
            Http.getRetrofit().create(ApiService::class.java).getInviteRecordList()
        }
    }
}