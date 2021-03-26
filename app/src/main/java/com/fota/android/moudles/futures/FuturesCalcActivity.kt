package com.fota.android.moudles.futures

import android.text.InputFilter
import androidx.lifecycle.ViewModelProvider
import com.fota.android.R
import com.fota.android.commonlib.utils.DecimalDigitsInputFilter
import com.fota.android.core.mvvmbase.BaseActivity
import com.fota.android.databinding.ActivityFuturesCalcBinding
import com.fota.android.moudles.futures.viewmodel.FuturesCalcViewModel

class FuturesCalcActivity : BaseActivity<ActivityFuturesCalcBinding, FuturesCalcViewModel>() {

    private var amountPercision = 0

    override var initComp: ((ActivityFuturesCalcBinding) -> Unit)? = {
        it.apply {
            model = viewModel
            pbLever.leverChangeListener = { level->
                viewModel!!.lever = level
            }

            viewModel!!.coinName.set(intent.getStringExtra("coinName"))
            layoutTitle.setAppTitle(intent.getStringExtra("coinName") + "USDT")
            layoutTitle.llLeftGoBack.setOnClickListener {
                finish()
            }

            rgType.setOnCheckedChangeListener { _, checkedId ->
                viewModel!!.isBuy = checkedId == R.id.rb_buy
            }

            amountPercision = intent.getIntExtra("amountPercision", 0)

            edtOpen.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(2))
            edtClose.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(2))
            edtAmount.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(amountPercision))
//            viewModel!!.currentPrice = intent.getStringExtra("currentPrice")!!
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_futures_calc
    }

    override fun createViewModel(): FuturesCalcViewModel? {
        return ViewModelProvider(this).get(FuturesCalcViewModel::class.java)
    }

    override fun initData() {
    }
}