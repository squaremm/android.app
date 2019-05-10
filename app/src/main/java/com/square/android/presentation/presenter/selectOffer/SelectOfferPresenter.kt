package com.square.android.presentation.presenter.selectOffer

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.RedemptionFull
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.selectOffer.SelectOfferView
import com.square.android.ui.activity.review.ReviewExtras
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager

@InjectViewState
class SelectOfferPresenter(private val redemptionId: Long) : BasePresenter<SelectOfferView>() {
    private var data: RedemptionFull? = null
    private var offers: List<OfferInfo>? = null

    private var currentPosition = 0

    override fun attachView(view: SelectOfferView?) {
        super.attachView(view)

        if (data == null) loadData()
    }

    private fun loadData() {
        launch {
            viewState.showProgress()

            data = repository.getRedemption(redemptionId).await()
            offers = repository.getPlaceOffers(data!!.redemption.place.id).await()

            viewState.hideProgress()
            viewState.showData(offers!!)
        }
    }

    fun itemClicked(position: Int) {
        currentPosition = position

        viewState.setSelectedItem(position)
    }

    fun backClicked() {
        router.exit()
    }

    fun submitClicked() {
        val offer = offers!![currentPosition]
        val place = data!!.redemption.place

        viewState.showOfferDialog(offer, place)
    }

    fun dialogSubmitClicked(id: Long) {
        val extras = ReviewExtras(redemptionId, id)

        router.replaceScreen(SCREENS.REVIEW, extras)

        AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.ACTIONS_OPENED, hashMapOf("id" to id.toString())))
    }
}
