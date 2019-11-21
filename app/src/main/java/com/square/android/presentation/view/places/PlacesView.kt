package com.square.android.presentation.view.places

import com.square.android.data.pojo.Place
import com.square.android.presentation.view.ProgressView

interface PlacesView : ProgressView {
    fun showData(data: MutableList<Place>, types: MutableList<String>, activatedItems: MutableList<String>, days: MutableList<String>)
    fun updateFilters(types: MutableList<String>, activated: MutableList<String>, updateAll: Boolean)
    fun setSelectedDayItem(position: Int)
    fun hideClear()
    fun showClear()
    fun changeCityName(name: String)
}
