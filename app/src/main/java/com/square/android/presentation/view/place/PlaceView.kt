package com.square.android.presentation.view.place

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView
import java.util.*

interface PlaceView : BaseView {
    fun showData(place: Place, offers: List<OfferInfo>, calendar: Calendar, typeImage: String?)
    fun showDistance(distance: Int?)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showOfferDialog(offer: OfferInfo, place: Place?)

    fun showIntervals(data: List<Place.Interval>)

    fun updateMonthName(calendar: Calendar)

    fun setSelectedDayItem(position: Int)

    fun setSelectedIntervalItem(position: Int)

    fun showProgress()

    fun hideProgress()
}