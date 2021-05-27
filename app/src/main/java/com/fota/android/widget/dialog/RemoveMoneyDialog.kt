package com.fota.android.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.fota.android.R
import com.fota.android.databinding.DialogRemoveMoneyBinding

class RemoveMoneyDialog(context: Context) : Dialog(context){
    val dataBinding = DataBindingUtil.inflate<DialogRemoveMoneyBinding>(LayoutInflater.from(context), R.layout.dialog_remove_money, null, false)
    init {

        setContentView(dataBinding.root)
        window!!.setBackgroundDrawable(ColorDrawable(0x00000000))
        window!!.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )

        setCanceledOnTouchOutside(false)
        setCancelable(false)

        dataBinding.apply {
            progressBar.isLever = false

            progressBar.leverChangeListener = {
                edtBuyQuantity.setText("$it%")
            }
        }
    }
}