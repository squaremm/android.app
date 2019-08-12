package com.square.android.ui.fragment.claimedActions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.square.android.R
import com.square.android.presentation.view.claimedActions.ClaimedActionsView
import com.square.android.presentation.presenter.claimedActions.ClaimedActionsPresenter
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.data.pojo.*
import com.square.android.extensions.copyToClipboard
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.review.*
import kotlinx.android.synthetic.main.fragment_claimed_actions.*

class ClaimedActionsFragment : BaseFragment(), ClaimedActionsView, ReviewAdapter.Handler  {

    @InjectPresenter
    lateinit var presenter: ClaimedActionsPresenter

    @ProvidePresenter
    fun providePresenter() = ClaimedActionsPresenter()

    private lateinit var reviewTypes: List<ReviewType>

    private var filteredTypes: List<ReviewType>? = null

    private var adapter: ReviewAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_claimed_actions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionsList.setHasFixedSize(true)
    }

    override fun showData(actionTypes: Set<String>, credits: Map<String, Int>,feedback: String, instaUser: String) {
        updateReviewTypes(feedback, instaUser)

        filteredTypes = reviewTypes.filter { it.key in actionTypes }

        adapter = ReviewAdapter(filteredTypes!!, credits, this)

        actionsList.adapter = adapter
    }

    override fun clearSelectedItem() {
        adapter?.clearSelection()
    }

    override fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
    }

    override fun disableItem(position: Int) {
        adapter?.disableReviewType(position)
    }

    override fun copyFeedbackToClipboard(feedback: String) {
        context?.copyToClipboard(feedback)
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

        context?.let {
            ReviewDialog(it)
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
    }

    private fun processRate(rating: Int) {
        if (rating == RATING_VALUE_UNDEFINED) return

        presenter.ratingUpdated(rating)
    }

    private fun updateReviewTypes(feedback: String, instaUser: String) {
        val feedbackDescription = getString(R.string.review_stage_2)

        reviewTypes.forEach {
            it.enabled = true

            if (it.stages.size > STAGE_COPY) {
                it.stages[STAGE_COPY].content = feedbackDescription
            }

            if (it.key == TYPE_INSTAGRAM_STORY) {
                it.stages[STAGE_RATE].content = getString(R.string.review_instagram_story_body, instaUser)
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
                                ReviewType.Stage(
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
                                ReviewType.Stage(
                                        subtitleRes = R.string.review_subtitle,
                                        content = getContentFor(R.string.type_tripavisor),
                                        ratingNeeded = true,
                                        buttonText = R.string.next_step
                                ),

                                ReviewType.Stage(
                                        subtitleRes = null,
                                        buttonText = R.string.copy_review
                                ),

                                ReviewType.Stage(
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
                                ReviewType.Stage(
                                        subtitleRes = R.string.review_subtitle,
                                        content = getContentFor(R.string.type_google_places),
                                        ratingNeeded = true,
                                        buttonText = R.string.next_step
                                ),

                                ReviewType.Stage(
                                        subtitleRes = null,
                                        buttonText = R.string.copy_review
                                ),

                                ReviewType.Stage(
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
                                ReviewType.Stage(
                                        subtitleRes = R.string.review_subtitle,
                                        content = getContentFor(R.string.type_facebook),
                                        ratingNeeded = true,
                                        buttonText = R.string.next_step
                                ),

                                ReviewType.Stage(
                                        subtitleRes = null,
                                        buttonText = R.string.copy_review
                                ),

                                ReviewType.Stage(
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
                                ReviewType.Stage(
                                        subtitleRes = R.string.review_subtitle,
                                        content = getContentFor(R.string.type_yelp),
                                        ratingNeeded = true,
                                        buttonText = R.string.next_step
                                ),

                                ReviewType.Stage(
                                        subtitleRes = null,
                                        buttonText = R.string.copy_review
                                ),

                                ReviewType.Stage(
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

//    private fun initializeReviewTypes() {
//        reviewTypes = listOf(
//                /*
//                 ReviewType(
//                         imageRes = R.drawable.instagram_logo,
//                         titleRes = R.string.insta_post,
//                         descriptionRes = R.string.insta_post_description,
//                         key = TYPE_INSTAGRAM_POST
//                 ),*/
//
//                ReviewType(
//                        imageRes = R.drawable.instagram_logo,
//                        titleRes = R.string.insta_story,
//                        descriptionRes = R.string.insta_story_description,
//                        key = TYPE_INSTAGRAM_STORY),
//
//                ReviewType(
//                        imageRes = R.drawable.trip_advisor_logo,
//                        titleRes = R.string.trip_advisor,
//                        descriptionRes = R.string.trip_advisor_description,
//                        key = TYPE_TRIP_ADVISOR),
//
//                ReviewType(
//                        imageRes = R.drawable.google_logo,
//                        titleRes = R.string.google_places,
//                        descriptionRes = R.string.google_places_description,
//                        key = TYPE_GOOGLE_PLACES),
//
//                ReviewType(
//                        imageRes = R.drawable.facebook_logo,
//                        titleRes = R.string.facebook_post,
//                        descriptionRes = R.string.facebook_post_description,
//                        key = TYPE_FACEBOOK_POST),
//
//                ReviewType(
//                        imageRes = R.drawable.yelp_logo,
//                        titleRes = R.string.yelp_post,
//                        descriptionRes = R.string.yelp_post_description,
//                        key = TYPE_YELP)
//        )
//    }
}
