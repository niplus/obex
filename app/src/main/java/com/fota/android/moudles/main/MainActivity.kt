package com.fota.android.moudles.main

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fota.android.R
import com.fota.android.app.ConstantsPage
import com.fota.android.app.FotaApplication
import com.fota.android.commonlib.base.MyActivityManager
import com.fota.android.commonlib.http.exception.ApiException
import com.fota.android.commonlib.http.rx.CommonSubscriber
import com.fota.android.commonlib.http.rx.CommonTransformer
import com.fota.android.commonlib.utils.L
import com.fota.android.commonlib.utils.SharedPreferencesUtil
import com.fota.android.commonlib.utils.TimeUtils
import com.fota.android.core.base.BtbMap
import com.fota.android.core.base.SimpleFragmentActivity
import com.fota.android.core.dialog.DialogModel
import com.fota.android.core.dialog.DialogUtils
import com.fota.android.core.event.Event
import com.fota.android.core.mvvmbase.BaseActivity
import com.fota.android.databinding.ActivityMain2Binding
import com.fota.android.http.Http
import com.fota.android.moudles.exchange.index.ExchangeFragment
import com.fota.android.moudles.futures.FuturesFragment
import com.fota.android.moudles.futures.bean.ToTradeEvent
import com.fota.android.moudles.futures.view.FutureFragment
import com.fota.android.moudles.home.HomeFragment
import com.fota.android.moudles.main.viewmodel.MainViewModel
import com.fota.android.moudles.market.MarketFragment
import com.fota.android.moudles.market.bean.FutureItemEntity
import com.fota.android.moudles.mine.MineFragment
import com.fota.android.moudles.mine.bean.VersionBean
import com.fota.android.service.UpdateIntentService
import com.fota.android.utils.DeviceUtils
import com.fota.android.utils.UserLoginUtil
import com.fota.android.widget.btbwidget.FotaButton
import com.fota.android.widget.popwin.CommomDialog
import com.google.android.material.tabs.TabLayout
import com.ndl.lib_common.utils.LiveDataBus
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : BaseActivity<ActivityMain2Binding, MainViewModel>() {



    private val tabString by lazy {
        mutableListOf(getString(R.string.main_tab5), getString(R.string.main_tab1), getString(R.string.main_tab2), getString(R.string.main_tab3), getString(R.string.main_tab4))
    }
    private val tabIcon = mutableListOf(
            R.drawable.selector_home_tab,
            R.drawable.selector_market_tab,
            R.drawable.selector_spot_tab,
            R.drawable.selector_future_tab,
            R.drawable.selector_mine_tab
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        fragments.clear()
//        fragments.add(HomeFragment())
//        fragments.add(MarketFragment())
//        fragments.add(ExchangeFragment())
//        fragments.add(FuturesFragment())
//        fragments.add(MineFragment())

        Log.i("===============", "oncreate")
    }

    override fun onResume() {
        super.onResume()
        Log.i("===============", "onResume")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.i("===============", "onNewIntent")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("===============", "onSaveInstanceState")
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main2
    }


    override fun initData() {
        LiveDataBus.getBus<String>("recreate").observe(this, Observer {
            if (it == "true") {
//                fragments.clear()
//                dataBinding.vpPage.adapter?.notifyDataSetChanged()
//                finish()
//                recreate()
                LiveDataBus.getBus<String>("recreate").value = "recreate"
            }else if (it == "recreate"){
//                clearFragmentsBeforeCreate()
//                viewModel!!.fragments.clear()
//                viewModel!!.fragments.add( HomeFragment())
//                viewModel!!.fragments.add( MarketFragment())
//                viewModel!!.fragments.add( ExchangeFragment())
//                viewModel!!.fragments.add( FuturesFragment())
//                viewModel!!.fragments.add( MineFragment())
//                dataBinding.vpPage.adapter?.notifyDataSetChanged()
                LiveDataBus.getBus<String>("recreate").value = "false"
            }
        })
        LiveDataBus.getBus<ToTradeEvent>("trade").observe(this, Observer {
            if (it.futureItemEntity.entityType == 3) {
                dataBinding.vpPage.setCurrentItem(2, false)
            } else {
                dataBinding.vpPage.setCurrentItem(3, false)
            }
        })

        dataBinding.apply {
            val adapter = object : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
                override fun getCount(): Int {
                    return viewModel!!.fragments.size
                }

                override fun getItem(position: Int): Fragment {
                    return viewModel!!.fragments[position]
                }

            }
            vpPage.adapter = adapter

            tabString.forEachIndexed { index, s ->
                val tabView = LayoutInflater.from(this@MainActivity).inflate(
                    R.layout.item_main_tab,
                    null
                )
                tabView.findViewById<TextView>(R.id.tv_tab).text = s
                tabView.findViewById<ImageView>(R.id.iv_tab).setImageResource(tabIcon[index])
                bnvNavigation.addTab(bnvNavigation.newTab().setCustomView(tabView))
            }
            vpPage.offscreenPageLimit = 5

            bnvNavigation.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    vpPage.setCurrentItem(tab.position, false)

                    when (tab.position) {
                        2 -> {
                            (viewModel!!.fragments[tab.position] as ExchangeFragment).onRefresh()
                        }
                        3 -> {
                            (viewModel!!.fragments[tab.position] as FuturesFragment).onRefresh()
                        }
                        1 -> {
                            (viewModel!!.fragments[tab.position] as MarketFragment).onRefresh()
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })

            vpPage.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    bnvNavigation.getTabAt(position)!!.select()
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

            })
        }

        showNoticeDialog()

        checkUpdate()
    }

    override fun createViewModel(): MainViewModel {
        return ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
    override fun initComp() {
        EventBus.getDefault().register(this)

    }

    /**
     * 开启通知权限
     */
    private fun showNoticeDialog() {
        val manager = NotificationManagerCompat.from(FotaApplication.getInstance())
        val isOpened = manager.areNotificationsEnabled()
        if (isOpened) {
            SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.NOTICE_TIMES, 0)
            return
        }
        var times =
            SharedPreferencesUtil.getInstance().get(SharedPreferencesUtil.Key.NOTICE_TIMES, -1)
        if (times < 15 && times != -1) {
            times++
            SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.NOTICE_TIMES, times)
            return
        }
        SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.NOTICE_TIMES, 1)
        DialogUtils.showDialog(this, DialogModel()
                .setMessage(getString(R.string.trade_notice_msg))
                .setSureText(getString(R.string.sure))
                .setCancelText(getString(R.string.cancel))
                .setSureClickListen { dialogInterface, i ->
                    toSetting()
                    dialogInterface.dismiss()
                }.setCancelClickListen { dialogInterface, i -> dialogInterface.dismiss() }
        )
    }

    private fun toSetting() {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        localIntent.data = Uri.fromParts("package", packageName, null)
        startActivity(localIntent)
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
                .subscribe(object : CommonSubscriber<VersionBean>(FotaApplication.getInstance()) {
                    override fun onNext(versionBean: VersionBean) {
//                        UserLoginUtil.delUser();
                        L.a("version ===   suc $versionBean")
                        updateVersionBean = versionBean
                        if (versionBean.isNewest) {
//                            showToazhegst("你已经是最新版本");
                        } else {
                            if (versionBean.isCompulsory) { //强更
                                showUpdateDialog(versionBean)
                            } else {
                                val version = SharedPreferencesUtil.getInstance().get(SharedPreferencesUtil.Key.UPDATE_VERSION, "")
                                if (!TextUtils.isEmpty(version) && version != versionBean.version) { //没有获取过此版本信息，弹出并保存版本信息
                                    showUpdateDialog(versionBean)
                                    if (!TextUtils.isEmpty(versionBean.version)) {
                                        SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.UPDATE_VERSION, versionBean.version)
                                        SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.UPDATE_TIME, System.currentTimeMillis())
                                    }
                                } else { //获取过此版本信息
                                    val oldTime = SharedPreferencesUtil.getInstance().get(SharedPreferencesUtil.Key.UPDATE_TIME, java.lang.Long.valueOf(0))
                                    if (TimeUtils.aboveOneday(System.currentTimeMillis(), oldTime)) { //距离上次显示超过1天
                                        showUpdateDialog(versionBean)
                                        SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.UPDATE_TIME, System.currentTimeMillis()) //更新显示时间
                                    } else {
                                    }
                                }
                            }
                        }
                    }

                    override fun onError(e: ApiException) {
                        L.a("version ===   fail ")
                    }
                })
    }

    var updateDialog: CommomDialog? = null
    var updateVersionBean: VersionBean? = null
    /**
     * 更新弹窗
     *
     * @param versionBean
     */
    private fun showUpdateDialog(versionBean: VersionBean) {
        val dialogModel = DialogModel() //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                .setMessage(versionBean.text)
                .setSureText(getString(R.string.update_sure))
                .setTitle(getString(R.string.update_title))
                .setClickAutoDismiss(false)
                .setCanCancelOnTouchOutside(false)
                .setCancelClickListen { dialogInterface, i -> dialogInterface.dismiss() }.setSureClickListen(DialogInterface.OnClickListener { dialogInterface, i ->
                    updateDialog?.findViewById<View>(R.id.ll_cancel)?.setVisibility(View.GONE)
                    updateDialog?.findViewById<FotaButton>(R.id.submit)?.setEnabled(false)
                    (updateDialog?.findViewById(R.id.submit) as FotaButton).setText(R.string.update_downloading)
                    //                        beforeUpdateWork("http://172.16.50.201:8089/mapi/home/download");
                    if (TextUtils.isEmpty(versionBean.url)) return@OnClickListener
                    beforeUpdateWork(versionBean.url)
                })
        if (versionBean.isCompulsory) {
            dialogModel.setCancelable(false).isCanCancelOnTouchOutside = false
        } else {
            dialogModel.cancelText = getString(R.string.cancel)
        }
        updateDialog = DialogUtils.getDialog(this, dialogModel)
        updateDialog?.show()
        (updateDialog?.findViewById(R.id.content) as TextView).gravity = Gravity.CENTER_VERTICAL
    }

    private fun beforeUpdateWork(url: String) {
//        if (!isEnableNotification()) {
//            showNotificationAsk();
//            return;
//        }
        toIntentServiceUpdate(url)
    }

    private fun toIntentServiceUpdate(url: String) {
        val updateIntent = Intent(this, UpdateIntentService::class.java)
        updateIntent.action = UpdateIntentService.ACTION_UPDATE
        updateIntent.putExtra("appName", "update-1.0.1")
        //随便一个apk的url进行模拟
        updateIntent.putExtra("downUrl", url)
        //        updateIntent.putExtra("downUrl", "http://192.168.1.173:8084/home/download");
        startService(updateIntent)
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onEventPosting(event: Event) {
        when (event._id) {
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
            R.id.login_quicktoast -> Handler().postDelayed({ showQuickLoginDialog() }, 1000)
        }
    }

    private fun showQuickLoginDialog() {
        if (!UserLoginUtil.haveQuickLogin()) DialogUtils.showDialog(MyActivityManager.getInstance().currentActivity, DialogModel() //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                .setMessage(getString(R.string.quicklogin_guide_msg))
                .setTitle(getString(R.string.quicklogin_guide_title))
                .setSureText(getString(R.string.goto_set_quicklog))
                .setCancelText(getString(R.string.cancel))
                .setSureClickListen { dialogInterface, i ->
                    SimpleFragmentActivity.gotoFragmentActivity(MyActivityManager.getInstance().currentActivity,
                            ConstantsPage.SafeSettingFragment
                    )
                    dialogInterface.dismiss()
                })
    }
}