package com.square.android.presentation.view.eventDetails

import com.square.android.data.pojo.Event
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView
import java.util.*

interface EventDetailsView : BaseView {

//    fun showData(event: Event, offers: List<OfferInfo>, calendar: Calendar, typeImage: String?)
    fun showData(event: Event, place: Place, calendar: Calendar, typeImage: String?, places: List<Place>)

    fun updateMonthName(calendar: Calendar)

    fun setSelectedDayItem(position: Int)

    fun setSelectedPlaceItem(index: Int)

    fun updateRestaurantName(placename: String)

    fun hideBookingProgress()
}