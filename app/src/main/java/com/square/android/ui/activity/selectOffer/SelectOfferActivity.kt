package com.square.android.ui.activity.selectOffer

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.PlaceInfo
import com.square.android.data.pojo.RedemptionFull
import com.square.android.presentation.presenter.selectOffer.SelectOfferPresenter
import com.square.android.presentation.view.selectOffer.SelectOfferView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.review.EXTRA_OFFER_ID
import com.square.android.ui.activity.review.EXTRA_REDEMPTION_ID
import com.square.android.ui.activity.review.ReviewActivity
import com.square.android.ui.activity.review.ReviewExtras
import com.square.android.ui.base.tutorial.Tutorial
import com.square.android.ui.base.tutorial.TutorialService
import com.square.android.ui.base.tutorial.TutorialStep
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.activity_select_offer.*
import org.jetbrains.anko.intentFor
import ru.terrakok.cicerone.Navigator

const val OFFER_EXTRA_ID = "CLAIMED_OFFER_EXTRA_ID"

class SelectOfferActivity : BaseActivity(), SelectOfferView, SelectOfferAdapter.Handler {
    private var adapter: SelectOfferAdapter? = null

    private var dialog: SelectOfferDialog? = null

    private var currentId: Long? = null

    @InjectPresenter
    lateinit var presenter: SelectOfferPresenter

    @ProvidePresenter
    fun providePresenter() = SelectOfferPresenter(intent.getLongExtra(OFFER_EXTRA_ID,0))

    override fun provideNavigator(): Navigator = SelectOfferNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_offer)

        selectOfferList.setHasFixedSize(true)

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

    override fun showOfferDialog(offer: OfferInfo, place: PlaceInfo) {
        dialog = SelectOfferDialog(this)

        currentId = offer.id

        dialog!!.show(offer, place) {
            presenter.dialogSubmitClicked(offer.id)
        }
    }

    override fun showData(data: List<OfferInfo>, redemptionFull: RedemptionFull?) {
        adapter = SelectOfferAdapter(data, this, redemptionFull)

        configureStep(1)
        selectOffersStepsLl.visibility = View.VISIBLE

        selectOfferList.adapter = adapter
        selectOfferList.addItemDecoration(MarginItemDecorator( selectOfferList.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(),true,
                selectOfferList.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(),
                selectOfferList.context.resources.getDimension(R.dimen.rv_item_decorator_16).toInt()
        ))

    }

    override fun hideProgress() {
        selectOfferProgress.visibility = View.INVISIBLE
        selectOfferList.visibility = View.VISIBLE
    }

    override fun itemClicked(position: Int) {
        presenter.itemClicked(position)
    }

    override fun setSelectedItem(position: Int) {
        adapter?.setSelectedItem(position)

        presenter.submitClicked()
    }

    override fun showProgress() {
        selectOfferProgress.visibility = View.VISIBLE
        selectOfferList.visibility = View.INVISIBLE
    }

    //TODO do fragments swap instead of launching activities -> currently working on OffersListPresenter and OffersListFragment
    private class SelectOfferNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.selectOfferContainer) {
        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.REVIEW -> {
                        val extras = data as ReviewExtras
                        context.intentFor<ReviewActivity>(EXTRA_OFFER_ID to extras.offerId,
                                EXTRA_REDEMPTION_ID to extras.redemptionId)
                    }
                    else -> null
                }

        override fun createFragment(screenKey: String, data: Any?): Fragment? = null
    }

    override val PERMISSION_REQUEST_CODE: Int?
        get() = 1341

    override val tutorial: Tutorial?
        get() =  Tutorial.Builder(tutorialKey = TutorialService.TutorialKey.SELECT_OFFER)
                .addNextStep(TutorialStep(
                        // width percentage, height percentage for text with arrow
                        floatArrayOf(0.35f, 0.50f),
                        getString(R.string.tut_4_1),
                        TutorialStep.ArrowPos.TOP,
                        R.drawable.arrow_bottom_left_x_top_right,
                        0.60f,
                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                        // 0f,0f,0f,0f for covering entire screen
                        floatArrayOf(0f,0f,0.15f,312f),
                        1,
                        // delay before showing view in ms
                        500f))
                .addNextStep(TutorialStep(
                        // width percentage, height percentage for text with arrow
                        floatArrayOf(0.35f, 0.50f),
                        "",
                        TutorialStep.ArrowPos.TOP,
                        R.drawable.arrow_bottom_left_x_top_right,
                        0.60f,
                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                        // 0f,0f,0f,0f for covering entire screen
                        floatArrayOf(0f,0f,0.0f,0f),
                        0,
                        // delay before showing view in ms
                        500f))

                .setOnNextStepIsChangingListener {
                    if(it == 2){
                        presenter.itemClicked(0)
                    }
                }
                .setOnContinueTutorialListener {
                    currentId?.run {presenter.dialogSubmitClicked(this)}
                }
                .build()

}
