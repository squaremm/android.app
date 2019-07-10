package com.square.android.presentation.presenter.pickUpInterval

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.CampaignBookInfo
import com.square.android.data.pojo.CampaignInterval
import com.square.android.extensions.getStringDate
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.campaignDetails.CampaignBookEvent
import com.square.android.presentation.view.pickUpInterval.PickUpIntervalView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject
import java.util.*

@InjectViewState
class PickUpIntervalPresenter(var campaignId: Long, var intervalId: Long): BasePresenter<PickUpIntervalView>(){

    private var calendar: Calendar = Calendar.getInstance()
    private var calendar2: Calendar =  Calendar.getInstance()

    private var currentPosition: Int? = null

    private val eventBus: EventBus by inject()

    private lateinit var intervalSlots: List<CampaignInterval.Slot>

    init {
        loadData()
    }

    fun loadData(){
        viewState.setContentLoading()

        calendar = Calendar.getInstance()
        calendar2.timeInMillis = calendar.timeInMillis

        viewState.changeDate(calendar, true)

        daySelected(calendar.get(Calendar.DAY_OF_MONTH) -1)

        viewState.setContentNormal()
    }

    fun bookClicked() = launch {
        currentPosition?.let {
            viewState.showProgress()

            repository.campaignBook(campaignId, intervalId, CampaignBookInfo(getSelectedStringDate(), intervalSlots[it].id)).await()

            eventBus.post(CampaignBookEvent())
        }
    }

    fun changeMonth(direction: Int){
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // right
        if(direction == 1){
            calendar.add(Calendar.MONTH, 1)
        }
        // left
        else{
            calendar.add(Calendar.MONTH, -1)
        }

        viewState.changeDate(calendar, false)

        daySelected(0)
    }

    fun daySelected(position: Int) = launch {
        viewState.setSelectedDayItem(position)

        viewState.setSlotsLoading()

        calendar2.timeInMillis = calendar.timeInMillis

        calendar2.set(Calendar.DAY_OF_MONTH, position+1)

        intervalSlots = repository.getCampaignSlots(campaignId, intervalId, getSelectedStringDate()).await()

        viewState.updateSlots(intervalSlots)

        viewState.setSlotsNormal()
    }

    fun slotClicked(position: Int){
        viewState.setSelectedItem(currentPosition, position)

        currentPosition = position
    }

    private fun getSelectedStringDate() = calendar2.getStringDate()
}
