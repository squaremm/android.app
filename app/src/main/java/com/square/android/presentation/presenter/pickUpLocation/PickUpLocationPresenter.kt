package com.square.android.presentation.presenter.pickUpLocation

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.CampaignInterval
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.pickUpLocation.PickUpLocationView

@InjectViewState
class PickUpLocationPresenter(val location: CampaignInterval.Location): BasePresenter<PickUpLocationView>(){

    init {
        viewState.showData(location)
    }

}
