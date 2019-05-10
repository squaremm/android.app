package com.square.android.presentation.view.offer

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.*
import com.square.android.presentation.view.BaseView

interface OfferView : BaseView {
    fun showData(data: List<OfferInfo>)

    fun setSelectedItem(position: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showOfferDialog(offer: OfferInfo, place: Place?)
}
