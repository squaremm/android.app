package com.square.android.presentation.presenter.checkIn

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Offer
import com.square.android.domain.review.ReviewInteractor
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.checkIn.CheckInView
import org.koin.standalone.inject

@InjectViewState
class CheckInPresenter(private val redemptionId: Long, private val offerId: Long): BasePresenter<CheckInView>(){

    private var data: Offer? = null

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

            viewState.hideProgress()
            viewState.showData(data!!, user, place)
        }
    }

    fun checkInClicked(){

    }

}
