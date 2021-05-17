package com.fota.android

import androidx.fragment.app.Fragment
import com.fota.android.core.mvvmbase.BaseViewModel
import com.fota.android.moudles.exchange.complete.ExchangeCompleteFragment
import com.fota.android.moudles.exchange.money.ExchangeMoneyListFragment
import com.fota.android.moudles.exchange.orders.ExchangeOrdersFragment
import com.fota.android.moudles.market.MarketFavorListFragment
import com.fota.android.moudles.market.MarketIndexSpotListFragment
import com.fota.android.moudles.market.MarketListFragment
import com.fota.android.moudles.market.MarketPresenter
import java.util.*

class FragmentViewModel: BaseViewModel() {

    private var fragments: MutableList<Fragment>? = null

    private var mFragments: MutableList<MarketListFragment>? = null

    var present: MarketPresenter? = null


    fun getFragments():  MutableList<Fragment>{
        if (fragments == null) {
            fragments = mutableListOf(
                ExchangeOrdersFragment(),
                ExchangeCompleteFragment(),
                ExchangeMoneyListFragment()
            )
        }

        return fragments!!
    }

    fun getMFragments(): MutableList<MarketListFragment>{
        if (mFragments == null) {
            mFragments = mutableListOf(
                MarketFavorListFragment(),
                MarketListFragment(),
                MarketIndexSpotListFragment(),
                MarketIndexSpotListFragment()
            )
        }

        return mFragments!!
    }
}