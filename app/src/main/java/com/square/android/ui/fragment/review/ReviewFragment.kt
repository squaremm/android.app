package com.square.android.ui.fragment.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.*
import com.square.android.presentation.presenter.review.ReviewPresenter
import com.square.android.presentation.view.review.ReviewView
import com.square.android.ui.activity.selectOffer.SelectOfferActivity
import com.square.android.ui.base.tutorial.Tutorial
import com.square.android.ui.base.tutorial.TutorialService
import com.square.android.ui.base.tutorial.TutorialStep
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_review.*
import org.jetbrains.anko.bundleOf

const val EXTRA_OFFER_ID = "EXTRA_OFFER_ID"
const val EXTRA_REDEMPTION_ID = "EXTRA_REDEMPTION_ID"

class ReviewExtras(val redemptionId: Long, val offerId: Long)

class ReviewFragment : BaseFragment(), ReviewView, ReviewAdapter.Handler, ReviewDialog.Handler{

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(redemptionId: Long, offerId: Long): ReviewFragment {
            val fragment = ReviewFragment()

            val args = bundleOf(EXTRA_REDEMPTION_ID to redemptionId, EXTRA_OFFER_ID to offerId)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ReviewPresenter

    @ProvidePresenter
    fun providePresenter(): ReviewPresenter = ReviewPresenter(getOfferId(), getRedemptionId())

    private var loadingDialog: LoadingDialog? = null

    private var adapter: ReviewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SelectOfferActivity).configureStep(3)

        reviewSend.setOnClickListener { presenter.submitClicked() }

        reviewSkip.setOnClickListener {
            presenter.skipClicked()
        }

        loadingDialog = LoadingDialog(activity!!)
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun showCongratulations() {
        CongratulationsDialog(activity!!).show {
            presenter.finishChain()
        }
    }

    override fun showButton() {
        reviewSend.visibility = View.VISIBLE
    }

    override fun hideButton() {
        reviewSend.visibility = View.GONE
    }

    override fun showData(data: Offer, actions: List<Offer.Action>) {
        adapter = ReviewAdapter(actions,this)
        reviewList.layoutManager = LinearLayoutManager(reviewList.context, RecyclerView.VERTICAL, false)
        reviewList.addItemDecoration(MarginItemDecorator(reviewList.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), vertical = true))
        reviewList.adapter = adapter

        visibleNow()
    }

    override fun showProgress() {
        reviewProgress.visibility = View.VISIBLE
        reviewList.visibility = View.INVISIBLE
    }

    override fun hideProgress() {
        reviewProgress.visibility = View.INVISIBLE
        reviewList.visibility = View.VISIBLE
    }

    override fun changeSelection(position: Int) {
        adapter?.changeSelection(position)
    }

    override fun itemClicked(position: Int) {
        presenter.itemClicked(position)
    }

    override fun showDialog(index: Int, action: Offer.Action, subActions: List<Offer.Action>, instaName: String, fbName: String) {
        val dialog = ReviewDialog(index, action, subActions, instaName, fbName, this, false, false)
        dialog.show(fragmentManager, "")
    }

    override fun showDeleteDialog(index: Int) {
        val dialog = MaterialDialog.Builder(activity!!)
                .cancelable(true)
                .title(R.string.action_delete_label)
                .positiveText(R.string.yes)
                .positiveColor(ContextCompat.getColor(activity!!, R.color.nice_pink))
                .negativeText(R.string.no)
                .negativeColor(ContextCompat.getColor(activity!!, R.color.secondary_text))
                .onPositive { dialog, which -> presenter.deleteFromFilled(index) }
                .onNegative { dialog, which -> dialog.dismiss() }
                .build()

        dialog.show()
    }

//    override fun sendClicked(index: Int, photo: ByteArray) {
//       presenter.addAction(index, photo)
//    }

    override fun sendClicked(index: Int, photo: ByteArray, actionType: String) {
        presenter.addAction(index, photo, actionType)
    }

    private fun getRedemptionId() = arguments?.getLong(EXTRA_REDEMPTION_ID, 0) ?: 0
    private fun getOfferId() = arguments?.getLong(EXTRA_OFFER_ID, 0) ?: 0

    override val PERMISSION_REQUEST_CODE: Int?
        get() = 1343

    override val tutorial: Tutorial?
        get() =  Tutorial.Builder(tutorialKey = TutorialService.TutorialKey.REVIEW)
                .addNextStep(TutorialStep(
                        // width percentage, height percentage for text with arrow
                        floatArrayOf(0.50f, 0.78f),
                        getString(R.string.tut_5_1),
                        TutorialStep.ArrowPos.TOP,
                        R.drawable.arrow_bottom_right_x_top_left,
                        0.35f,
                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                        // 0f,0f,0f,0f for covering entire screen
                        floatArrayOf(0f,0f,0.30f,500f),
                        1,
                        // delay before showing view in ms
                        500f))

                .setOnNextStepIsChangingListener {

                }
                .setOnContinueTutorialListener {

                }
                .build()

}
