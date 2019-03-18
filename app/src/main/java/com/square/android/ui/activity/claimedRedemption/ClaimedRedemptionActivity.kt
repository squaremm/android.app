package com.square.android.ui.activity.claimedRedemption


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.presentation.presenter.claimedRedemption.ClaimedRedemptionPresenter
import com.square.android.presentation.view.claimedRedemption.ClaimedRedemptionView
import com.square.android.ui.activity.BaseActivity
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


    override fun provideNavigator(): Navigator = ClaimedRedemptionNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_claimed_redemption)

        initializePager()

        claimedRedemptionBack.setOnClickListener { presenter.exit() }
    }

    private fun initializePager() {
        val adapter = ClaimedRedemptionAdapter(supportFragmentManager)

        claimedRedemptionPager.adapter = adapter

        claimedRedemptionTab.setupWithViewPager(claimedRedemptionPager)
    }

    private class ClaimedRedemptionNavigator(activity: FragmentActivity) : AppNavigator(activity, 0) {
        override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            throw UnsupportedOperationException("No forward navigation here")
        }

        override fun createFragment(screenKey: String, data: Any?): Fragment {
            throw UnsupportedOperationException("No forward navigation here")
        }

    }

    private fun getOfferId() = intent.getLongExtra(CLAIMED_OFFER_EXTRA_ID, 0)

    private fun getRedemptionId() = intent.getLongExtra(CLAIMED_REDEMPTION_EXTRA_ID, 0)
}
