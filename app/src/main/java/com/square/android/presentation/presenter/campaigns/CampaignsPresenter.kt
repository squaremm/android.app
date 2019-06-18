package com.square.android.presentation.presenter.campaigns

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.CampaignInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.campaigns.CampaignsView

@InjectViewState
class CampaignsPresenter: BasePresenter<CampaignsView>() {

    private var data: List<CampaignInfo>? = null

    init {
        loadData()
    }

    private fun loadData() = launch {

        viewState.showProgress()

        data = repository.getCampaigns().await()

        viewState.hideProgress()
        viewState.showCampaigns(data!!)
    }

    fun itemClicked(position: Int) {
        val id = data!![position].id

        if(data!![position].hasWinner){
            router.navigateTo(SCREENS.CAMPAIGN_FINISHED, id)
        } else{
            router.navigateTo(SCREENS.CAMPAIGN_DETAILS, id)
        }
    }

}