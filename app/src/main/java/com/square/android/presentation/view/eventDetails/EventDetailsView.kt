package com.square.android.presentation.view.eventDetails

import com.square.android.data.pojo.EventDetail
import com.square.android.presentation.view.BaseView

interface EventDetailsView : BaseView {

    fun showData(items: List<EventDetail>, dinnerStatus: String)

}