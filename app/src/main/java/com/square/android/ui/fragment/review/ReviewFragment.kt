package com.square.android.ui.fragment.review

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
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
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.places.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_review.*
import org.jetbrains.anko.bundleOf

const val EXTRA_OFFER_ID = "EXTRA_OFFER_ID"
const val EXTRA_REDEMPTION_ID = "EXTRA_REDEMPTION_ID"

const val STAGE_RATE = 0
const val STAGE_COPY = 1
const val STAGE_OPEN = 2

class ReviewExtras(val redemptionId: Long, val offerId: Long)

class ReviewFragment : BaseFragment(), ReviewView, ReviewAdapter.Handler {

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

    private lateinit var reviewTypes: List<ReviewType>

    private var filteredTypes: List<ReviewType>? = null

    private var adapter: ReviewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SelectOfferActivity).configureStep(3)

        reviewSubmit.setOnClickListener { presenter.submitClicked() }

        reviewSkip.setOnClickListener {presenter.finishChain()}
    }

    override fun showCongratulations() {
        CongratulationsDialog(activity!!).show {
            presenter.finishChain()
        }
    }

    override fun clearSelectedItem() {
        adapter?.clearSelection()
    }

    override fun showButtons() {
        reviewSubmit.visibility = View.VISIBLE
        reviewSpacing.visibility = View.VISIBLE
        reviewSkip.visibility = View.VISIBLE
    }

    override fun disableItem(position: Int) {
        adapter?.disableReviewType(position)
    }

    override fun showData(data: Offer, actionTypes: Set<String>, credits: Map<String, Int>) {
        val used = data.posts.map { it.type }
        Log.e("LOL", used.toString())
        Log.e("LOLEK", actionTypes.toString())

        filteredTypes = reviewTypes.filter { it.key in actionTypes && (it.key !in used || it.key == TYPE_INSTAGRAM_STORY) }

        adapter = ReviewAdapter(filteredTypes!!, data.credits, this)

        reviewList.layoutManager = GridLayoutManager(context, 2)
        reviewList.adapter = adapter
        reviewList.addItemDecoration(GridItemDecoration(2,reviewList.context.resources.getDimension(R.dimen.value_24dp).toInt(), false))

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

    override fun setSelectedItem(position: Int) {
        adapter?.setSelectedItem(position)
    }

    override fun itemClicked(position: Int) {
        val type = filteredTypes!![position]

        presenter.itemClicked(type.key, position)
    }

    override fun showDialog(type: String, coins: Int, index: Int) {
        val index = filteredTypes!!.indexOfFirst { it.key == type }
        val reviewType = filteredTypes!![index]

        ReviewDialog(activity!!)
                .show(reviewType, coins, index) { s: String, i: Int ->
                    presenter.navigateByKey(index = i, reviewType = s)
                }
    }

    private fun getRedemptionId() = arguments?.getLong(EXTRA_REDEMPTION_ID, 0) ?: 0
    private fun getOfferId() = arguments?.getLong(EXTRA_OFFER_ID, 0) ?: 0

    override fun initReviewTypes() {
        reviewTypes = listOf(

                ReviewType(
                        //TODO change icon (imageRes)
                        imageRes = R.drawable.add_photo,
                        title = getString(R.string.photo_uppercase),
                        description = getString(R.string.send_photo_description),
                        key = TYPE_PICTURE
                ),

                ReviewType(
                        imageRes = R.drawable.instagram_logo,
                        title = getString(R.string.insta_post),
                        description = getString(R.string.insta_post_description),
                        key = TYPE_INSTAGRAM_POST,
                        content = getString(R.string.review_instagram_post_body)
                ),

                ReviewType(
                        imageRes = R.drawable.instagram_logo,
                        title = getString(R.string.insta_story),
                        description = getString(R.string.insta_story_description),
                        key = TYPE_INSTAGRAM_STORY,
                        content = getString(R.string.review_instagram_story_body)
                ),

                ReviewType(
                        imageRes = R.drawable.trip_advisor_logo,
                        title = getString(R.string.trip_advisor),
                        key = TYPE_TRIP_ADVISOR,
                        app_name = getString(R.string.trip_advisor_name),
                        showUploadLabel = true
                ),

                ReviewType(
                        imageRes = R.drawable.google_logo,
                        title = getString(R.string.google_places),
                        key = TYPE_GOOGLE_PLACES,
                        app_name = getString(R.string.google_places_name),
                        showUploadLabel = true
                ),

                ReviewType(
                        imageRes = R.drawable.facebook_logo,
                        title = getString(R.string.facebook_post),
                        key = TYPE_FACEBOOK_POST,
                        app_name = getString(R.string.facebook_name),
                        showUploadLabel = true
                ),

                ReviewType(
                        imageRes = R.drawable.yelp_logo,
                        title = getString(R.string.yelp),
                        key = TYPE_YELP,
                        app_name = getString(R.string.yelp_name),
                        showUploadLabel = true
                )
        )

//        reviewTypes.filter { it.key }
    }

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
