package com.square.android.presentation.view.pickUpInterval

import com.square.android.data.pojo.CampaignInterval
import com.square.android.presentation.view.ProgressView
import java.util.*

interface PickUpIntervalView : ProgressView {
    fun setSelectedDayItem(position: Int)

    fun setContentLoading()

    fun setContentNormal()

    fun setSlotsLoading()

    fun setSlotsNormal()

    fun changeDate(calendar: Calendar, useCalendarDay: Boolean)

    fun updateSlots(intervalSlots: List<CampaignInterval.Slot>)

    fun setSelectedItem(previousPosition: Int?, currentPosition: Int)
}