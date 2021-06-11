package com.fota.android.moudles

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fota.android.core.mvvmbase.BaseViewModel

class InviteViewModel: BaseViewModel() {

    private val repository = InviteRepository()
    val inviteInfoLiveData = ObservableField<InviteBean>()
    val inviteRecordLiveData = MutableLiveData<InviteRecordBean>()
    val inviteListLiveData = MutableLiveData<InviteListBean?>()

    fun getInviteInfo(){
        launchUI {
            val result = repository.getInviteInfo()
            if (result.code == 0){
                inviteInfoLiveData.set(result.data)
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