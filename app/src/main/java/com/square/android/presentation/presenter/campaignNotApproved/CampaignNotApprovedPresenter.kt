package com.square.android.presentation.presenter.campaignNotApproved

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.campaignNotApproved.CampaignNotApprovedView

@InjectViewState
class CampaignNotApprovedPresenter(var campaign: Campaign): BasePresenter<CampaignNotApprovedView>(){

    init {
        viewState.showData(campaign)
    }

    fun joinClicked() = launch {
        viewState.showProgress()

        repository.joinCampaign(campaign.id).await()

        viewState.showThanks()

        viewState.hideProgress()
    }

}
