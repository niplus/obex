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
import android.os.ParcelFileDescriptor.MODE_READ_WRITE
import android.os.ParcelFileDescriptor.MODE_WORLD_READABLE
import android.provider.MediaStore
import android.util.Log
import com.fota.android.commonlib.utils.ToastUitl
import kotlinx.coroutines.*
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

fun Context.saveBitmap2Public(fileName: String, bitmap: Bitmap, block: (Boolean)->Unit){
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.ImageColumns.DISPLAY_NAME, fileName)
        put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/*")
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
        //10以下不能通过相对路径relative_path, 所以直接设置绝对路径
        contentValues.put(MediaStore.MediaColumns.DATA, "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$fileName")
    }else{
        contentValues.put(MediaStore.Images.ImageColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
    }

    var uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    //在一加7 pro上发现无法通过relative_path获取路径
    if (uri == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        contentValues.remove(MediaStore.Images.ImageColumns.RELATIVE_PATH)
        contentValues.put(MediaStore.MediaColumns.DATA, "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$fileName")
        uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    if (uri != null){
        MainScope().launch {
            withContext(Dispatchers.IO){
                val output = contentResolver.openOutputStream(uri)
                if (output == null){
                    block.invoke(false)
                    return@withContext
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
                output?.flush()
                output?.close()
                block.invoke(true)
            }
        }
    }else{
        block.invoke(false)
    }
}