package com.square.android.ui.activity.eventDetails

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.eventDetails.EventDetailsPresenter
import com.square.android.presentation.view.eventDetails.EventDetailsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import ru.terrakok.cicerone.Navigator

const val EVENT_EXTRA_ID = "EVENT_EXTRA_ID"

class EventDetailsActivity: BaseActivity(), EventDetailsView{
    @InjectPresenter
    lateinit var presenter: EventDetailsPresenter

    @ProvidePresenter
    fun providePresenter() = EventDetailsPresenter(intent.getLongExtra(EVENT_EXTRA_ID,0))

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
    }

}