package com.fota.android.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

class AppUtils {
    companion object{
        fun installApk(context: Context, file: File){
            if (!file.exists()) {
                return
            }
            Log.i("update_version", "path: ${file.absolutePath}")
            val authority = context.packageName + ".apkfileProvider"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.putExtra("name", "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val contentUri = FileProvider.getUriForFile(context, authority, file)
                intent.setDataAndType(
                    contentUri,
                    "application/vnd.android.package-archive"
                )
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                intent.setDataAndType(
                    Uri.fromFile(file),
                    "application/vnd.android.package-archive"
                )
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}