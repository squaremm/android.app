package com.square.android.ui.fragment.reviewAction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.R
import com.square.android.data.pojo.*
import com.square.android.ui.activity.party.LocationEvent
import com.square.android.ui.activity.place.IntervalAdapter
import com.square.android.ui.fragment.BaseNoMvpFragment
import com.square.android.ui.fragment.map.MarginItemDecorator

//class DriverRadioEvent(val data: Boolean)
//
//class DriverFilledEvent(val data: DriverFragmentExtras)

//
//class DriverLocationGottenEvent(val data: LocationExtras)

class ReviewActionFragment(private val action: Offer.Action): BaseNoMvpFragment() {

    private var intervalsAdapter: IntervalAdapter? = null

    private var rememberItems: List<String> = listOf()
    private var avoidItems: List<String> = listOf()
    private var typologyItems: Map<String, String> = mapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_review_action, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(action.type){
            //TODO there will be more types - facebook review, facebook story etc

            TYPE_FACEBOOK_POST -> {

            }
            TYPE_INSTAGRAM_POST ->{

            }
            TYPE_INSTAGRAM_STORY -> {

            }
            TYPE_TRIP_ADVISOR -> {

            }
            TYPE_GOOGLE_PLACES -> {

            }
            TYPE_YELP -> {

            }

            //TODO update this drawable
            TYPE_PICTURE -> {

            }
        }



        intervalsAdapter = IntervalAdapter(intervals, intervalHandler)

        driverIntervalsRv.layoutManager = LinearLayoutManager(driverIntervalsRv.context, RecyclerView.HORIZONTAL, false)
        driverIntervalsRv.addItemDecoration(MarginItemDecorator(driverIntervalsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), vertical = false))
        driverIntervalsRv.adapter = intervalsAdapter

        driverAddress.setOnClickListener {
            eventBus.post(LocationEvent(false))
        }

    }


}