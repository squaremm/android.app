package com.square.android.presentation.presenter.sendPictureUpload

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.sendPicture.SendPictureEvent
import com.square.android.presentation.view.sendPictureUpload.SendPictureUploadView
import com.square.android.ui.fragment.sendPictureUpload.UploadPictureExtras
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
                                                //TODO what to do with image type?
class SendPictureUploadPresenter(val index: Int, val type: Int): BasePresenter<SendPictureUploadView>(){

    private val eventBus: EventBus by inject()

    init {
        viewState.hideProgress()

        viewState.changeLabel(type)
    }

    fun uploadPhoto(photo: ByteArray) = launch {
        viewState.showProgress()

        eventBus.post(SendPictureEvent(UploadPictureExtras(index,photo)))

        router.finishChain()
    }
}
