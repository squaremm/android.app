package com.square.android.presentation.view.offer

import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.OfferInfo
import com.square.android.presentation.view.BaseView

interface OfferView : BaseView {
    fun showData(data: List<OfferInfo>)
}
