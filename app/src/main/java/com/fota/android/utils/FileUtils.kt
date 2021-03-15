/**
 *
 *
 * getCacheDir  获取/data/data//cache目录
 * getFileDir   获取/data/data//files目录
 *
 * getExternalCacheDir sdCard/Android/data//cache
 * getExternalFileDir sdcard/Android/data//files
 */

package com.fota.android.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


fun Context.getSandBoxDir(type: String): File? {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() ||
            !Environment.isExternalStorageRemovable())
        getExternalFilesDir(type)
    else {
        Log.d("obex", "external dir is not exist")
        filesDir
    }
}

fun Context.saveBitmap2File(fileName: String, bitmap: Bitmap){
    val dir = getSandBoxDir(Environment.DIRECTORY_PICTURES)
    if (dir != null && dir.exists()){
        val imgFile = File("${dir.absolutePath}${File.separator}share${File.separator}$fileName")
        imgFile.createNewFile()
        if (imgFile.exists())
            GlobalScope.launch {

                try {
                    val fos = FileOutputStream(imgFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                    fos.flush()
                    fos.close()
                }catch (e: Exception){
                }

            }

    }
}

fun Context.saveBitmap2Public(fileName: String){

    if (Build.VERSION.SDK_INT < 29){

    }else{//适配android 10+
        val contentUri = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) MediaStore.Images.Media.EXTERNAL_CONTENT_URI else MediaStore.Images.Media.INTERNAL_CONTENT_URI
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/*")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/")
            put(MediaStore.MediaColumns.IS_PENDING, 1) //告诉系统，文件还未准备好，暂时不对外暴露
        }

        val uri = contentResolver.insert(contentUri, contentValues)
    }

}