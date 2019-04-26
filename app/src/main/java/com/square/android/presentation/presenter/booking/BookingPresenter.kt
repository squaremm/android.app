package com.square.android.presentation.presenter.booking

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Place
import com.square.android.extensions.getStringDate
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.placeDetail.BookSelectedEvent
import com.square.android.presentation.view.booking.BookingView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlaceLoadedEvent(val data: Place)

class SpotsUpdatedEvent()

@InjectViewState
class BookingPresenter : BasePresenter<BookingView>() {
    private var currentPosition: Int? = null

    private val eventBus: EventBus by inject()

    private var calendar: Calendar = Calendar.getInstance()

    private var placeId: Long? = null

    init {
        eventBus.register(this)

        viewState.showDate(calendar)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onIntervalsLoadedEvent(event: PlaceLoadedEvent) {
        placeId = event.data.id

        loadIntervals()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSpotsUpdated(event: SpotsUpdatedEvent) {
        loadIntervals()
    }

    fun itemClicked(position: Int) {
        viewState.setSelectedItem(currentPosition, position)

        currentPosition = position
    }

    fun selectNextDay() {
        dateSelected(calendar.apply { add(Calendar.DATE, 1) })
    }

    fun selectPreviousDay() {
        dateSelected(calendar.apply { add(Calendar.DATE, -1) })
    }

    fun dateSelected(newCalendar: Calendar) {
        calendar = newCalendar

        viewState.showDate(calendar)

        loadIntervals()
    }

    fun bookClicked() {
        val date = getStringDate()

        val event = BookSelectedEvent(currentPosition, date)

        eventBus.post(event)
    }

    private fun loadIntervals() {
        launch {
            viewState.showProgress()

            val intervals = repository.getIntervals(placeId!!, getStringDate()).await()

            viewState.hideProgress()

            viewState.showIntervals(intervals)
        }
    }

    private fun getStringDate() = calendar.getStringDate()

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
