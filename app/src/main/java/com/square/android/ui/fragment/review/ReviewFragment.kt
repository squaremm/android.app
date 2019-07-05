package com.square.android.ui.fragment.review

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.*
import com.square.android.data.pojo.ReviewType.Stage
import com.square.android.extensions.copyToClipboard
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
    fun providePresenter(): ReviewPresenter = ReviewPresenter(getRedemptionId(), getOfferId())

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

        reviewSkip.setOnClickListener {presenter.goToMain()}
    }

    override fun showCongratulations() {
        CongratulationsDialog(activity!!).show {
            presenter.goToMain()
        }
    }

    override fun clearSelectedItem() {
        adapter?.clearSelection()
    }

    override fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
    }

    override fun showButtons() {
        reviewSubmit.visibility = View.VISIBLE
        reviewSpacing.visibility = View.VISIBLE
        reviewSkip.visibility = View.VISIBLE
    }

    override fun disableItem(position: Int) {
        adapter?.disableReviewType(position)
    }

    override fun showData(data: Offer, feedback: String) {
        updateReviewTypes(feedback, data)

        val used = data.posts.map { it.type }

        filteredTypes = reviewTypes.filter { it.key in data.credits && (it.key !in used || it.key == TYPE_INSTAGRAM_STORY) }

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

    override fun copyFeedbackToClipboard(feedback: String) {
        activity?.copyToClipboard(feedback)
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

    override fun showDialog(type: String, coins: Int, feedback: String) {
        val index = filteredTypes!!.indexOfFirst { it.key == type }
        val reviewType = filteredTypes!![index]

        ReviewDialog(activity!!, presenter.reviewInfo.feedback)
                .show(reviewType, coins) { stageResult ->
                    when (stageResult.stage) {
                        STAGE_RATE -> processRate(stageResult.rating)
                        STAGE_COPY -> presenter.copyClicked()
                        STAGE_OPEN -> {
                            if (!stageResult.doneClicked) {
                                presenter.openLinkClicked(type)
                            }
                        }
                    }

                    if (stageResult.isLast) presenter.lastStageReached(index)
                }
    }

    private fun processRate(rating: Int) {
        if (rating == RATING_VALUE_UNDEFINED) return

        presenter.ratingUpdated(rating)
    }

    private fun updateReviewTypes(feedback: String, data: Offer) {
        val feedbackDescription = getString(R.string.review_stage_2)

        reviewTypes.forEach {
            it.enabled = true

            if (it.stages.size > STAGE_COPY) {
                it.stages[STAGE_COPY].content = feedbackDescription
            }

            if (it.key == TYPE_INSTAGRAM_STORY) {
                it.stages[STAGE_RATE].content = getString(R.string.review_instagram_story_body, data.instaUser)
            }
        }
    }

    private fun getContentFor(@StringRes typeStringRes: Int): String {
        val type = getString(typeStringRes)
        return getString(R.string.review_other_body_format)
    }

    private fun getRedemptionId() = arguments?.getLong(EXTRA_REDEMPTION_ID, 0) ?: 0
    private fun getOfferId() = arguments?.getLong(EXTRA_OFFER_ID, 0) ?: 0

    override fun initReviewTypes() {
        reviewTypes = listOf(
                /* TODO uncomment
                 ReviewType(
                         imageRes = R.drawable.instagram_logo,
                         titleRes = R.string.insta_post,
                         descriptionRes = R.string.insta_post_description,
                         key = TYPE_INSTAGRAM_POST,

                         stages = listOf(
                                 Stage(
                                         subtitleRes = R.string.review_instagram_post_title,
                                         content = getString(R.string.review_instagram_post_body),
                                         buttonText = R.string.ok
                                 )
                         )
                 ),*/

                ReviewType(
                        //TODO change icon (imageRes)
                        imageRes = R.drawable.add_photo,
                        titleRes = R.string.send_picture,
                        descriptionRes = R.string.send_photo_description,
                        key = TYPE_PICTURE,
                        stages = listOf()
                ),

                ReviewType(
                        imageRes = R.drawable.instagram_logo,
                        titleRes = R.string.insta_story,
                        descriptionRes = R.string.insta_story_description,
                        key = TYPE_INSTAGRAM_STORY,

                        stages = listOf(
                                Stage(
                                        subtitleRes = R.string.review_instagram_story_title,
                                        buttonText = R.string.ok
                                )
                        )
                ),

                ReviewType(
                        imageRes = R.drawable.trip_advisor_logo,
                        titleRes = R.string.trip_advisor,
                        descriptionRes = R.string.trip_advisor_description,
                        key = TYPE_TRIP_ADVISOR,

                        stages = listOf(
                                Stage(
                                        subtitleRes = R.string.review_subtitle,
                                        content = getContentFor(R.string.type_tripavisor),
                                        ratingNeeded = true,
                                        buttonText = R.string.next_step
                                ),

                                Stage(
                                        subtitleRes = null,
                                        buttonText = R.string.copy_review
                                ),

                                Stage(
                                        subtitleRes = null,
                                        content = getString(R.string.review_stage_3_tripadvisor),
                                        buttonText = R.string.open_restaurant,
                                        doneEnabled = true
                                )
                        )
                ),

                ReviewType(
                        imageRes = R.drawable.google_logo,
                        titleRes = R.string.google_places,
                        descriptionRes = R.string.google_places_description,
                        key = TYPE_GOOGLE_PLACES,

                        stages = listOf(
                                Stage(
                                        subtitleRes = R.string.review_subtitle,
                                        content = getContentFor(R.string.type_google_places),
                                        ratingNeeded = true,
                                        buttonText = R.string.next_step
                                ),

                                Stage(
                                        subtitleRes = null,
                                        buttonText = R.string.copy_review
                                ),

                                Stage(
                                        subtitleRes = null,
                                        content = getString(R.string.review_stage_3_maps),
                                        buttonText = R.string.open_restaurant,
                                        doneEnabled = true
                                )
                        )),

                ReviewType(
                        imageRes = R.drawable.facebook_logo,
                        titleRes = R.string.facebook_post,
                        descriptionRes = R.string.facebook_post_description,
                        key = TYPE_FACEBOOK_POST,

                        stages = listOf(
                                Stage(
                                        subtitleRes = R.string.review_subtitle,
                                        content = getContentFor(R.string.type_facebook),
                                        ratingNeeded = true,
                                        buttonText = R.string.next_step
                                ),

                                Stage(
                                        subtitleRes = null,
                                        buttonText = R.string.copy_review
                                ),

                                Stage(
                                        subtitleRes = null,
                                        content = getString(R.string.review_stage_3_facebook),
                                        buttonText = R.string.open_restaurant,
                                        doneEnabled = true
                                )
                        )),

                ReviewType(
                        imageRes = R.drawable.yelp_logo,
                        titleRes = R.string.yelp_post,
                        descriptionRes = R.string.yelp_post_description,
                        key = TYPE_YELP,

                        stages = listOf(
                                Stage(
                                        subtitleRes = R.string.review_subtitle,
                                        content = getContentFor(R.string.type_yelp),
                                        ratingNeeded = true,
                                        buttonText = R.string.next_step
                                ),

                                Stage(
                                        subtitleRes = null,
                                        buttonText = R.string.copy_review
                                ),

                                Stage(
                                        subtitleRes = null,
                                        content = getString(R.string.review_stage_3_yelp),
                                        buttonText = R.string.open_restaurant,
                                        doneEnabled = true
                                )
                        ))
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
