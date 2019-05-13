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
    private var calendar2: Calendar = Calendar.getInstance()

    private var place: Place? = null

    init {
        eventBus.register(this)

        viewState.showDate(calendar)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onIntervalsLoadedEvent(event: PlaceLoadedEvent) {
        place = event.data

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

    fun bookClicked() {
        val date = getStringDate()

        val event = BookSelectedEvent(currentPosition, date)

        eventBus.post(event)
    }

    fun dayItemClicked(position: Int) {
        viewState.setSelectedDayItem(position)

        calendar2.timeInMillis = calendar.timeInMillis
        calendar2.add(Calendar.DAY_OF_YEAR, position)

        viewState.updateMonthName(calendar2)

        loadIntervals()
    }

    private fun loadIntervals() {
        launch {
            viewState.showProgress()

//            val intervalsWrapper = repository.getIntervals(place?.id!!, getStringDate()).await()
            val intervalSlots = repository.getIntervalSlots(place?.id!!, getStringDate()).await()

            viewState.hideProgress()

            viewState.showIntervals(intervalSlots)
        }
    }


    fun getStringDate() = calendar2.getStringDate()


    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
