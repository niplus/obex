package com.fota.android.moudles.mine.tradehistory

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fota.android.R
import com.fota.android.core.mvvmbase.BaseFragment
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.databinding.FragmentCapitalFlowBinding
import com.fota.android.databinding.ItemFundDataBinding
import com.fota.android.moudles.mine.bean.Item
import com.ndl.lib_common.base.BaseAdapter
import com.ndl.lib_common.base.MyViewHolder
import java.text.SimpleDateFormat

class CapitalFlowFragment : BaseFragment<FragmentCapitalFlowBinding, CapitalFlowViewModel>() {

    private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd hh:mm:ss")
    private val fundData = mutableListOf<Item>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_capital_flow
    }

    override fun initData() {
        viewModel.fundDataLiveData.observe(this, Observer {
            fundData.addAll(it.item)
            dataBinding.rvCapitalFlow.adapter?.notifyDataSetChanged()
        })
        viewModel.getFundData()
    }

    override fun initComp() {
        dataBinding.apply {
            rvCapitalFlow.layoutManager = LinearLayoutManager(requireContext())
            rvCapitalFlow.adapter = object : BaseAdapter<ItemFundDataBinding, Item>(fundData, R.layout.item_fund_data, 0){
                override fun onBindViewHolder(
                    holder: MyViewHolder<ItemFundDataBinding>,
                    position: Int
                ) {
                    super.onBindViewHolder(holder, position)
                    holder.dataBinding.apply {
                        val fundItemData  = data[position]
                        assetName.text = fundItemData.contractName.replace("永续", " ${getString(R.string.perp)}")

                        averagePrice.text = when(fundItemData.type){
                            1 -> getString(R.string.fund_fee)
                            2 -> getString(R.string.transaction_fee)
                            else -> getString(R.string.sub_commission)
                        }
                        openPositionPrice.text = fundItemData.symbol
                        margin.text = fundItemData.amount
                        tvTime.text = simpleDateFormat.format(fundItemData.gmtCreate)
                    }
                }
            }

            rvCapitalFlow.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.top = 30
                }
            })
        }


    }

    override fun createViewModel(): CapitalFlowViewModel {
        return ViewModelProvider(this).get(CapitalFlowViewModel::class.java)
    }
}