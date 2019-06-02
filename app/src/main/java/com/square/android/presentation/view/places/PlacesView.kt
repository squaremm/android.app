package com.square.android.presentation.view.places

import com.square.android.data.pojo.Place
import com.square.android.presentation.view.ProgressView

interface PlacesView : ProgressView {
    fun updateDistances()
    fun showPlaces(data: List<Place>, types: MutableList<String>)
    fun updatePlaces(data: List<Place>)
}
