package com.square.android.presentation.view.eventSummary

import com.square.android.data.pojo.EventSummary
import com.square.android.presentation.view.BaseView

interface EventSummaryView : BaseView {

    fun showData(items: List<EventSummary>, dinnerStatus: String)

}