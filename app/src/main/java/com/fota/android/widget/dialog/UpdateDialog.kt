package com.fota.android.widget.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import androidx.work.*
import com.fota.android.R
import com.fota.android.SettingActivity
import com.fota.android.core.mvvmbase.BaseActivity
import com.fota.android.http.ApiService
import com.fota.android.http.Http
import com.fota.android.utils.AppUtils
import com.fota.android.utils.downloadutils.FileUtil
import java.io.File
import java.io.FileOutputStream

class UpdateDialog(private val mActivity: Activity): Dialog(mActivity), LifecycleOwner{

    var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    private val downloadRequest: OneTimeWorkRequest

    companion object{
        var downloadUrl: String? = null
    }
    init {
        mLifecycleRegistry.currentState = Lifecycle.State.STARTED
        setContentView(R.layout.dialog_update)

        val param = window!!.attributes
        param.width = ViewGroup.LayoutParams.MATCH_PARENT
        window!!.attributes = param
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window!!.decorView.setPadding(50, 0, 50, 0)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        val progressbar = findViewById<ProgressBar>(R.id.progress)
        val tvProgress = findViewById<TextView>(R.id.tv_progress)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        downloadRequest = OneTimeWorkRequest.Builder(UpdateWork::class.java)
            .addTag("update")
            .setConstraints(constraints)
            .build()

        Log.i("update_version", "uuid: ${downloadRequest.id}")
        WorkManager.getInstance(context)
            .getWorkInfoByIdLiveData(downloadRequest.id)
            .observe(this, Observer {
                if ( it.state == WorkInfo.State.SUCCEEDED){
                    tvProgress.text = "100%"
                    progressbar.progress = 100

                    val file = it.outputData.getString("file")
                    installProcess(File(file))
                }else{
                    val progress =  (it.progress.getFloat("progress", 0f) * 100).toInt()
                    tvProgress.text = "$progress%"
                    progressbar.progress = progress
                }
            })
        findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
            if (mActivity is SettingActivity){
                mActivity.permissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )

                )
            }

        }
    }

    fun startWork(){
        WorkManager
            .getInstance(context)
            .enqueueUniqueWork("update", ExistingWorkPolicy.KEEP, downloadRequest)
    }

    override fun show() {
        mLifecycleRegistry.currentState = Lifecycle.State.RESUMED
        super.show()
    }

    override fun dismiss() {
        mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        super.dismiss()
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    /**
     * 安装过程处理
     */
    private fun installProcess(file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canInstallPackage: Boolean =
                context.packageManager.canRequestPackageInstalls()
            if (canInstallPackage) {
                Log.i("update_version", "permission grant")
                AppUtils.installApk(context, file)
            } else {
                if (mActivity is SettingActivity)
                    mActivity.launcher.launch(file)
            }
        } else {
            Log.i("update_version", "version low")
            AppUtils.installApk(context, file)
        }
    }



}

class UpdateWork(context: Context, params: WorkerParameters): CoroutineWorker(context, params){
    override suspend fun doWork(): Result {
        Log.i("update_version", "thread: ${Thread.currentThread()}")
            try {
                val result = Http.getRetrofit().create(ApiService::class.java).downLoad(UpdateDialog.downloadUrl!!)

                val body = result.body()?: throw RuntimeException("下载失败")
                val length = body.contentLength()

                val updateFile = FileUtil.getDiskCacheDir(
                    applicationContext,
//                    "update-1.0.1" + System.currentTimeMillis() + ".apk"
                    "test.apk"
                )

                Log.i("update_version", "file dir: ${updateFile.absolutePath}")

                val input = body.byteStream()
                val output = FileOutputStream(updateFile)
                val bufferSize = 1024 * 8
                val buffer = ByteArray(bufferSize)
                var readSize = 0
                var currentProgress = 0
                while (input.read(buffer, 0, bufferSize).also { readSize = it } != -1){
                    output.write(buffer, 0, readSize)
                    currentProgress += readSize
                    setProgress(workDataOf("progress" to currentProgress.toFloat() / length.toFloat()))
                }

                input.close()
                output.close()

                val command = "chmod 777 ${updateFile.absolutePath}"
                val runtime = Runtime.getRuntime()
                runtime.exec(command)

                return Result.success(workDataOf("file" to updateFile.absolutePath))

            }catch (e: Exception){
                e.stackTrace
                Log.i("update_version", "exception: ${e.message}")
                return Result.failure()
            }
    }
}