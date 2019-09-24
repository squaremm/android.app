package com.square.android.presentation.presenter.eventSummary

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.EventSummary
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.eventSummary.EventSummaryView

@InjectViewState
class EventSummaryPresenter(val eventId: Long): BasePresenter<EventSummaryView>(){

    private var data: List<EventSummary> = listOf()

    init {
        loadData()
    }

    private fun loadData() = launch {
        //TODO data =  get event booking summary from API

        viewState.showData(data, "TODO")
    }

    fun itemClicked(position: Int){

    }

}