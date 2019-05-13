package com.square.android.presentation.view.booking

import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView
import com.square.android.presentation.view.ProgressView
import java.util.*

interface BookingView : ProgressView {
    fun showIntervals(data: List<Place.Interval>)

    fun setSelectedItem(previousPosition: Int?, currentPosition: Int)
    fun showDate(calendar: Calendar)

    fun updateMonthName(calendar: Calendar)

    fun setSelectedDayItem(position: Int)

}
