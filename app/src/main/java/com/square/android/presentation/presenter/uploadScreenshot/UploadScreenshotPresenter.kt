package com.square.android.presentation.presenter.uploadScreenshot

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.uploadScreenshot.UploadScreenshotView

@InjectViewState
class UploadScreenshotPresenter: BasePresenter<UploadScreenshotView>(){

    fun uploadPhoto(photo: ByteArray) = launch {
        viewState.showProgress()

       //TODO waiting for API endpoint
//        repository.uploadScreenshot(photo).await()

        viewState.acExit()
    }
}