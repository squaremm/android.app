package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.Profile
import com.square.android.extensions.loadImage
import kotlinx.android.synthetic.main.coupon_body.view.*

class ClaimedCouponDialog(val context: Context) {

    var dialog: MaterialDialog? = null


    @SuppressLint("InflateParams")
    fun show(offer: Offer, place: Place, user: Profile.User) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fragment_claimed_coupon, null, false)

        dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(false)
                .build()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.couponImage.loadImage(offer.photo)

        user.mainImage?.run {
            view.couponAvatar.loadImage(this)
        }


        Log.e("LOL", place.name + " " + place.address + " " + user.name)

        view.couponPlaceName.text = place.name
        view.couponAddress.text = place.address

        view.couponPersonName.text = user.name

        view.couponOfferName.text = offer.name

        view.couponComponents.text = offer.compositionAsString()

        view.couponCoins.text = offer.price.toString()

        view.bt_coupon_check_in.visibility = View.VISIBLE
        view.bt_coupon_check_in.setOnClickListener {
            cancel()
        }

        dialog?.show()
    }

    fun cancel(){
        dialog?.cancel()
    }
}