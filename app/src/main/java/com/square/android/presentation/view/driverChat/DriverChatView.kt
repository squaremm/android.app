package com.square.android.presentation.view.driverChat

import com.square.android.data.pojo.Driver
import com.square.android.presentation.view.ProgressView

interface DriverChatView : ProgressView {
    fun showData(items: List<Any>, driver: Driver, userId: Long)
}