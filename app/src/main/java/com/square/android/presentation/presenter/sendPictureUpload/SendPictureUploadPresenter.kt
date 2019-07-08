package com.square.android.presentation.presenter.sendPictureUpload

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.sendPicture.SendPictureEvent
import com.square.android.presentation.view.sendPictureUpload.SendPictureUploadView
import com.square.android.ui.fragment.sendPictureUpload.SendPictureExtras
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class SendPictureUploadPresenter(val index: Int, val type: Int): BasePresenter<SendPictureUploadView>(){

    private val eventBus: EventBus by inject()

    init {
        viewState.hideProgress()

        viewState.changeLabel(type)
    }

    fun uploadPhoto(photo: ByteArray) = launch {
        viewState.showProgress()

        //TODO waiting for API endpoint
//        repository.sendPicture(photo).await()

        eventBus.post(SendPictureEvent(SendPictureExtras(index,type)))

        router.finishChain()
    }
}
