package com.square.android.ui.fragment.dinnerOffer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.extensions.loadImage
import com.square.android.ui.fragment.BaseNoMvpFragment
import kotlinx.android.synthetic.main.fragment_dinner_offer.*
import java.util.regex.Pattern

class DinnerInfoClickedEvent()

class DinnerInfoCloseEvent()

class DinnerOfferFragment(private val offerInfo: OfferInfo): BaseNoMvpFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dinner_offer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dinnerOfferImg.loadImage((offerInfo.mainImage ?: offerInfo.photo) ?: "")
        dinnerOfferName.text = offerInfo.name
        dinnerOfferCredits.text = offerInfo.price.toString()

        if(!offerInfo.composition.isNullOrEmpty()){
            scrollViewMaxHeight.visibility = View.VISIBLE
            dinnerOfferDetails.visibility = View.VISIBLE
            dinnerOfferQt.visibility = View.VISIBLE

            val numberList: MutableList<Int> = mutableListOf()
            val names = offerInfo.compositionAsStr()

            val p = Pattern.compile("\\d+")
            val m = p.matcher(offerInfo.compositionAsString())
            while (m.find()) {
                numberList.add(m.group().toInt())
            }

            offerNames.text = names

            offerNumbers.text = numberList.joinToString(separator = "\n")
        }

        //TODO when info button will be visible?
        dinnerOfferInfo.setOnClickListener { eventBus.post(DinnerInfoClickedEvent()) }

        dinnerOfferClose.setOnClickListener { eventBus.post(DinnerInfoCloseEvent()) }
    }
}