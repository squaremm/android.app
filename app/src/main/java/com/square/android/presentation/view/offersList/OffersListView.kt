package com.square.android.presentation.view.offersList

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.PlaceInfo
import com.square.android.data.pojo.RedemptionFull
import com.square.android.presentation.view.ProgressView

interface OffersListView : ProgressView {
    fun showData(data: List<OfferInfo>, redemptionFull: RedemptionFull?)
    fun setSelectedItem(position: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showOfferDialog(offer: OfferInfo, place: PlaceInfo)

    fun acNavigate(stepNo: Int, data: Any)
}