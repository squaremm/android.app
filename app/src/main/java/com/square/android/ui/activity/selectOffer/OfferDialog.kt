package com.square.android.ui.activity.selectOffer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.square.android.R
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.PlaceInfo
import com.square.android.data.pojo.RedemptionFull.Redemption
import com.square.android.data.pojo.UserInfo
import com.square.android.extensions.loadImage
import kotlinx.android.synthetic.main.coupon_body.view.*
import kotlinx.android.synthetic.main.offer_dialog.view.*

class OfferDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(offer: OfferInfo, place: PlaceInfo, userInfo: UserInfo, onAction: () ->Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.offer_dialog, null, false)

        val dialog = AlertDialog.Builder(context)
                .setView(view)
                .create()

        val body = view.couponBody

        body.couponImage.loadImage(offer.photo)
        body.couponAvatar.loadImage(userInfo.photo)

        body.couponPlaceName.text = place.name
        body.couponAddress.text = place.address

        body.couponPersonName.text = userInfo.name

        body.couponOfferName.text = offer.name

        body.couponComponents.text = offer.compositionAsString()

        body.couponCoins.text = context.getString(R.string.price_format, offer.price)

        body.couponBack.setOnClickListener { dialog.dismiss() }
        body.couponBack.visibility = View.VISIBLE

        view.offerDialogSubmit.setOnClickListener { onAction.invoke() }

        dialog.show()
    }
}