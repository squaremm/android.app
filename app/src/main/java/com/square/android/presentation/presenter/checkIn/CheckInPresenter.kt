package com.square.android.presentation.presenter.checkIn

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.RedemptionFull
import com.square.android.domain.review.ReviewInteractor
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.checkIn.CheckInView
import com.square.android.ui.fragment.review.ReviewExtras
import org.koin.standalone.inject

@InjectViewState
class CheckInPresenter(private val redemptionId: Long, private val offerId: Long): BasePresenter<CheckInView>(){

    private var data: Offer? = null
    private var redemptionFull: RedemptionFull? = null

    private val interactor : ReviewInteractor by inject()

    init {
        loadData()
    }

    private fun loadData() {
        launch {
            data = interactor.getOffer(offerId).await()
            val user = repository.getCurrentUser().await()

            val placeId = data!!.place.id
            val place = repository.getPlace(placeId).await()

            redemptionFull = repository.getRedemption(redemptionId).await()

            viewState.showData(data!!, user, place, redemptionFull!!)

            viewState.hideProgress()
        }
    }

    fun checkInClicked(){
        val extras = ReviewExtras(redemptionId, offerId)

        router.navigateTo(SCREENS.REVIEW, extras)
    }

}
