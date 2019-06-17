package com.square.android.presentation.presenter.campaignDetails

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.OldCampaign
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.campaignDetails.CampaignDetailsView

@InjectViewState
class CampaignDetailsPresenter(val campaignId: Long): BasePresenter<CampaignDetailsView>(){

    private var data: OldCampaign? = null

    init {
        loadData()
    }

    private fun loadData() = launch {

        //TODO uncomment later
//            data = repository.getCampaign(campaignId).await()
//            viewState.showData(data!!)

        viewState.hideProgress()

//        viewState.showData(data!!)
        }


    fun joinClicked() = launch {
        viewState.showProgress()

        //   data = repository.participate.await()

        viewState.showThanks()

        viewState.hideProgress()
    }

}
