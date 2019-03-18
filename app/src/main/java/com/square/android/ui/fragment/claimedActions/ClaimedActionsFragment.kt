package com.square.android.ui.fragment.claimedActions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.square.android.R
import com.square.android.presentation.view.claimedActions.ClaimedActionsView
import com.square.android.presentation.presenter.claimedActions.ClaimedActionsPresenter

import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.data.pojo.*
import com.square.android.ui.activity.review.ReviewAdapter

import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_claimed_actions.*

class ClaimedActionsFragment : BaseFragment(), ClaimedActionsView {
    @InjectPresenter
    lateinit var presenter: ClaimedActionsPresenter

    private lateinit var reviewTypes: List<ReviewType>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_claimed_actions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeReviewTypes()

        actionsList.setHasFixedSize(true)
    }

    override fun showData(actionTypes: Set<String>, credits: Map<String, Int>) {
        val filteredTypes = reviewTypes.filter { it.key in actionTypes }

        val adapter = ReviewAdapter(filteredTypes, credits, null)

        actionsList.adapter = adapter
    }

    private fun initializeReviewTypes() {
        reviewTypes = listOf(
                /* TODO uncomment
                 ReviewType(
                         imageRes = R.drawable.instagram_logo,
                         titleRes = R.string.insta_post,
                         descriptionRes = R.string.insta_post_description,
                         key = TYPE_INSTAGRAM_POST
                 ),*/

                ReviewType(
                        imageRes = R.drawable.instagram_logo,
                        titleRes = R.string.insta_story,
                        descriptionRes = R.string.insta_story_description,
                        key = TYPE_INSTAGRAM_STORY),

                ReviewType(
                        imageRes = R.drawable.trip_advisor_logo,
                        titleRes = R.string.trip_advisor,
                        descriptionRes = R.string.trip_advisor_description,
                        key = TYPE_TRIP_ADVISOR),

                ReviewType(
                        imageRes = R.drawable.google_logo,
                        titleRes = R.string.google_places,
                        descriptionRes = R.string.google_places_description,
                        key = TYPE_GOOGLE_PLACES),

                ReviewType(
                        imageRes = R.drawable.facebook_logo,
                        titleRes = R.string.facebook_post,
                        descriptionRes = R.string.facebook_post_description,
                        key = TYPE_FACEBOOK_POST),

                ReviewType(
                        imageRes = R.drawable.yelp_logo,
                        titleRes = R.string.yelp_post,
                        descriptionRes = R.string.yelp_post_description,
                        key = TYPE_YELP)
        )
    }
}
