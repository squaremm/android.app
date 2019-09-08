package com.square.android.ui.fragment.claimedActions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.square.android.R
import com.square.android.presentation.view.claimedActions.ClaimedActionsView
import com.square.android.presentation.presenter.claimedActions.ClaimedActionsPresenter
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.data.pojo.*
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.places.GridItemDecoration
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

    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_claimed_actions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionsList.setHasFixedSize(true)

        loadingDialog = LoadingDialog(activity!!)
    }

    override fun showData(actionTypes: Set<String>, credits: Map<String, Int>) {
        updateReviewTypes()

        filteredTypes = reviewTypes.filter { it.key in actionTypes}

        adapter = ReviewAdapter(filteredTypes!!, credits, this)

        actionsList.layoutManager = GridLayoutManager(context, 2)
        actionsList.adapter = adapter
        actionsList.addItemDecoration(GridItemDecoration(2,actionsList.context.resources.getDimension(R.dimen.value_24dp).toInt(), false))
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun clearSelectedItem() {
        adapter?.clearSelection()
    }

    override fun disableItem(position: Int) {
        adapter?.disableReviewType(position)
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

    private fun updateReviewTypes() {
//        reviewTypes.forEach {
//            it.enabled = true
//        }
    }

    override fun showProgress() {}

    override fun hideProgress() {}

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
}
