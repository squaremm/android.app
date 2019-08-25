package com.square.android.presentation.view.partyDetails

import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView
import java.util.*

interface PartyDetailsView : BaseView {

//    fun showData(party: Party, offers: List<OfferInfo>, calendar: Calendar, typeImage: String?)
    fun showData(party: Place, offers: List<OfferInfo>, calendar: Calendar, typeImage: String?)


//    @StateStrategyType(OneExecutionStateStrategy::class)
//    fun showOfferDialog(offer: OfferInfo, party: Party?)
//    @StateStrategyType(OneExecutionStateStrategy::class)
//    fun showOfferDialog(offer: OfferInfo, party: Place?)

    fun showIntervals(data: List<Place.Interval>)

    fun updateMonthName(calendar: Calendar)

    fun setSelectedDayItem(position: Int)

    fun setSelectedIntervalItem(position: Int)

    fun showProgress()

    fun hideProgress()

    fun setSelectedPlaceItem(index: Int)

    fun updateRestaurantName(placename: String)

    fun hideBookingProgress()
}