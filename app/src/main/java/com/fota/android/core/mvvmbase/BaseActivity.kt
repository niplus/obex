package com.fota.android.core.mvvmbase

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.fota.android.LanguageContextWrapper.Companion.wrap
import com.fota.android.R
import com.fota.android.commonlib.base.AppConfigs
import com.fota.android.commonlib.base.MyActivityManager
import com.fota.android.utils.getLocale
import com.gyf.barlibrary.ImmersionBar
import com.ndl.lib_common.utils.showSnackMsg
import com.umeng.analytics.MobclickAgent
import me.jessyan.autosize.internal.CustomAdapt

abstract class BaseActivity<T : ViewDataBinding, H : BaseViewModel>: AppCompatActivity(), CustomAdapt {

    val dataBinding: T by lazy {
        DataBindingUtil.setContentView<T>(this, getLayoutId())
    }

    var viewModel: H? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppConfigs.getTheme() == AppConfigs.THEME_WHITE) {
            //默認是白天主題
            setTheme(R.style.AppTheme_White)
        } else {
            //否则是晚上主題
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)

        highApiEffects()
        MyActivityManager.getInstance().addActivity(this)
        initSystemBar()

        viewModel = createViewModel()
//        viewModel?.error?.observe(this, Observer {
//
//            if (it != null) {
//                if (it.isString){
//                    showSnackMsg(it.msg!!)
//                }else{
//                    showSnackMsg(getString(it.resId!!))
//                }
//            }
//        })
        initComp()
        initData()
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        MyActivityManager.getInstance().removeActivity(this)
    }

    abstract fun getLayoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()
    abstract fun initComp()

    abstract fun createViewModel(): H?

    override fun attachBaseContext(newBase: Context?) {
        val context: Context = wrap(newBase!!, getLocale())
        super.attachBaseContext(context)
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun highApiEffects() {
        window.decorView.fitsSystemWindows = true
        //透明状态栏 @顶部
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //透明导航栏 @底部 这一句不要加，目的是防止沉浸式状态栏和部分底部自带虚拟按键的手机（比如华为）发生冲突，注释掉就好了
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    lateinit var mImmersionBar: ImmersionBar
    protected open fun initSystemBar() {
        mImmersionBar = ImmersionBar.with(this)
        mImmersionBar.keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        if (AppConfigs.getTheme() == 0) {
            mImmersionBar.statusBarDarkFont(false, 0.2f)
        } else { //白色主题设置黑色状态栏字体
            mImmersionBar.statusBarDarkFont(true, 0.2f)
        }
//        mImmersionBar.fitsSystemWindows(true)
        mImmersionBar.init()
    }

    override fun isBaseOnWidth(): Boolean {
        return true
    }

    override fun getSizeInDp(): Float {
        return 375f
    }
}