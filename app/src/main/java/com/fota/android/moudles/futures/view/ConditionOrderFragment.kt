package com.fota.android.moudles.futures.view

import android.graphics.Rect
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fota.android.BR
import com.fota.android.R
import com.fota.android.commonlib.base.AppConfigs
import com.fota.android.commonlib.utils.GradientDrawableUtils
import com.fota.android.core.mvvmbase.BaseFragment
import com.fota.android.databinding.FragmentConditionOrderBinding
import com.fota.android.databinding.ItemConditionOrderBinding
import com.fota.android.moudles.futures.bean.ConditionOrdersBean
import com.fota.android.moudles.futures.bean.Order
import com.fota.android.moudles.futures.viewmodel.ConditionOrderViewModel
import com.fota.android.widget.dialog.MessageDialog
import com.ndl.lib_common.base.BaseAdapter
import com.ndl.lib_common.base.MyViewHolder
import com.ndl.lib_common.utils.LiveDataBus
import com.ndl.lib_common.utils.showSnackMsg
import java.text.SimpleDateFormat

class ConditionOrderFragment : BaseFragment<FragmentConditionOrderBinding, ConditionOrderViewModel>() {


    private val orders = mutableListOf<Order>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_condition_order
    }

    override fun initData() {
        viewModel.conditionOrderLiveData.observe(this, Observer {
            orders.clear()
            orders.addAll(it.items)
            dataBinding.rvConditionOrder.adapter?.notifyDataSetChanged()
        })

        LiveDataBus.getBus<ConditionOrdersBean>("conditionOrder").observe(this, Observer {
            orders.clear()
            if (!it.item.isNullOrEmpty()) {
                orders.addAll(it.item)
            }
            dataBinding.rvConditionOrder.adapter?.notifyDataSetChanged()
        })

        viewModel.cancelOrderLiveData.observe(this, Observer {
            if (it.code == 0) messageDialog?.dismiss()
            hideLoadDialog()
        })

    }


    private var messageDialog: MessageDialog? = null
    private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
    override fun initComp() {
        dataBinding.apply {
            rvConditionOrder.layoutManager = LinearLayoutManager(requireContext())
            rvConditionOrder.adapter = object : BaseAdapter<ItemConditionOrderBinding, Order>(
                orders,
                R.layout.item_condition_order,
                BR.order
            ){
                override fun onBindViewHolder(
                    holder: MyViewHolder<ItemConditionOrderBinding>,
                    position: Int
                ) {
                    super.onBindViewHolder(holder, position)
                    holder.dataBinding.apply {
                        val itemData = data[position]
                        averagePrice.text = itemData.triggerPrice
                        openPositionPrice.text = if (itemData.triggerType == 1) getString(R.string.contractweituo_type_limit) else getString(R.string.contractweituo_type_market)
                        margin.text = itemData.algoPrice
                        applies.text = itemData.quantity

                        assetName.text = itemData.contractName.replace("永续"," ${getString(R.string.perp)}")

                        buyOrSell.text = when(itemData.orderType){
                            2 -> getString(R.string.take_profit)
                            3 -> getString(R.string.stop_loss)
                            else -> {
                                getString(if(itemData.orderDirection == 1) R.string.tradehis_kong_short else R.string.tradehis_duo_short)
                            }
                        }

                        orderLeft.setBackgroundColor(AppConfigs.getColor(itemData.orderDirection == 2))
                        assetName.setTextColor(AppConfigs.getColor(itemData.orderDirection == 2))
                        buyOrSell.setTextColor(AppConfigs.getColor(itemData.orderDirection == 2))

                        GradientDrawableUtils.setBoardColor(
                            buyOrSell,
                            AppConfigs.getColor(itemData.orderDirection == 2)
                        )

                        tvTime.text = "${simpleDateFormat.format(itemData.gmtCreate)}"

                        root.setOnLongClickListener {
                            messageDialog = MessageDialog(requireContext(), getString(R.string.sure_cancel_order)){
                                showLoadDialog()
                                viewModel.cancelConditionOrder(data[position].id)
                            }
                            messageDialog!!.show()
                            return@setOnLongClickListener true
                        }
                    }
                }
            }

            rvConditionOrder.addItemDecoration(object : RecyclerView.ItemDecoration() {
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

    override fun createViewModel(): ConditionOrderViewModel {
        return ViewModelProvider(this).get(ConditionOrderViewModel::class.java)
    }
}