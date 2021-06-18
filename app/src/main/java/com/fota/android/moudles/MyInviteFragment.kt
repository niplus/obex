package com.fota.android.moudles

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fota.android.BR
import com.fota.android.R
import com.fota.android.core.mvvmbase.BaseFragment
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.databinding.FragmentMyInviteBinding
import com.fota.android.databinding.ItemInviteBinding
import com.fota.android.widget.recyclerview.SmartRefreshLayoutUtils
import com.ndl.lib_common.base.BaseAdapter
import com.ndl.lib_common.base.MyViewHolder
import java.text.SimpleDateFormat


class MyInviteFragment : BaseFragment<FragmentMyInviteBinding, InviteViewModel>() {
    private val inviteList = mutableListOf<InviteListBeanItem>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_my_invite
    }

    override fun initData() {
        viewModel.apply {
            inviteListLiveData.observe(this@MyInviteFragment, Observer {
                inviteList.clear()
                if (!it.isNullOrEmpty()){
                    inviteList.addAll(it)
                }
                dataBinding.rvInviteRecord.adapter?.notifyDataSetChanged()
                dataBinding.refreshLayout.finishRefresh()
            })

            inviteRecordLiveData.observe(this@MyInviteFragment, Observer {
                val foregroundColorSpan = ForegroundColorSpan(0xFF395FC3.toInt())
                val relativeSizeSpan = RelativeSizeSpan(0.5f)

                val unit = getString(R.string.person)

                val todaySpannableString = SpannableString(it.todayCount.toString() + "/"+unit)
                todaySpannableString.setSpan(foregroundColorSpan, 0, it.todayCount.toString().length,  Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                todaySpannableString.setSpan(relativeSizeSpan, it.todayCount.toString().length, it.todayCount.toString().length + unit.length + 1,  Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                dataBinding.tvToday.text = todaySpannableString

                val yestodaySpannableString = SpannableString(it.yesterdayCount.toString() + "/"+unit)
                yestodaySpannableString.setSpan(foregroundColorSpan, 0, it.yesterdayCount.toString().length,  Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                yestodaySpannableString.setSpan(relativeSizeSpan, it.yesterdayCount.toString().length, it.yesterdayCount.toString().length + unit.length + 1,  Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                dataBinding.tvYestoday.text = yestodaySpannableString

                val totalSpannableString = SpannableString(it.totalCount.toString() + "/"+unit)
                totalSpannableString.setSpan(foregroundColorSpan, 0, it.totalCount.toString().length,  Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                totalSpannableString.setSpan(relativeSizeSpan,  it.totalCount.toString().length, it.totalCount.toString().length + unit.length + 1,  Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                dataBinding.tvTotal.text = totalSpannableString


            })
            getInviteList()
            getInviteRecord()
        }
    }

    private val simpleDateFormat = SimpleDateFormat("MM.dd/hh:mm")
    override fun initComp() {
        dataBinding.apply {
            rvInviteRecord.layoutManager = LinearLayoutManager(requireContext())
            rvInviteRecord.adapter = object : BaseAdapter<ItemInviteBinding, InviteListBeanItem>(inviteList, R.layout.item_invite, BR.invite){
                override fun onBindViewHolder(holder: MyViewHolder<ItemInviteBinding>, position: Int) {
                    super.onBindViewHolder(holder, position)
                    holder.dataBinding.apply {
                        if (position % 2 == 0)
                            container.setBackgroundColor(0x00ECF2FF)
                        else
                            container.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.color_ecf2ff_0c1115))


                        tvTime.text = simpleDateFormat.format(data[position].inviteDate.toLong())
                    }
                }
            }

            initHeader(refreshLayout, context)
            refreshLayout.setOnRefreshListener {
                this@MyInviteFragment.viewModel.getInviteList()
                this@MyInviteFragment.viewModel.getInviteRecord()
            }
        }
    }

    override fun createViewModel(): InviteViewModel {
        return ViewModelProvider(requireActivity()).get(InviteViewModel::class.java)
    }

}