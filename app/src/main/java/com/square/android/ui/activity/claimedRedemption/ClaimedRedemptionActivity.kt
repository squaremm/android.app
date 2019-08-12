package com.square.android.ui.activity.claimedRedemption

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.presentation.presenter.claimedRedemption.ClaimedRedemptionPresenter
import com.square.android.presentation.view.claimedRedemption.ClaimedRedemptionView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.sendPicture.INDEX_EXTRA
import com.square.android.ui.activity.sendPicture.SendPictureActivity
import com.square.android.ui.activity.uploadScreenshot.UploadScreenshotActivity
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.activity_claimed_redemption.*
import org.jetbrains.anko.intentFor
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

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

        //(claimedRedemptionPager.adapter?.instantiateItem(claimedRedemptionPager, 0) as BaseFragment).visibleNow()

        claimedRedemptionPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) { }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                (claimedRedemptionPager.adapter?.instantiateItem(claimedRedemptionPager, position) as BaseFragment).visibleNow()
            }

        })
    }

    private class ClaimedRedemptionNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.selectOfferContainer) {

        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.SEND_PICTURE -> context.intentFor<SendPictureActivity>(INDEX_EXTRA to data as Int)
                    SCREENS.UPLOAD_SCREENSHOT -> context.intentFor<UploadScreenshotActivity>(INDEX_EXTRA to data as Int)
                    else -> null
                }

        override fun createFragment(screenKey: String, data: Any?): Fragment {
            throw UnsupportedOperationException("No forward navigation here")
        }

        override fun setupFragmentTransactionAnimation(command: Command,
                                                       currentFragment: Fragment?,
                                                       nextFragment: Fragment,
                                                       fragmentTransaction: FragmentTransaction) {

            if(command is Forward){
                fragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right)
            } else{
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out)
            }

        }
    }

    private fun getOfferId() = intent.getLongExtra(CLAIMED_OFFER_EXTRA_ID, 0)

    private fun getRedemptionId() = intent.getLongExtra(CLAIMED_REDEMPTION_EXTRA_ID, 0)
}
