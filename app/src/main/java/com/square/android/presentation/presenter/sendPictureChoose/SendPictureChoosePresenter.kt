package com.square.android.presentation.presenter.sendPictureChoose

import com.arellomobile.mvp.InjectViewState
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.pojo.SendPictureType
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.sendPictureChoose.SendPictureChooseView
import com.square.android.ui.fragment.sendPictureUpload.SendPictureExtras

@InjectViewState
class SendPictureChoosePresenter(val index: Int) : BasePresenter<SendPictureChooseView>() {

    init {
        val items: MutableList<SendPictureType> = mutableListOf()

        //TODO waiting for images
        items.add(SendPictureType(R.drawable.trip_advisor_logo, R.string.foodpic))
        items.add(SendPictureType(R.drawable.trip_advisor_logo, R.string.atmosphere))
        items.add(SendPictureType(R.drawable.trip_advisor_logo, R.string.model_in_venue))
        items.add(SendPictureType(R.drawable.trip_advisor_logo, R.string.still_life))

        viewState.showData(items.toList())
    }

    fun typeSelected(typeIndex: Int) {
        val extras = SendPictureExtras(index, typeIndex + 1)
        router.navigateTo(SCREENS.SEND_PICTURE_UPLOAD, extras)
    }
}