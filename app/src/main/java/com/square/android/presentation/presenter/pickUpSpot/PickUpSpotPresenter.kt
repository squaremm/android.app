package com.square.android.presentation.presenter.pickUpSpot

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.CampaignInterval
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.pickUpSpot.PickUpSpotView
import com.square.android.ui.fragment.pickUpInterval.PickUpIntervalExtras

@InjectViewState
class PickUpSpotPresenter(var campaignId: Long): BasePresenter<PickUpSpotView>(){

    private var locationWrappers: List<CampaignInterval.Location>? = null

    private var selectedIntervalId: Long? = null

    init {
        loadData()
    }

    private fun loadData() = launch {
        locationWrappers = repository.getCampaignLocations(campaignId).await()

        viewState.dataLoaded(locationWrappers!!)
    }

    fun continueClicked() = launch {
        selectedIntervalId?.let {

            val extras = PickUpIntervalExtras(campaignId, it)

            router.navigateTo(SCREENS.PICK_UP_INTERVAL, extras)
        }
    }

    fun spotSelected(index: Int){
        selectedIntervalId = locationWrappers!![index].id

        viewState.setSelectedItem(index)

        viewState.enableButton()
    }

}

