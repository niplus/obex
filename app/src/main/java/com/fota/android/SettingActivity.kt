package com.fota.android

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fota.android.app.ConstantsPage
import com.fota.android.app.FotaApplication
import com.fota.android.commonlib.base.AppConfigs
import com.fota.android.commonlib.http.exception.ApiException
import com.fota.android.commonlib.http.rx.CommonSubscriber
import com.fota.android.commonlib.http.rx.CommonTransformer
import com.fota.android.commonlib.utils.L
import com.fota.android.commonlib.utils.Pub
import com.fota.android.core.base.BtbMap
import com.fota.android.core.base.SimpleFragmentActivity
import com.fota.android.core.dialog.DialogModel
import com.fota.android.core.dialog.DialogUtils
import com.fota.android.core.event.Event
import com.fota.android.core.event.EventWrapper
import com.fota.android.core.mvvmbase.BaseActivity
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.databinding.FragmentSettingBinding
import com.fota.android.http.Http
import com.fota.android.moudles.mine.bean.MineBean
import com.fota.android.moudles.mine.bean.MineBean.UserSecurity
import com.fota.android.moudles.mine.bean.VersionBean
import com.fota.android.service.UpdateIntentService
import com.fota.android.utils.*
import com.fota.android.widget.btbwidget.FotaButton
import com.fota.android.widget.dialog.ShareDialog.Companion.inviteCode
import com.fota.android.widget.dialog.UpdateDialog
import com.fota.android.widget.dialog.UpdateDialog.Companion.downloadUrl
import com.fota.android.widget.popwin.CommomDialog
import com.ndl.lib_common.utils.LiveDataBus.getBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class SettingActivity : BaseActivity<FragmentSettingBinding, BaseViewModel>(), View.OnClickListener{
    val launcher = registerForActivityResult(object : ActivityResultContract<File, File?>(){
        var file: File? = null
        override fun createIntent(context: Context, input: File?): Intent {
            file = input
            val packageURI = Uri.parse("package:$packageName")
            Log.i("update_version", "permission open")
            return Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): File? {
            if (resultCode == Activity.RESULT_OK && file != null) return file!!
            else {
                Log.i("update_version", "file is null")
                return null
            }
        }

    }){
        if (it != null)
        AppUtils.installApk(this,it)
    }

    private var mUpdateDialog: UpdateDialog? = null
    val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        mUpdateDialog?.startWork()
    }


    private var logOut = false

    //    AppDownloadManager appDownloadManager = null;
    var updateDialog: CommomDialog? = null
    var updateVersionBean: VersionBean? = null
    private var userSecurity: UserSecurity? = null
    override fun getLayoutId(): Int {
        return R.layout.fragment_setting
    }

    override fun initSystemBar() {

    }
    override fun initData() {

//        userSecurity = bundle.getSerializable("security")

        getBus<Any>("recreate").observe(this, object : Observer<Any?> {
            override fun onChanged(t: Any?) {
                if (t!! == "true")
                    finish()
            }
        })
    }

    override fun initComp() {
        dataBinding.apply {
            setView(this@SettingActivity)
            btnLogout.setOnClickListener(this@SettingActivity)
            if (UserLoginUtil.havaUser()) {
                btnLogout.setVisibility(View.VISIBLE)
            } else {
                btnLogout.setVisibility(View.GONE)
            }
            tvLanguage.setText(getLanguageString())
            tvAboutfota.setOnClickListener(this@SettingActivity)
            rlLanguage.setOnClickListener(this@SettingActivity)
//        rlBg.setOnClickListener(this);

            //        rlBg.setOnClickListener(this);
            tvVersionupdate.setOnClickListener(this@SettingActivity)
//        if (AppConfigs.getTheme() == 0) {
//            tvBg.setText(R.string.set_bg_black);
//        } else {//白色主题设置黑色状态栏字体
//            tvBg.setText(R.string.set_bg_white);
//        }
//        appDownloadManager = new AppDownloadManager(getActivity());

            //        if (AppConfigs.getTheme() == 0) {
//            tvBg.setText(R.string.set_bg_black);
//        } else {//白色主题设置黑色状态栏字体
//            tvBg.setText(R.string.set_bg_white);
//        }
//        appDownloadManager = new AppDownloadManager(getActivity());
            tvSafe.setOnClickListener(this@SettingActivity)
            tlIdentity.setOnClickListener(this@SettingActivity)
            tvTradelever.setOnClickListener(this@SettingActivity)

            val theme = AppConfigs.getTheme()
            if (theme == 0) { //黑板
                cbTheme.setChecked(true)
            } else {
                cbTheme.setChecked(false)
            }
            cbTheme.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                if (b) { //选中，变黑板
                    AppConfigs.themeOrLangChanged = true
                    AppConfigs.setTheme(0)
                    //                    notify(R.id.event_theme_changed);
                    recreate()
                    EventWrapper.post(Event.create(R.id.mine_refreshbar)) //通知我的页面刷新状态栏
                    val event = Event.create(R.id.event_theme_changed)
                    event.putParam(Int::class.java, R.id.event_theme_changed)
                    EventWrapper.post(event)
                } else {
                    AppConfigs.themeOrLangChanged = true
                    AppConfigs.setTheme(1)
                    recreate()
                    EventWrapper.post(Event.create(R.id.mine_refreshbar)) //通知我的页面刷新状态栏
                    //                    notify(R.id.event_theme_changed);
                    val event = Event.create(R.id.event_theme_changed)
                    event.putParam(Int::class.java, R.id.event_theme_changed)
                    EventWrapper.post(event)
                }
            })
            if (UserLoginUtil.havaUser()) setIdIcon()
        }
        
    }

    override fun createViewModel(): BaseViewModel {
        return ViewModelProvider(this).get(BaseViewModel::class.java)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_logout -> DialogUtils.showDialog(this,
                DialogModel() //.setView(DialogUtils.getDefaultStyleMsgTV(this, getString(R.string.mine_logout)))
                    .setMessage(getString(R.string.mine_logout))
                    .setSureText(getString(R.string.sure))
                    .setCancelText(getString(R.string.cancel))
                    .setSureClickListen { dialogInterface, i ->
                        logOut()
                        dialogInterface.dismiss()
                    }
            )
            R.id.rl_language -> SimpleFragmentActivity.gotoFragmentActivity(
                this,
                ConstantsPage.LanguageFragment
            )
            R.id.tv_aboutfota -> SimpleFragmentActivity.gotoFragmentActivity(
                this,
                ConstantsPage.AboutFotaFragment
            )
            R.id.tv_versionupdate -> //                Beta.checkUpgrade();
                checkUpdate()
            R.id.tv_safe -> {
                val bundle = Bundle()
                bundle.putSerializable("security", userSecurity)
                SimpleFragmentActivity.gotoFragmentActivity(
                    this,
                    ConstantsPage.SafeSettingFragment,
                    bundle
                )
            }
            R.id.tl_identity -> if (UserLoginUtil.havaUser()) {
                val bundle_id = Bundle()
                if (userSecurity == null || userSecurity!!.cardCheckStatus == 2) return
                bundle_id.putSerializable("cardCheckStatus", userSecurity!!.cardCheckStatus)
                SimpleFragmentActivity.gotoFragmentActivity(
                    this,
                    ConstantsPage.IdentityFragment,
                    bundle_id
                )
            } else {
                SimpleFragmentActivity.gotoFragmentActivity(
                    this,
                    ConstantsPage.IdentityFragment
                )
            }
            R.id.tv_tradelever -> SimpleFragmentActivity.gotoFragmentActivity(
                this,
                ConstantsPage.TradeLeverFragment
            )
        }
    }

    /**
     * 退出登录
     */
    fun logOut() {
        if (!UserLoginUtil.havaUser()) {
            return
        }
        logOut = true
        dataBinding.btnLogout.setVisibility(View.GONE)
        Http.getHttpService().logOut()
            .compose(CommonTransformer())
            .subscribe(object : CommonSubscriber<String>(this) {
                override fun onNext(outBean: String) {
                    UserLoginUtil.delUser()
                    inviteCode = ""
                    FtRounts.toQuickLogin(this@SettingActivity)
                    FotaApplication.setLoginStatus(false)
                    finish()
                }

                override fun onError(e: ApiException) {
                    super.onError(e)
                    UserLoginUtil.delUser()
                    FotaApplication.setLoginStatus(false)
                    FtRounts.toQuickLogin(this@SettingActivity)
                    finish()
                }
            })
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onEventPosting(event: Event) {
        when (event._id) {
            R.id.event_theme_changed, R.id.event_language_changed -> recreate()
            R.id.update_downloaded -> //                getHoldingActivity().recreate();
                //recreate();
                if (updateDialog != null && updateDialog!!.isShowing && updateVersionBean != null) {
                    if (updateVersionBean!!.isCompulsory) { //强更
                    } else {
                        updateDialog!!.findViewById<View>(R.id.ll_cancel).visibility = View.VISIBLE
                    }
                    updateDialog!!.findViewById<View>(R.id.submit).isEnabled = true
                    (updateDialog!!.findViewById<View>(R.id.submit) as FotaButton).setText(R.string.update_sure)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == 0x1){

        }
    }
    /**
     * 检测更新
     */
    fun checkUpdate() {
        val map = BtbMap()
        map.p("version", DeviceUtils.getVersonName(FotaApplication.getInstance()))
        map.p("platform", 2)
        Http.getHttpService().getVersionUpdate(map)
            .compose(CommonTransformer())
            .subscribe(object : CommonSubscriber<VersionBean>(this) {
                override fun onNext(versionBean: VersionBean) {
                    L.a("version ===   suc $versionBean")
                    updateVersionBean = versionBean
                    mUpdateDialog = UpdateDialog(this@SettingActivity)
                    downloadUrl = updateVersionBean!!.url
                    mUpdateDialog!!.show()
//                    if (versionBean.isNewest) {
//                        showSnackMsg(getString(R.string.update_newest))
//                    } else {
//                        showUpdateDialog(versionBean)
//                    }
                }

                override fun onError(e: ApiException) {
                    L.a("version ===   fail ")
                }
            })
    }


    /**
     * 更新弹窗
     *
     * @param versionBean
     */
    private fun showUpdateDialog(versionBean: VersionBean) {
        val dialogModel =
            DialogModel() //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                .setMessage(versionBean.text)
                .setSureText(getString(R.string.update_sure))
                .setTitle(getString(R.string.update_title))
                .setClickAutoDismiss(false)
                .setCanCancelOnTouchOutside(false)
                .setCancelClickListen { dialogInterface, i -> dialogInterface.dismiss() }
                .setSureClickListen(
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        updateDialog!!.findViewById<View>(R.id.ll_cancel).visibility = View.GONE
                        updateDialog!!.findViewById<View>(R.id.submit).isEnabled = false
                        (updateDialog!!.findViewById<View>(R.id.submit) as FotaButton).setText(R.string.update_downloading)
                        if (TextUtils.isEmpty(versionBean.url)) return@OnClickListener
                        beforeUpdateWork(versionBean.url)
                    })
        if (versionBean.isCompulsory) {
            dialogModel.setCancelable(false).isCanCancelOnTouchOutside = false
        } else {
            dialogModel.cancelText = getString(R.string.cancel)
        }
        updateDialog = DialogUtils.getDialog(this, dialogModel)
        updateDialog!!.show()
        (updateDialog!!.findViewById<View>(R.id.content) as TextView).gravity =
            Gravity.CENTER_VERTICAL
    }


    /**
     * 开始下载
     *
     * @param url
     */
    private fun beforeUpdateWork(url: String) {

        toIntentServiceUpdate(url)
    }

    private fun toIntentServiceUpdate(url: String) {
        val updateIntent = Intent(
            this,
            UpdateIntentService::class.java
        )
        updateIntent.action = UpdateIntentService.ACTION_UPDATE
        updateIntent.putExtra("appName", "update-1.0.1")
        //随便一个apk的url进行模拟
        updateIntent.putExtra("downUrl", url)
        startService(updateIntent)
    }

    /**
     * 设置身份认证图标
     */
    private fun setIdIcon() {
        if (userSecurity != null) {
            val drawableRight = resources.getDrawable(
                Pub.getThemeResource(this, R.attr.icon_right)
            )
            if (userSecurity!!.cardCheckStatus == 0) {
                val drawableleft = resources.getDrawable(
                    R.mipmap.icon_id_uncheck
                )
               dataBinding.tvIdentity.setText(R.string.safesetting_goauth)
               dataBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(
                    drawableleft,
                    null,
                    drawableRight,
                    null
                )
            } else if (userSecurity!!.cardCheckStatus == 1 || userSecurity!!.cardCheckStatus == 4) {
                val drawableleft = resources.getDrawable(
                    R.mipmap.icon_id_uncheck
                )
               dataBinding.tvIdentity.setText(R.string.safesetting_ident_shenhezhong)
               dataBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(
                    drawableleft,
                    null,
                    drawableRight,
                    null
                )
            } else if (userSecurity!!.cardCheckStatus == 2) {
                val drawableleft = resources.getDrawable(
                    R.mipmap.safe_icon_setted
                )
               dataBinding.tvIdentity.setText(R.string.safesetting_ident_over)
               dataBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(
                    drawableleft,
                    null,
                    null,
                    null
                )
            } else if (userSecurity!!.cardCheckStatus == 3) {
                val drawableleft = resources.getDrawable(
                    R.mipmap.icon_id_uncheck
                )
               dataBinding.tvIdentity.setText(R.string.safesetting_ident_fail)
               dataBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(
                    drawableleft,
                    null,
                    drawableRight,
                    null
                )
            } else {
//               dataBinding.tvIdentity.setText(R.string.safesetting_ident_shenhezhong);
//               dataBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (UserLoginUtil.havaUser()) {
           dataBinding.btnLogout.setVisibility(View.VISIBLE)
        } else {
           dataBinding.btnLogout.setVisibility(View.GONE)
        }
        if (!UserLoginUtil.havaUser()) return
        getMindeMsg()
    }

    /**
     * 获取我的数据
     */
    fun getMindeMsg() {
        Http.getHttpService().mineData
            .compose(CommonTransformer())
            .subscribe(object : CommonSubscriber<MineBean>(this) {
                override fun onNext(mineBean: MineBean) {

                    if (mineBean != null && mineBean.userSecurity != null) {
                        userSecurity = mineBean.userSecurity
                        setIdIcon()
                    }
                }

                override fun onError(e: ApiException) {
                    super.onError(e)
                }

                override fun showLoading(): Boolean {
                    return false
                }
            })
    }

}