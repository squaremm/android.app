package com.square.android.presentation.presenter.uploadPics

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.uploadPics.UploadPicsView

@InjectViewState
class UploadPicsPresenter(var campaign: Campaign): BasePresenter<UploadPicsView>() {

    fun sendOverReview() = launch {
        viewState.showProgress()

        //TODO change and uncomment later
//        participation = repository.sendOverReview(participation.id).await()

        viewState.replaceToApproval()
    }

    fun removePhoto(index: Int) = launch {
        viewState.showProgress()

        repository.removeCampaignImage(campaign.id, campaign.images!![index].id).await()

        reloadData()
    }

    private fun reloadData() = launch {
        viewState.showProgress()

        campaign = repository.getCampaign(campaign.id).await()

        viewState.reloadData(campaign)
        viewState.hideProgress()
    }

}