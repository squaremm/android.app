package com.square.android.presentation.view.claimedCoupon

import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.UserInfo
import com.square.android.presentation.view.BaseView

interface ClaimedCouponView : BaseView {
    fun showData(offer: Offer, userInfo: UserInfo)
}
