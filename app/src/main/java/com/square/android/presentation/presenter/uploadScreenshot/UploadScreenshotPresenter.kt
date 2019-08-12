package com.square.android.presentation.presenter.uploadScreenshot

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.sendPicture.SendPictureEvent
import com.square.android.presentation.view.uploadScreenshot.UploadScreenshotView
import com.square.android.ui.fragment.sendPictureUpload.UploadPictureExtras
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class UploadScreenshotPresenter(val index: Int): BasePresenter<UploadScreenshotView>(){

    private val eventBus: EventBus by inject()

    init {
        viewState.hideProgress()
    }

    fun uploadPhoto(photo: ByteArray) = launch {
        viewState.showProgress()

        eventBus.post(SendPictureEvent(UploadPictureExtras(index,photo)))

        viewState.goBack()
    }
}
