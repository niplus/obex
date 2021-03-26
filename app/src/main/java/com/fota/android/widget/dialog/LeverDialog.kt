package com.fota.android.widget.dialog

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.fota.android.R
import com.fota.android.databinding.DialogLeverAdjustBinding

class LeverDialog(context: Context): BottomDialog(context) {
    var dataBinding: DialogLeverAdjustBinding? = null
    var onClickListener: View.OnClickListener? = null
    set(value) {
        field = value
        if (value != null)
            dataBinding?.btnConfirm?.setOnClickListener(value)
    }

    private var lever: Int = 0


    init {
        dataBinding = DataBindingUtil.inflate<DialogLeverAdjustBinding>(LayoutInflater.from(context), R.layout.dialog_lever_adjust, null, false)
        setContentView(dataBinding!!.root)

        dataBinding!!.apply {
            progress.leverChangeListener = {
                tvLever.text = "${it}X"
                lever = it

                if (lever >= 50){
                    dataBinding!!.tvHint.visibility = View.VISIBLE
                } else
                    dataBinding!!.tvHint.visibility = View.GONE
            }

            ivCancel.setOnClickListener {
                dismiss()
            }

            ivPlus.setOnClickListener {
                if (lever == 100)
                    return@setOnClickListener
                lever += 1
                if (lever >= 50){
                    dataBinding!!.tvHint.visibility = View.VISIBLE
                } else
                    dataBinding!!.tvHint.visibility = View.GONE
                progress.progress = lever
                tvLever.text = "${lever}X"
            }

            ivSub.setOnClickListener {
                if (lever == 1){
                    return@setOnClickListener
                }

                lever -= 1
                if (lever >= 50){
                    dataBinding!!.tvHint.visibility = View.VISIBLE
                } else
                    dataBinding!!.tvHint.visibility = View.GONE
                progress.progress = lever
                tvLever.text = "${lever}X"
            }
        }

    }

    fun setLever(lever: Int){
        this.lever = lever
        dataBinding!!.progress.progress = lever
        dataBinding!!.tvLever.text = "${lever}X"
        if (lever >= 50){
            dataBinding!!.tvHint.visibility = View.VISIBLE
        } else
            dataBinding!!.tvHint.visibility = View.INVISIBLE
    }

    fun getLever(): Int{
        return lever
    }
}