package com.square.android.presentation.presenter.uploadPics

import com.square.android.data.pojo.Participation
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.uploadPics.UploadPicsView

class UploadPicsPresenter(val participation: Participation): BasePresenter<UploadPicsView>() {


    fun sendOverReview(){
        viewState.showProgress()

        //TODO uncomment later
//        repository.sendOverReview(participation.id).await()
//        viewState.replaceToApproval()
    }

}