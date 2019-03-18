package com.square.android.presentation.view.claimedCoupon

import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.PlaceInfo
import com.square.android.data.pojo.UserInfo
import com.square.android.presentation.view.BaseView

interface ClaimedCouponView : BaseView {
    fun showData(offer: Offer, place: PlaceInfo, userInfo: UserInfo)
}
