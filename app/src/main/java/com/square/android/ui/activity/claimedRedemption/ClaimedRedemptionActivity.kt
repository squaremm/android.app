package com.square.android.ui.activity.claimedRedemption

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.claimedRedemption.ClaimedRedemptionPresenter
import com.square.android.presentation.view.claimedRedemption.ClaimedRedemptionView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.activity_claimed_redemption.*
import ru.terrakok.cicerone.Navigator

const val CLAIMED_OFFER_EXTRA_ID = "EXTRA_ID_OFFER"
const val CLAIMED_REDEMPTION_EXTRA_ID = "EXTRA_ID_REDEMPTION"

class ClaimedExtras(val offerId: Long, val redemptionId: Long)

class ClaimedRedemptionActivity : BaseActivity(), ClaimedRedemptionView {
    @InjectPresenter
    lateinit var presenter: ClaimedRedemptionPresenter

    @ProvidePresenter
    fun providePresenter() = ClaimedRedemptionPresenter(getOfferId(), getRedemptionId())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_claimed_redemption)

        initializePager()

        claimedRedemptionBack.setOnClickListener { onBackPressed() }
    }

    private fun initializePager() {
        val adapter = ClaimedRedemptionAdapter(supportFragmentManager)

        claimedRedemptionPager.adapter = adapter

        claimedRedemptionTab.setupWithViewPager(claimedRedemptionPager)

        //(claimedRedemptionPager.adapter?.instantiateItem(claimedRedemptionPager, 0) as BaseFragment).visibleNow()

        claimedRedemptionPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) { }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                (claimedRedemptionPager.adapter?.instantiateItem(claimedRedemptionPager, position) as BaseFragment).visibleNow()
            }

        })
    }

    private fun getOfferId() = intent.getLongExtra(CLAIMED_OFFER_EXTRA_ID, 0)

    private fun getRedemptionId() = intent.getLongExtra(CLAIMED_REDEMPTION_EXTRA_ID, 0)
}
