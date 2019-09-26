package com.square.android.ui.fragment.dinnerInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.ui.fragment.BaseNoMvpFragment
import kotlinx.android.synthetic.main.fragment_dinner_info.*

class DinnerBackClickedEvent()

class DinnerInfoFragment(private val offerInfo: OfferInfo): BaseNoMvpFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dinner_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dinnerInfoBack.setOnClickListener {
            eventBus.post(DinnerBackClickedEvent())
        }

        dinnerInfoTitle.text = offerInfo.name

        //TODO where to get this text from
        dinnerInfoText.text = "TODO"
    }

}