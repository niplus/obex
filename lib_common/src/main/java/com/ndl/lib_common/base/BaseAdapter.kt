package com.ndl.lib_common.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BaseAdapter<T: ViewDataBinding, V: Any>(var data: MutableList<V>, val layout: Int, private val varId: Int): RecyclerView.Adapter<MyViewHolder<T>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<T> {
        val dataBinding = DataBindingUtil.inflate<T>(LayoutInflater.from(parent.context), layout, parent, false)
        return MyViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder<T>, position: Int) {
        if (varId != 0) {
            holder.dataBinding.setVariable(varId, data[position])
            holder.dataBinding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun refreshData(refreshData: MutableList<V>){
        data = refreshData
        notifyDataSetChanged()
    }


}

class MyViewHolder<T: ViewDataBinding>(val dataBinding: T): RecyclerView.ViewHolder(dataBinding.root)