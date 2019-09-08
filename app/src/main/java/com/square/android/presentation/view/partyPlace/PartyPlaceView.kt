package com.square.android.presentation.view.partyPlace

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView
import java.util.*

interface PartyPlaceView : BaseView {

    fun showData(place: Place, offers: List<OfferInfo>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showOfferDialog(offer: OfferInfo, place: Place?)

    fun showIntervals(data: List<Place.Interval>)

    fun setSelectedIntervalItem(position: Int)
}