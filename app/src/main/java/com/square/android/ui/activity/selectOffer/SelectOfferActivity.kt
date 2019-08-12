package com.square.android.ui.activity.selectOffer

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.presentation.presenter.selectOffer.SelectOfferPresenter
import com.square.android.presentation.view.selectOffer.SelectOfferView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.sendPicture.INDEX_EXTRA
import com.square.android.ui.activity.sendPicture.SendPictureActivity
import com.square.android.ui.activity.uploadScreenshot.UploadScreenshotActivity
import com.square.android.ui.fragment.review.ReviewExtras
import com.square.android.ui.fragment.checkIn.CheckInFragment
import com.square.android.ui.fragment.offersList.OffersListFragment
import com.square.android.ui.fragment.review.ReviewFragment
import kotlinx.android.synthetic.main.activity_select_offer.*
import org.jetbrains.anko.intentFor
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

const val OFFER_EXTRA_ID = "CLAIMED_OFFER_EXTRA_ID"

class SelectOfferActivity: BaseActivity(), SelectOfferView {

    @InjectPresenter
    lateinit var presenter: SelectOfferPresenter

    @ProvidePresenter
    fun providePresenter() = SelectOfferPresenter(intent.getLongExtra(OFFER_EXTRA_ID,0))

    private var currentStep = 1

    override fun provideNavigator(): Navigator = SelectOfferNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_offer)
    }

    fun configureStep(step: Int){
        currentStep = step

        when(step){
            1 -> {
                selectOfferStep1.setBackgroundResource(R.drawable.round_bg_stroke_pink)
                selectOfferStep2.setBackgroundResource(R.drawable.round_bg_stroke_gray)
                selectOfferStep2.setBackgroundResource(R.drawable.round_bg_stroke_gray)

                selectOfferStep1.text = "1."
                selectOfferStep2.text = ""
                selectOfferStep3.text = ""

                selectOfferTitle.text = getString(R.string.select_offer_title)
                selectOfferHoursLl.visibility = View.VISIBLE
            }

            2 -> {
                selectOfferStep1.setBackgroundResource(R.drawable.round_bg_grey_divider)
                selectOfferStep2.setBackgroundResource(R.drawable.round_bg_stroke_pink)
                selectOfferStep3.setBackgroundResource(R.drawable.round_bg_stroke_gray)

                selectOfferStep1.text = "1."
                selectOfferStep2.text = "2."
                selectOfferStep3.text = ""

                selectOfferTitle.text = getString(R.string.select_offer_title)
                selectOfferHoursLl.visibility = View.GONE
            }

            3 -> {
                selectOfferStep1.setBackgroundResource(R.drawable.round_bg_grey_divider)
                selectOfferStep2.setBackgroundResource(R.drawable.round_bg_grey_divider)
                selectOfferStep3.setBackgroundResource(R.drawable.round_bg_stroke_pink)

                selectOfferStep1.text = "1."
                selectOfferStep2.text = "2."
                selectOfferStep3.text = "3."

                selectOfferTitle.text = getString(R.string.review_title)
                selectOfferHoursLl.visibility = View.GONE
            }
        }
    }

    fun setHours(hours: String){
        selectOfferTime.text = hours
    }

    private class SelectOfferNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.selectOfferContainer) {

        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.SEND_PICTURE -> context.intentFor<SendPictureActivity>(INDEX_EXTRA to data as Int)
                    SCREENS.UPLOAD_SCREENSHOT -> context.intentFor<UploadScreenshotActivity>(INDEX_EXTRA to data as Int)
                    else -> null
                }

        override fun createFragment(screenKey: String, data: Any?) = when (screenKey) {
            SCREENS.OFFERS_LIST -> OffersListFragment.newInstance(data as Long)

            SCREENS.CHECK_IN -> {
                val extras = data as ReviewExtras
                CheckInFragment.newInstance(extras.redemptionId, extras.offerId) }

            SCREENS.REVIEW -> {
                val extras = data as ReviewExtras
                ReviewFragment.newInstance(extras.redemptionId, extras.offerId)
            }

            else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
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

}
