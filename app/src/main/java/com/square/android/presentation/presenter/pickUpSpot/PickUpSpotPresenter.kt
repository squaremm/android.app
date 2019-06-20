package com.square.android.presentation.presenter.pickUpSpot

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Campaign
import com.square.android.data.pojo.CampaignBookInfo
import com.square.android.data.pojo.CampaignInterval
import com.square.android.data.pojo.CampaignLocationWrapper
import com.square.android.extensions.getStringDate
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.pickUpSpot.PickUpSpotView
import com.square.android.ui.activity.pickupMap.PickUpMapExtras
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.util.*

class IntervalSelectedEvent(val data: Long)

@InjectViewState
class PickUpSpotPresenter(var campaign: Campaign): BasePresenter<PickUpSpotView>(){

    private val eventBus: EventBus by inject()

    private var currentPosition: Int? = null

    private var locationWrappers: List<CampaignLocationWrapper>? = null

    private var selectedIntervalId: Long = 0

    private var calendar: Calendar = Calendar.getInstance()
    private var calendar2: Calendar =  Calendar.getInstance()

    private lateinit var intervalSlots: List<CampaignInterval.Slot>

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onIntervalSelectedEvent(event: IntervalSelectedEvent) {
        assignLocation(event.data)
    }

    init {
        eventBus.register(this)

        getLocations()
    }

    private fun getLocations() = launch {
        locationWrappers = repository.getCampaignLocations(campaign.id).await()

        viewState.dataLoaded()
    }

    private fun assignLocation(intervalId: Long) = launch {
        viewState.setContentLoading()

        selectedIntervalId = intervalId

        val selectedLocationWrapper = locationWrappers!!.first {it.intervalId == selectedIntervalId}

        viewState.assignAddress(selectedLocationWrapper)

        calendar = Calendar.getInstance()
        calendar2.timeInMillis = calendar.timeInMillis

        viewState.changeDate(calendar, true)

        viewState.setContentNormal()

        daySelected(calendar.get(Calendar.DAY_OF_MONTH) -1)
    }

    fun bookClicked() = launch {
        currentPosition?.let {
            viewState.showProgress()

            repository.campaignBook(campaign.id, selectedIntervalId, CampaignBookInfo(getSelectedStringDate(), intervalSlots[it].id)).await()

            //TODO where to go now?

            viewState.hideProgress()
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

        intervalSlots = repository.getCampaignSlots(campaign.id, selectedIntervalId, getSelectedStringDate()).await()

        viewState.updateSlots(intervalSlots)

        viewState.setSlotsNormal()
    }

    fun slotClicked(position: Int){
        viewState.setSelectedItem(currentPosition, position)

        currentPosition = position
    }

    fun addressClicked(){
        if(!locationWrappers.isNullOrEmpty()){

            val extras = PickUpMapExtras(locationWrappers!!, selectedIntervalId)

            //TODO check if working
            router.navigateTo(SCREENS.PICK_UP_MAP, extras)
        }
    }

    override fun onDestroy() {
        eventBus.unregister(this)

        super.onDestroy()
    }

    private fun getSelectedStringDate() = calendar2.getStringDate()

}

