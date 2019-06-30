package com.square.android.presentation.presenter.offersList

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.RedemptionFull
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.offersList.OffersListView
import com.square.android.ui.activity.review.ReviewExtras
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager

@InjectViewState
class OffersListPresenter(private val redemptionId: Long) : BasePresenter<OffersListView>(){

    private var data: RedemptionFull? = null
    private var offers: List<OfferInfo>? = null

    private var currentPosition = 0

    init {
        loadData()
    }

    private fun loadData() = launch {
        viewState.showProgress()

        data = repository.getRedemption(redemptionId).await()
        offers = repository.getOffersForBooking(data!!.redemption.place.id, redemptionId).await()

        viewState.hideProgress()

        viewState.showData(offers!!, data)
    }

    fun itemClicked(position: Int) {
        currentPosition = position

        viewState.setSelectedItem(position)
    }

    fun submitClicked() {
        val offer = offers!![currentPosition]
        val place = data!!.redemption.place

        viewState.showOfferDialog(offer, place)
    }

    fun dialogSubmitClicked(id: Long) {
        val extras = ReviewExtras(redemptionId, id)

        viewState.acNavigate(2, extras)

        AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.ACTIONS_OPENED.apply { venueName = data?.redemption?.place?.name }, hashMapOf("id" to id.toString())), repository)
    }

}
