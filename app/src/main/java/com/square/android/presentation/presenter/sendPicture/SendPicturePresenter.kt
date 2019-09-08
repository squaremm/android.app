package com.square.android.presentation.presenter.sendPicture

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.sendPicture.SendPictureView
import com.square.android.ui.fragment.sendPictureUpload.UploadPictureExtras

class SendPictureEvent(val data: UploadPictureExtras)

@InjectViewState
class SendPicturePresenter(val index: Int): BasePresenter<SendPictureView>(){

    init {
        router.navigateTo(SCREENS.SEND_PICTURE_CHOOSE, index)
    }

    fun finishChain() = router.finishChain()
}