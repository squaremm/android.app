package com.square.android.presentation.presenter.campaignDetails

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.campaignDetails.CampaignDetailsView

@InjectViewState
class CampaignDetailsPresenter(val campaignId: Long): BasePresenter<CampaignDetailsView>(){

    private var data: Campaign? = null

    init {
        loadData()
    }

    private fun loadData() {
        launch {
            //TODO uncomment later
//            data = repository.getCampaign(campaignId).await()
//            viewState.showData(data!!)
        }
    }

    fun participateClicked(){

    }

}
