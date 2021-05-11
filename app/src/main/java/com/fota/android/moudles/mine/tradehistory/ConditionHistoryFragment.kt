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
import com.fota.android.BR
import com.fota.android.R
import com.fota.android.commonlib.base.AppConfigs
import com.fota.android.commonlib.utils.GradientDrawableUtils
import com.fota.android.core.mvvmbase.BaseFragment
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.databinding.FragmentConditionHistoryBinding
import com.fota.android.databinding.ItemConditionOrderBinding
import com.fota.android.moudles.futures.bean.Order
import com.fota.android.moudles.mine.ConditionHistoryViewModel
import com.fota.android.widget.dialog.MessageDialog
import com.ndl.lib_common.base.BaseAdapter
import com.ndl.lib_common.base.MyViewHolder


class ConditionHistoryFragment : BaseFragment<FragmentConditionHistoryBinding, ConditionHistoryViewModel>() {

    private val orders = mutableListOf<Order>()

    private var type = 0
    override fun getLayoutId(): Int {
        return R.layout.fragment_condition_history
    }

    override fun initData() {
        viewModel.conditionOrderLiveData.observe(this, Observer {
            if (type == 0){
                orders.clear()
                if (it.items.isNotEmpty()){
                    orders.addAll(it.items)
                }

                dataBinding.rvCondition.adapter?.notifyDataSetChanged()
            }
        })

        viewModel.conditionHistoryOrderLiveData.observe(this, Observer {
            if (type == 1){
                orders.clear()
                if (it.items.isNotEmpty()){
                    orders.addAll(it.items)
                }

                dataBinding.rvCondition.adapter?.notifyDataSetChanged()
            }
        })

        viewModel.getConditionOrder()
        viewModel.getConditionHisToryOrder()
    }

    override fun initComp() {
        dataBinding.apply {
            rvCondition.layoutManager = LinearLayoutManager(requireContext())
            rvCondition.adapter = object : BaseAdapter<ItemConditionOrderBinding, Order>(orders, R.layout.item_condition_order, BR.order){
                override fun onBindViewHolder(
                    holder: MyViewHolder<ItemConditionOrderBinding>,
                    position: Int
                ) {
                    super.onBindViewHolder(holder, position)
                    holder.dataBinding.apply {
                        val itemData = data[position]
                        averagePrice.text = itemData.triggerPrice
                        openPositionPrice.text = if (itemData.triggerType == 1) "限价" else "市价"
                        margin.text = itemData.algoPrice
                        applies.text = itemData.quantity

                        buyOrSell.text = when(itemData.orderType){
                            2 -> "止盈"
                            3 -> "止损"
                            else -> {
                                "计划委托"
                            }
                        }

                        orderLeft.setBackgroundColor(AppConfigs.getColor(itemData.orderDirection == 2))
                        assetName.setTextColor(AppConfigs.getColor(itemData.orderDirection == 2))
                        buyOrSell.setTextColor(AppConfigs.getColor(itemData.orderDirection == 2))

                        GradientDrawableUtils.setBoardColor(
                            buyOrSell,
                            AppConfigs.getColor(itemData.orderDirection == 2)
                        )
                    }
                }
            }
            rvCondition.addItemDecoration(object : RecyclerView.ItemDecoration() {
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

    override fun createViewModel(): ConditionHistoryViewModel {
        return ViewModelProvider(this).get(ConditionHistoryViewModel::class.java)
    }

    fun changeType(type: Int)
    {
        this.type = type
        var temp: List<Order>? = if (type == 0){
            viewModel.conditionOrderLiveData.value?.items
        }else{
            viewModel.conditionHistoryOrderLiveData.value?.items
        }
        orders.clear()
        if (!temp.isNullOrEmpty()){
            orders.addAll(temp)
        }

        dataBinding.rvCondition.adapter?.notifyDataSetChanged()

    }
}