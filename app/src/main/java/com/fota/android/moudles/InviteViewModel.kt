package com.fota.android.moudles

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fota.android.core.mvvmbase.BaseViewModel
import com.ndl.lib_common.base.Response

class InviteViewModel: BaseViewModel() {

    private val repository = InviteRepository()
    val inviteInfo = ObservableField<InviteBean>()
    val inviteRecordLiveData = MutableLiveData<InviteRecordBean>()
    val inviteListLiveData = MutableLiveData<InviteListBean?>()

    fun getInviteInfo(){
        launchUI {
            val result = repository.getInviteInfo()
            if (result.code == 0){
                inviteInfo.set(result.data)
            }
        }
    }

    fun getInviteRecord(){
        launchUI {
            val result = repository.getInviteRecord()
            if (result.code == 0)
                inviteRecordLiveData.value = result.data
        }
    }

    fun getInviteList(){
        launchUI {
            val result = repository.getInviteList()
            if (result.code == 0)inviteListLiveData.value = result.data
            else inviteListLiveData.value = null
        }
    }

}