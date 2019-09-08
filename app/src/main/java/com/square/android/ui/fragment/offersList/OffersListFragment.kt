package com.square.android.ui.fragment.offersList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.RedemptionFull
import com.square.android.presentation.presenter.offersList.OffersListPresenter
import com.square.android.presentation.view.offersList.OffersListView
import com.square.android.ui.activity.selectOffer.*
import com.square.android.ui.base.tutorial.Tutorial
import com.square.android.ui.base.tutorial.TutorialService
import com.square.android.ui.base.tutorial.TutorialStep
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_offers_list.*
import org.jetbrains.anko.bundleOf

class OffersListFragment: BaseFragment(), OffersListView, OffersListAdapter.Handler {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(offerId: Long): OffersListFragment {
            val fragment = OffersListFragment()

            val args = bundleOf(OFFER_EXTRA_ID to offerId)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: OffersListPresenter

    @ProvidePresenter
    fun providePresenter(): OffersListPresenter = OffersListPresenter( arguments?.getLong(OFFER_EXTRA_ID,0) ?: 0)

    private var adapter: OffersListAdapter? = null

    private var dialog: SelectOfferDialog? = null

    private var currentId: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_offers_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SelectOfferActivity).configureStep(1)
    }

    override fun showProgress() {
        offersListRv.visibility = View.GONE
        offersListProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        offersListProgress.visibility = View.GONE
        offersListRv.visibility = View.VISIBLE
    }

    override fun showOfferDialog(offer: OfferInfo) {
        currentId = offer.id

        dialog = SelectOfferDialog(activity!!)

        dialog!!.show(offer) { presenter.dialogSubmitClicked(offer.id) }
    }

    override fun showData(data: List<OfferInfo>, redemptionFull: RedemptionFull) {
        adapter = OffersListAdapter(data, this, redemptionFull)

        offersListRv.adapter = adapter
        offersListRv.addItemDecoration(MarginItemDecorator( offersListRv.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(),true,
                offersListRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(),
                offersListRv.context.resources.getDimension(R.dimen.rv_item_decorator_16).toInt()
        ))

        (activity as SelectOfferActivity).setHours(getString(R.string.time_range, redemptionFull.redemption.startTime, redemptionFull.redemption.endTime))

        visibleNow()
    }

    override fun setSelectedItem(position: Int) {
        adapter?.setSelectedItem(position)

        presenter.submitClicked()
    }

    override fun itemClicked(position: Int) {
        presenter.dialogAllowed = true

        presenter.itemClicked(position)
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
                    dialog?.cancel()
                    currentId?.run {presenter.dialogSubmitClicked(this)}
                }
                .build()
}
