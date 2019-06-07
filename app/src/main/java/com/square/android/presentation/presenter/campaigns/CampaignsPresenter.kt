package com.square.android.presentation.presenter.campaigns

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.campaigns.CampaignsView

@InjectViewState
class CampaignsPresenter: BasePresenter<CampaignsView>() {

    private var data: List<Campaign>? = null

    init {
        loadData()
    }

    private fun loadData() {
        launch {

            //TODO uncomment later
//            viewState.showProgress()

//            data = repository.getCampaigns().await()
//
//            viewState.hideProgress()
//            viewState.showCampaigns(data!!)
        }
    }


    fun itemClicked(position: Int) {
        val id = data!![position].id

        router.navigateTo(SCREENS.CAMPAIGN_DETAILS, id)
    }

}