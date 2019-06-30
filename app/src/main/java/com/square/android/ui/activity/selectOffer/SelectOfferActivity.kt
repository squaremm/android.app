package com.square.android.ui.activity.selectOffer

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
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
import com.square.android.ui.activity.review.ReviewExtras
import com.square.android.ui.fragment.checkIn.CheckInFragment
import com.square.android.ui.fragment.offersList.OffersListFragment
import kotlinx.android.synthetic.main.activity_select_offer.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

const val OFFER_EXTRA_ID = "CLAIMED_OFFER_EXTRA_ID"

class SelectOfferActivity : BaseActivity(), SelectOfferView {

    @InjectPresenter
    lateinit var presenter: SelectOfferPresenter

    @ProvidePresenter
    fun providePresenter() = SelectOfferPresenter(intent.getLongExtra(OFFER_EXTRA_ID,0))

    override fun provideNavigator(): Navigator = SelectOfferNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_offer)
    }

    fun configureStep(step: Int){

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
                selectOfferStep1.setBackgroundResource(R.drawable.round_background)
                selectOfferStep1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(selectOfferStep1.context, R.color.gray_divider))

                selectOfferStep2.setBackgroundResource(R.drawable.round_bg_stroke_pink)
                selectOfferStep2.setBackgroundResource(R.drawable.round_bg_stroke_gray)

                selectOfferStep1.text = "1."
                selectOfferStep2.text = "2."
                selectOfferStep3.text = ""

                selectOfferTitle.text = getString(R.string.select_offer_title)
                selectOfferHoursLl.visibility = View.GONE
            }

            3 -> {
                selectOfferStep1.setBackgroundResource(R.drawable.round_background)
                selectOfferStep1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(selectOfferStep1.context, R.color.gray_divider))

                selectOfferStep2.setBackgroundResource(R.drawable.round_background)
                selectOfferStep2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(selectOfferStep2.context, R.color.gray_divider))

                selectOfferStep3.setBackgroundResource(R.drawable.round_bg_stroke_pink)

                selectOfferStep1.text = "1."
                selectOfferStep2.text = "2."
                selectOfferStep3.text = "3."

                selectOfferTitle.text = getString(R.string.review_title)
                selectOfferHoursLl.visibility = View.GONE
            }
        }

    }

    fun navigate(stepNo: Int, data: Any? = null){
        when(stepNo){
            2 -> presenter.navigate(SCREENS.CHECK_IN, data)
            3 ->  presenter.navigate(SCREENS.REVIEW, data)
        }
    }

    fun setHours(hours: String){
        selectOfferTime.text = hours
    }

    private class SelectOfferNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.selectOfferContainer) {

        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
        throw IllegalArgumentException("No navigation from here")

        //TODO currently working on fragment_check_in, CheckInFragment and CheckInPresenter
        override fun createFragment(screenKey: String, data: Any?) = when (screenKey) {
            SCREENS.OFFERS_LIST -> OffersListFragment.newInstance(data as Long)

            SCREENS.CHECK_IN -> {
                val extras = data as ReviewExtras
                CheckInFragment.newInstance(extras.redemptionId, extras.offerId) }

            //TODO ReviewFragment
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
