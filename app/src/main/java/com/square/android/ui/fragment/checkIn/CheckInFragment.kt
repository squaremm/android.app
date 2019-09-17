package com.square.android.ui.fragment.checkIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionFull
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.checkIn.CheckInPresenter
import com.square.android.presentation.view.checkIn.CheckInView
import com.square.android.ui.fragment.review.EXTRA_OFFER_ID
import com.square.android.ui.fragment.review.EXTRA_REDEMPTION_ID
import com.square.android.ui.activity.selectOffer.SelectOfferActivity
import com.square.android.ui.base.tutorial.Tutorial
import com.square.android.ui.base.tutorial.TutorialService
import com.square.android.ui.base.tutorial.TutorialStep
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_check_in.*
import org.jetbrains.anko.bundleOf

class CheckInFragment: BaseFragment(), CheckInView{

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(redemptionId: Long, offerId: Long): CheckInFragment {
            val fragment = CheckInFragment()

            val args = bundleOf(EXTRA_REDEMPTION_ID to redemptionId, EXTRA_OFFER_ID to offerId)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: CheckInPresenter

    @ProvidePresenter
    fun providePresenter(): CheckInPresenter = CheckInPresenter(getRedemptionId(), getOfferId())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_check_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SelectOfferActivity).configureStep(2)

        couponCheckIn.setOnClickListener {presenter.checkInClicked()}

        couponSv.setOnTouchListener { v, event ->
            couponSv.parent.requestDisallowInterceptTouchEvent(true)

            false
        }
    }

    override fun showData(offer: Offer, user: Profile.User, place: Place, redemptionFull: RedemptionFull) {
        couponImage.loadImage(offer.photo)

        user.mainImage?.let {
            couponAvatar.loadImage(it)
        }

        couponPersonName.text = user.name
        couponComponents.text = offer.compositionAsString()

        couponPlaceName.text = place.name
        couponPlaceAddress.text = place.address
        couponPlaceDate.text = "${redemptionFull.redemption.date} / ${getString(R.string.time_range, redemptionFull.redemption.startTime, redemptionFull.redemption.endTime)}"

        visibleNow()
    }

    override fun hideProgress() {
        couponProgress.visibility = View.GONE

        couponPersonName.visibility = View.VISIBLE
        separator.visibility = View.VISIBLE
        couponSv.visibility = View.VISIBLE
        separator2.visibility = View.VISIBLE
        couponPlaceLl.visibility = View.VISIBLE
        couponCheckIn.visibility = View.VISIBLE
    }

    private fun getRedemptionId() = arguments?.getLong(EXTRA_REDEMPTION_ID, 0) ?: 0
    private fun getOfferId() = arguments?.getLong(EXTRA_OFFER_ID, 0) ?: 0

    override val PERMISSION_REQUEST_CODE: Int?
        get() = 1342

    override val tutorial: Tutorial?
        get() =  Tutorial.Builder(tutorialKey = TutorialService.TutorialKey.CHECK_IN)
                .addNextStep(TutorialStep(
                        // width percentage, height percentage for text with arrow
                        floatArrayOf(0.35f, 0.50f),
                        "",
                        TutorialStep.ArrowPos.TOP,
                        R.drawable.arrow_bottom_left_x_top_right,
                        0.60f,
                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                        // 0f,0f,0f,0f for covering entire screen
                        floatArrayOf(0f,0f,0f,0f),
                        0,
                        // delay before showing view in ms
                        500f,
                        0))
                .setOnNextStepIsChangingListener {
                }
                .setOnContinueTutorialListener {
                    presenter.checkInClicked()
                }
                .build()

}
