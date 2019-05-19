package com.square.android.ui.activity.review


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.data.pojo.*
import com.square.android.data.pojo.ReviewType.Stage
import com.square.android.extensions.copyToClipboard
import com.square.android.presentation.presenter.review.ReviewPresenter
import com.square.android.presentation.view.review.ReviewView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.dialogs.ClaimedCouponDialog
import kotlinx.android.synthetic.main.activity_review.*
import ru.terrakok.cicerone.Navigator

const val EXTRA_OFFER_ID = "EXTRA_OFFER_ID"
const val EXTRA_REDEMPTION_ID = "EXTRA_REDEMPTION_ID"

const val STAGE_RATE = 0
const val STAGE_COPY = 1
const val STAGE_OPEN = 2

class ReviewExtras(val redemptionId: Long, val offerId: Long)

class ReviewActivity : BaseActivity(), ReviewView, ReviewAdapter.Handler {
    private lateinit var reviewTypes: List<ReviewType>

    private var filteredTypes: List<ReviewType>? = null

    private var adapter: ReviewAdapter? = null

    @InjectPresenter
    lateinit var presenter: ReviewPresenter

    @ProvidePresenter
    fun providePresenter() = ReviewPresenter(getOfferId(), getRedemptionId())

    override fun provideNavigator(): Navigator = ReviewNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        reviewBack.setOnClickListener { presenter.exit() }

        reviewList.setHasFixedSize(true)

        reviewSubmit.setOnClickListener { presenter.submitClicked() }

        reviewSkip.setOnClickListener { presenter.exit() }
    }

    override fun showCongratulations() {
        CongratulationsDialog(this).show {
            presenter.exit()
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
        reviewSkip.visibility = View.VISIBLE
    }

    override fun disableItem(position: Int) {
        adapter?.disableReviewType(position)
    }

    override fun showData(data: Offer, feedback: String, user: Profile.User, place: Place) {
        updateReviewTypes(feedback, data)

        val used = data.posts.map { it.type }

        filteredTypes = reviewTypes.filter { it.key in data.credits && it.key !in used }

        adapter = ReviewAdapter(filteredTypes!!, data.credits, this)

        reviewList.adapter = adapter

        ClaimedCouponDialog(this).show(data, place, user)
    }

    override fun showProgress() {
        reviewProgress.visibility = View.VISIBLE
        reviewList.visibility = View.INVISIBLE
    }

    override fun copyFeedbackToClipboard(feedback: String) {
        copyToClipboard(feedback)
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

        presenter.itemClicked(type.key)
    }

    override fun showDialog(type: String, coins: Int, feedback: String) {
        val index = filteredTypes!!.indexOfFirst { it.key == type }
        val reviewType = filteredTypes!![index]

        ReviewDialog(this, presenter.reviewInfo.feedback)
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

    private fun getContentFor(@StringRes typeStringRes: Int): String {
        val type = getString(typeStringRes)
        return getString(R.string.review_other_body_format)
    }

    private fun getOfferId() = intent.getLongExtra(EXTRA_OFFER_ID, 0)

    private fun getRedemptionId() = intent.getLongExtra(EXTRA_REDEMPTION_ID, 0)

    private class ReviewNavigator(activity: androidx.fragment.app.FragmentActivity) : AppNavigator(activity, R.id.review_container) {

        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
                throw IllegalArgumentException("No navigation from here")

        override fun createFragment(screenKey: String, data: Any?) =
                throw IllegalArgumentException("No navigation from here")

    }
}
