package com.fota.android.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.TextView
import com.fota.android.R

class MessageDialog(context: Context, content: String, confirm: ()->Unit): Dialog(context) {

    init {
        setContentView(R.layout.dialog_alert)
        window!!.setBackgroundDrawable(ColorDrawable(0x00000000))
        window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val tvCancel = findViewById<TextView>(R.id.tv_cancel)
        val tvContent = findViewById<TextView>(R.id.tv_content)
        val tvConfirm = findViewById<TextView>(R.id.tv_confirm)

        tvContent.text = content
        tvCancel.setOnClickListener {
            dismiss()
        }

        tvConfirm.setOnClickListener {
            confirm.invoke()
        }
    }
}