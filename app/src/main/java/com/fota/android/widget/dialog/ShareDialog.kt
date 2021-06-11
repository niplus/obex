package com.fota.android.widget.dialog

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.fota.android.R
import com.fota.android.commonlib.http.exception.ApiException
import com.fota.android.commonlib.http.rx.CommonSubscriber
import com.fota.android.commonlib.http.rx.CommonTransformer
import com.fota.android.commonlib.utils.ToastUitl
import com.fota.android.commonlib.utils.UIUtil
import com.fota.android.core.base.BtbMap
import com.fota.android.databinding.DialogShareBinding
import com.fota.android.http.Http
import com.fota.android.utils.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ShareDialog(
    context: Context,
    isBuy: Boolean,
    monetaryLever: Int,
    coinName: String,
    profitLoss: String,
    openPrice: String,
    markPrice: String,
    shareUrl: String,
    val activity: Activity
) : Dialog(context) {

    var dataBinding: DialogShareBinding? = null

    init {
        dataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_share,
            null,
            false
        )
        setContentView(dataBinding!!.root)

        val params = window!!.attributes
        //dialog全屏，并且底色改成透明
        params.width = context.getScreenWidth()
        params.height = context.getScreenHeight()
        window!!.attributes = params
        val drawable = GradientDrawable();
        drawable.setColor(Color.parseColor("#00000000"))
        window!!.setBackgroundDrawable(drawable)

        dataBinding!!.apply {
            //设置海报宽高
            val imageParams = container.layoutParams
            imageParams.width = (context.getScreenWidth() * 0.875).toInt()
            imageParams.height = imageParams.width
            container.layoutParams = imageParams

            if (isBuy){
                tvShareType.text ="买"
                tvShareType.setTextColor(0xFF33C891.toInt())
                tvShareProfitLoss.setTextColor(0xFF33C891.toInt())
            }else{
                tvShareType.text ="卖"
                tvShareType.setTextColor(0xFFC83333.toInt())
                tvShareProfitLoss.setTextColor(0xFFC83333.toInt())
            }

            tvShareMonetaryLever.text = "${monetaryLever}x"
            tvShareCoin.text = coinName
            tvShareProfitLoss.text = profitLoss
            tvOpenPrice.text = openPrice
            tvMarkPrice.text = markPrice

            vOutside.setOnClickListener {
                dismiss()
            }

            container.setOnClickListener {  }

            if (inviteCode == "") {
                val map = BtbMap()
                Http.getWalletService().invite(map)
                    .compose(CommonTransformer())
                    .subscribe(object : CommonSubscriber<String>(context) {
                        override fun onNext(list: String) {
                            inviteCode = list
                            tvInviteCode.text = list
                            val bitmap = ZXingUtils.Create2DCode(
                                shareUrl,
                                UIUtil.dip2px(context, 45.0),
                                UIUtil.dip2px(context, 45.0)
                            )
                            ivQrcode.setImageBitmap(bitmap)
                        }

                        override fun onError(e: ApiException) {

                        }
                    })
            }else{
                tvInviteCode.text = inviteCode
                val bitmap = ZXingUtils.Create2DCode(
                    shareUrl,
                    UIUtil.dip2px(context, 45.0),
                    UIUtil.dip2px(context, 45.0)
                )
                ivQrcode.setImageBitmap(bitmap)
            }

            ivDownload.setOnClickListener {
                savePic()
            }
        }
    }

    private fun getPath(): String? {
        val bitmap: Bitmap? = BitmapUtils.getBitmap(dataBinding!!.container)
        val path = FileUtils.saveImageToGallery(context, bitmap)

        if (!path.isNullOrEmpty()){
            ToastUitl.show(context.getString(R.string.save_pic_success), Toast.LENGTH_SHORT)
            dismiss()
        }else{
            ToastUitl.show(context.getString(R.string.save_pic_failed), Toast.LENGTH_SHORT)
        }
        return path
    }

    /**
     * 获取Bitmap
     *
     * @param view view
     * @return Bitmap
     */
    fun getBitmap(view: View): Bitmap? {
        val width = view.width
        val height = view.height

        // draw(canvas)获取Bitmap方法
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    private fun savePic() {
        if (PermissionUtils.checkPermissions(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            getPath()
        } else {
            PermissionUtils.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                255
            )
        }
    }

    companion object{
        var inviteCode: String = ""
    }

}