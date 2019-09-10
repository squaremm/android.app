package com.square.android.ui.fragment.claimedCoupon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.UserInfo
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.claimedCoupon.ClaimedCouponPresenter
import com.square.android.presentation.view.claimedCoupon.ClaimedCouponView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.coupon_body.view.*
import kotlinx.android.synthetic.main.fragment_claimed_coupon.*

class ClaimedCouponFragment : BaseFragment(), ClaimedCouponView {
    override fun showData(offer: Offer, userInfo: UserInfo) {
        coupon.couponImage.loadImage(offer.photo)

        //TODO load user photo
        // coupon.couponAvatar.loadImage(offer.photo)

        //TODO remove name from layout ? OR get place by ID in presenter
//        coupon.couponPlaceName.text = place.name

        //TODO remove address from layout ? OR get place by ID in presenter
//        coupon.couponAddress.text = place.address

        coupon.couponPersonName.text = userInfo.name

        coupon.couponOfferName.text = offer.name

        coupon.couponComponents.text = offer.compositionAsString()

        coupon.couponCoins.text = offer.price.toString()
    }

    @InjectPresenter
    lateinit var presenter: ClaimedCouponPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_claimed_coupon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
