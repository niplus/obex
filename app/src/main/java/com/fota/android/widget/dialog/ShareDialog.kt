package com.fota.android.widget.dialog

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.fota.android.R
import com.fota.android.commonlib.http.exception.ApiException
import com.fota.android.commonlib.http.rx.CommonSubscriber
import com.fota.android.commonlib.http.rx.CommonTransformer
import com.fota.android.commonlib.utils.UIUtil
import com.fota.android.core.base.BtbMap
import com.fota.android.databinding.DialogShareBinding
import com.fota.android.http.Http
import com.fota.android.utils.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ShareDialog(
    context: Context,
    isBuy: Boolean,
    monetaryLever: Int,
    coinName: String,
    profitLoss: String,
    openPrice: String,
    markPrice: String,
val activity: Activity) : Dialog(context) {

    var dataBinding: DialogShareBinding? = null

    init {
        dataBinding = DataBindingUtil.inflate<DialogShareBinding>(LayoutInflater.from(context), R.layout.dialog_share, null, false)
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

            if (inviteCode == "") {
                val map = BtbMap()
                Http.getWalletService().invite(map)
                    .compose(CommonTransformer())
                    .subscribe(object : CommonSubscriber<String>(context) {
                        override fun onNext(list: String) {
                            inviteCode = list
                            tvInviteCode.text = list
                            val bitmap = ZXingUtils.Create2DCode(
                                "https://invite.cboex.com/#/share?invitationCode=$list",
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
                    "https://invite.cboex.com/#/share?invitationCode=$inviteCode",
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
        Log.i("nidongliang", "container: ${dataBinding?.container}")
        val bitmap: Bitmap? = getBitmap(dataBinding!!.container)
        Log.i("nidongliang", "bitmap: $bitmap")

        context.saveBitmap2File("test.jpg", bitmap!!)
        return ""
//        return saveImageToGallery(context, bitmap!!)
    }

    //保存文件到指定路径
    fun saveImageToGallery(context: Context, bmp: Bitmap): String? {
        // 首先保存图片
        val storePath =
            Environment.getExternalStorageDirectory().toString() + "/images/"
        val appDir = File(storePath)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            //通过io流的方式来压缩保存图片
            val isSuccess =
                bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos)
            fos.flush()
            fos.close()

            //把文件插入到系统图库

            //保存图片后发送广播通知更新数据库
            val uri = Uri.fromFile(file)
            // 通知图库更新
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                null
            ) { path, uri ->
                val mediaScanIntent =
                    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = uri
                context.sendBroadcast(mediaScanIntent)
            }
            val path = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                file.absolutePath,
                fileName,
                null
            )
            val delete = file.delete()
            return if (delete) {
                path
            } else {
                ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
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

        // getDrawingCache()获取Bitmap方法
//        view.setDrawingCacheEnabled(true);
//        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
//        view.destroyDrawingCache();
//        view.setDrawingCacheEnabled(false);

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
            val path: String? = getPath()
            if (!TextUtils.isEmpty(path)) {
                Toast.makeText(context, "图片保存成功", Toast.LENGTH_SHORT).show()
            }
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