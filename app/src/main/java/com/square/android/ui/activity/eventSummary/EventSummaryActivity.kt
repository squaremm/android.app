package com.square.android.ui.activity.eventSummary

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.EventSummary
import com.square.android.presentation.presenter.eventSummary.EventSummaryPresenter
import com.square.android.presentation.view.eventSummary.EventSummaryView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_event_summary.*
import ru.terrakok.cicerone.Navigator

const val EVENT_EXTRA_ID = "EVENT_EXTRA_ID"

class EventSummaryActivity: BaseActivity(), EventSummaryView, EventSummaryAdapter.Handler{

    @InjectPresenter
    lateinit var presenter: EventSummaryPresenter

    @ProvidePresenter
    fun providePresenter() = EventSummaryPresenter(intent.getLongExtra(EVENT_EXTRA_ID,0))

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private var eventSummaryAdapter: EventSummaryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_summary)
    }

    override fun showData(items: List<EventSummary>, dinnerStatus: String) {
        eventSummaryAdapter = EventSummaryAdapter(items,this, dinnerStatus )
        eventSummaryList.layoutManager = LinearLayoutManager(eventSummaryList.context, RecyclerView.VERTICAL, false)
        eventSummaryList.adapter = eventSummaryAdapter
    }

    override fun itemClicked(position: Int) {
        presenter.itemClicked(position)
    }

}