package com.square.android.presentation.presenter.sendPicture

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.sendPicture.SendPictureView

@InjectViewState
class SendPicturePresenter(val index: Int): BasePresenter<SendPictureView>(){

    fun uploadPhoto(photo: ByteArray) = launch {
        viewState.showProgress()

       //TODO waiting for API endpoint
//        repository.sendPicture(photo).await()

        viewState.acExit()
    }
}