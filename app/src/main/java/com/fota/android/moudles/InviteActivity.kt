package com.fota.android.moudles

import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fota.android.R
import com.fota.android.core.mvvmbase.BaseActivity
import com.fota.android.databinding.ActivityInviteBinding
import com.fota.android.databinding.ItemInviteTabBinding
import com.google.android.material.tabs.TabLayoutMediator

class InviteActivity : BaseActivity<ActivityInviteBinding, InviteViewModel>() {

    private val tabText = mutableListOf<String>()
    private val fragments = mutableListOf(CommissionFragment(), MyInviteFragment())
    override fun getLayoutId(): Int {
        return R.layout.activity_invite
    }

    override fun initData() {

    }

    override fun initComp() {
        tabText.add(getString(R.string.my_commission))
        tabText.add(getString(R.string.my_invite))
        dataBinding.apply {
            this.viewModel = this@InviteActivity.viewModel


            viewpager.adapter = object : FragmentStateAdapter(this@InviteActivity){
                override fun getItemCount(): Int {
                    return 2
                }

                override fun createFragment(position: Int): Fragment {
                    return fragments[position]
                }
            }

            TabLayoutMediator(tbInvite, viewpager, TabLayoutMediator.TabConfigurationStrategy { tab, position ->

            }).attach()

            tabText.forEachIndexed { index, s ->
                val tabDataBinding = DataBindingUtil.inflate<ItemInviteTabBinding>(LayoutInflater.from(this@InviteActivity), R.layout.item_invite_tab, null, false)
                tabDataBinding.apply {
                    tvName.text = s
                }
                tbInvite.getTabAt(index)!!.customView = tabDataBinding.root
            }
        }
    }

    override fun createViewModel(): InviteViewModel? {
        return ViewModelProvider(this).get(InviteViewModel::class.java)
    }
}