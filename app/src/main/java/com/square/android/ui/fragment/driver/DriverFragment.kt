package com.square.android.ui.fragment.driver

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.R
import com.square.android.data.pojo.DriverRide
import com.square.android.data.pojo.Place
import com.square.android.ui.activity.party.DriverExtras
import com.square.android.ui.activity.party.LocationEvent
import com.square.android.ui.activity.party.LocationExtras
import com.square.android.ui.activity.place.IntervalAdapter
import com.square.android.ui.fragment.BaseNoMvpFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_driver.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DriverRadioEvent(val data: Boolean)

class DriverFilledEvent(val data: DriverFragmentExtras)
class DriverFragmentExtras(val driverIntervalId: String?, val latLng: LatLng?)

class DriverLocationGottenEvent(val data: LocationExtras)

class DriverFragment(private val driverExtras: DriverExtras): BaseNoMvpFragment() {

    var needDriver = true

    private var intervalsAdapter: IntervalAdapter? = null

    //TODO change to list later?
    private var intervals: MutableList<DriverRide> = driverExtras.driveIntervals.toMutableList()

    private var currentPositionIntervals: Int? = null

    private var latLng: LatLng? = null

    init {
        eventBus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDriverLocationGottenEvent(event: DriverLocationGottenEvent) {
        latLng = event.data.latLng

        driverAddress.setText(event.data.address)
        checkAndSendData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_driver, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!TextUtils.isEmpty(driverExtras.dinnerPlace)){
            driverStop1.visibility = View.VISIBLE
            driverStop1Value.visibility = View.VISIBLE

            driverStop1Value.text = driverExtras.dinnerPlace
        }

        driverDestinationValue.text = driverExtras.destination

        driverRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.driverRadioYes -> {
                    needDriver = true
                    eventBus.post(DriverRadioEvent(needDriver))

                    updateEnabled(true)
                }
                R.id.driverRadioNo -> {
                    needDriver = false
                    eventBus.post(DriverRadioEvent(needDriver))

                    updateEnabled(false)
                }
            }
        }

          //TODO change to DriverRide?
//        intervals.add(Place.Interval("abcd","13:00","21:00", listOf(),3,""))
//        intervals.add(Place.Interval("eeee","21:00","23:50", listOf(),0,""))
//        intervals.add(Place.Interval("aaaa","05:00","12:00", listOf(),8,""))
//
//        intervalsAdapter = IntervalAdapter(intervals, intervalHandler)
//
//        driverIntervalsRv.layoutManager = LinearLayoutManager(driverIntervalsRv.context, RecyclerView.HORIZONTAL, false)
//        driverIntervalsRv.addItemDecoration(MarginItemDecorator(driverIntervalsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), vertical = false))
//        driverIntervalsRv.adapter = intervalsAdapter

        driverAddress.setOnClickListener {
            eventBus.post(LocationEvent(false))
        }

    }

    private var intervalHandler = object : IntervalAdapter.Handler{
        override fun itemClicked(position: Int, text: String, offers: List<Long>) {
            if(needDriver){
                intervalsAdapter?.setSelectedItem(position)

                currentPositionIntervals = position

                checkAndSendData()
            }
        }
    }

    fun checkAndSendData(){
        var intervalId: String? = null

        currentPositionIntervals?.let {
           intervalId = intervals[it].id
        }

        eventBus.post(DriverFilledEvent(DriverFragmentExtras(intervalId, latLng)))
    }

    private fun updateEnabled(enabled: Boolean){
        driverAddress.isEnabled = enabled

        driverDisabledView.visibility = if(enabled) View.GONE else View.VISIBLE
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }
}