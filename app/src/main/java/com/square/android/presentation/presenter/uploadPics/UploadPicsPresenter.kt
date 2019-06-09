package com.square.android.presentation.presenter.uploadPics

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.network.PhotoId
import com.square.android.data.pojo.Participation
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.uploadPics.UploadPicsView

@InjectViewState
class UploadPicsPresenter(var participation: Participation): BasePresenter<UploadPicsView>() {

    fun sendOverReview() = launch {
        viewState.showProgress()

        //TODO uncomment later
        //not sure if data should be reloaded manually after this call or API will return updated Participation with approval status "Waiting for review"
//        participation = repository.sendOverReview(participation.id).await()

        viewState.replaceToApproval()
    }

    fun removePhoto(index: Int) = launch {
        viewState.showProgress()

        //TODO uncomment later
//        repository.removeParticipationPhoto(participation.id, PhotoId( participation.photos!![index].id)).await()

        reloadData()
    }

    private fun reloadData() = launch {
        viewState.showProgress()

        //TODO uncomment later
//        participation = repository.getParticipation(participation.id).await()

        viewState.reloadData(participation)
        viewState.hideProgress()
    }

}