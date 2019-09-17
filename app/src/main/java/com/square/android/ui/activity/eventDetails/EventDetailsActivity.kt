package com.square.android.ui.activity.eventDetails

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.EventDetail
import com.square.android.presentation.presenter.eventDetails.EventDetailsPresenter
import com.square.android.presentation.view.eventDetails.EventDetailsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_event_details.*
import ru.terrakok.cicerone.Navigator

const val EVENT_EXTRA_ID = "EVENT_EXTRA_ID"

class EventDetailsActivity: BaseActivity(), EventDetailsView, EventDetailsAdapter.Handler{

    @InjectPresenter
    lateinit var presenter: EventDetailsPresenter

    @ProvidePresenter
    fun providePresenter() = EventDetailsPresenter(intent.getLongExtra(EVENT_EXTRA_ID,0))

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private var eventDetailAdapter: EventDetailsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
    }

    override fun showData(items: List<EventDetail>, dinnerStatus: String) {
        eventDetailAdapter = EventDetailsAdapter(items,this, dinnerStatus )
        eventDetailsList.layoutManager = LinearLayoutManager(eventDetailsList.context, RecyclerView.VERTICAL, false)
        eventDetailsList.adapter = eventDetailAdapter
    }

    override fun itemClicked(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}