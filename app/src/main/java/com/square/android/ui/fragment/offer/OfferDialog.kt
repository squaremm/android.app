package com.square.android.ui.fragment.offer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.extensions.loadImage
import kotlinx.android.synthetic.main.offer_dialog.view.*
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.square.android.data.pojo.Place

class OfferDialog(private val context: Context) {

    lateinit var dialog: MaterialDialog


    @SuppressLint("InflateParams")
    fun show(offer: OfferInfo, place: Place?) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.offer_dialog, null, false)

        dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.black_trans_75)))

        view.offerDialogImg.loadImage(offer.photo)
        view.offerDialogPlace.text = offer.name
        view.offerDialogCredits.text = offer.price.toString()
        view.offerDialogComponents.text = offer.compositionAsStr()

        //TODO change when API done
        view.offerHours.text = "Friday - Sunday: 19.00 - 23.00"

        dialog.show()
    }

    fun close(){
        dialog.cancel()
    }

}