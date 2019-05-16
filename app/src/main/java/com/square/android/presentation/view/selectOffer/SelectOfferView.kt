package com.square.android.presentation.view.selectOffer

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.*
import com.square.android.presentation.view.ProgressView

interface SelectOfferView : ProgressView {
    fun showData(data: List<OfferInfo>, redemptionFull: RedemptionFull?)
    fun setSelectedItem(position: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showOfferDialog(offer: OfferInfo, place: PlaceInfo)
}
