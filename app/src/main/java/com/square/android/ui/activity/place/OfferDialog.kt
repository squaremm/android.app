package com.square.android.ui.activity.place

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.extensions.loadImage
import kotlinx.android.synthetic.main.offer_dialog.view.*
import java.util.regex.Pattern

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

        dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)))

        view.offerDialogImg.loadImage((offer.mainImage ?: offer.photo) ?: "")
        view.offerDialogName.text = offer.name
        view.offerDialogCredits.text = offer.price.toString()

        //TODO change later
        //TODO separate values for numbers and names will be added in API later
        if(!offer.composition.isNullOrEmpty()){
            view.scrollViewMaxHeight.visibility = View.VISIBLE
            view.offerDialogDetails.visibility = View.VISIBLE
            view.offerDialogQt.visibility = View.VISIBLE

            val numberList: MutableList<Int> = mutableListOf()
            val names = offer.compositionAsStr()

            val p = Pattern.compile("\\d+")
            val m = p.matcher(offer.compositionAsString())
            while (m.find()) {
                numberList.add(m.group().toInt())
            }

            view.offerNames.text = names

            view.offerNumbers.text = numberList.joinToString(separator = "\n")
        }

        view.offerClose.setOnClickListener { close() }

        dialog.show()
    }

    fun close(){
        dialog.cancel()
    }

}