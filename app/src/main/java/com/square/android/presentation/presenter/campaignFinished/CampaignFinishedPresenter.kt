package com.square.android.presentation.presenter.campaignFinished

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.campaignFinished.CampaignFinishedView

@InjectViewState
class CampaignFinishedPresenter(private val campaignId: Long): BasePresenter<CampaignFinishedView>() {

    private var data: Campaign? = null

    init {
        loadData()
    }

    private fun loadData() {
        launch {

            //TODO uncomment later
//            data = repository.getCampaign(campaignId).await()
//
//            viewState.showData(data!!)
        }
    }
}