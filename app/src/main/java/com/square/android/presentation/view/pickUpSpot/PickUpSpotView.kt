package com.square.android.presentation.view.pickUpSpot

import com.square.android.data.pojo.CampaignInterval
import com.square.android.data.pojo.CampaignLocationWrapper
import com.square.android.presentation.view.ProgressView
import java.util.*

interface PickUpSpotView : ProgressView {

    fun setSelectedDayItem(position: Int)

    fun dataLoaded()

    fun setContentLoading()

    fun setContentNormal()

    fun setSlotsLoading()

    fun setSlotsNormal()

    fun assignAddress(locationWrapper: CampaignLocationWrapper)

    fun changeDate(calendar: Calendar, useCalendarDay: Boolean)

    fun updateSlots(intervalSlots: List<CampaignInterval.Slot>)

    fun setSelectedItem(previousPosition: Int?, currentPosition: Int)
}
