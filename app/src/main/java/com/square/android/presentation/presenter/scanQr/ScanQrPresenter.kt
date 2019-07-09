package com.square.android.presentation.presenter.scanQr

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.QrInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.campaignDetails.ScanQrEvent
import com.square.android.presentation.view.scanQr.ScanQrView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class ScanQrPresenter : BasePresenter<ScanQrView>() {

    private val eventBus: EventBus by inject()

    fun scanQr(qrCode: String) {
        launch {
            viewState.showProgress()

            repository.sendQr(QrInfo(qrCode)).await()

            viewState.hideProgress()

            eventBus.post(ScanQrEvent())
        }
    }
}
