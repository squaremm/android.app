package com.square.android.presentation.view.sendPictureChoose

import com.square.android.data.pojo.SendPictureType
import com.square.android.presentation.view.BaseView

interface SendPictureChooseView: BaseView {

    fun showData(items: List<SendPictureType>)
}