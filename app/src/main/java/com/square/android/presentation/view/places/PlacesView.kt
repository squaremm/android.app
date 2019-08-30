package com.square.android.presentation.view.places

import com.square.android.data.pojo.Place
import com.square.android.presentation.view.ProgressView

interface PlacesView : ProgressView {
    fun updateDistances()
    fun showData(data: List<Place>, types: MutableList<String>, activatedItems: MutableList<String>, days: MutableList<String>)
    fun updatePlaces(data: List<Place>)
    fun setSelectedFilterItems(positions: List<Int>)

    fun updateFilters(activated: MutableList<String>)

    fun setSelectedDayItem(position: Int)

    fun hideClear()
    fun showClear()
}
