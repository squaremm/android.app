package com.square.android.presentation.view.placesList

import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView

interface PlacesListView : BaseView{
    fun updateDistances()
    fun showData(data: List<Place>)
    fun updatePlaces(data: List<Place>)
}
