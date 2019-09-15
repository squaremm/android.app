package com.square.android.presentation.presenter.eventDetails

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.eventDetails.EventDetailsView

@InjectViewState
class EventDetailsPresenter(val eventId: Long): BasePresenter<EventDetailsView>(){

}