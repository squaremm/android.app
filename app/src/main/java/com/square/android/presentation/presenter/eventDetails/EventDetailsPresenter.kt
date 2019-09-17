package com.square.android.presentation.presenter.eventDetails

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.EventDetail
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.eventDetails.EventDetailsView

@InjectViewState
class EventDetailsPresenter(val eventId: Long): BasePresenter<EventDetailsView>(){

    private var data: List<EventDetail> = listOf()

    init {
        loadData()
    }

    private fun loadData() = launch {
        //TODO data =  get event booking summary from API

        viewState.showData(data, "TODO")
    }

}