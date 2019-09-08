package com.square.android.ui.fragment.driverReturn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.ui.activity.party.DriverExtras
import com.square.android.ui.activity.party.LocationEvent
import com.square.android.ui.activity.party.LocationExtras
import com.square.android.ui.activity.place.IntervalAdapter
import com.square.android.ui.fragment.BaseNoMvpFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_driver_return.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ReturnRadioEvent(val data: Boolean)

class ReturnFilledEvent(val data: ReturnFragmentExtras)
class ReturnFragmentExtras(val returnIntervalId: String?, val latLng: LatLng?)

class ReturnLocationGottenEvent(val data: LocationExtras)

class DriverReturnFragment(private val driverExtras: DriverExtras): BaseNoMvpFragment() {

    var needDriver = true

    private var intervalsAdapter: IntervalAdapter? = null

    //TODO change to list later?
    private var intervals: MutableList<Place.Interval> = driverExtras.returnIntervals.toMutableList()

    private var currentPositionIntervals: Int? = null

    private var latLng: LatLng? = null

    init {
        eventBus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReturnLocationGottenEvent(event: ReturnLocationGottenEvent) {
        latLng = event.data.latLng

        driverReturnAddress.setText(event.data.address)
        checkAndSendData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_driver_return, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        driverReturnDepartureValue.text = driverExtras.destination

        driverReturnRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.driverReturnRadioYes -> {
                    needDriver = true
                    eventBus.post(ReturnRadioEvent(needDriver))

                    updateEnabled(true)
                }
                R.id.driverReturnRadioNo -> {
                    needDriver = false
                    eventBus.post(ReturnRadioEvent(needDriver))

                    updateEnabled(false)
                }
            }
        }

        //TODO just for tests, delete later
        intervals.add(Place.Interval("abcd","13:00","21:00", listOf(),3,""))
        intervals.add(Place.Interval("eeee","21:00","23:50", listOf(),0,""))
        intervals.add(Place.Interval("aaaa","05:00","12:00", listOf(),8,""))

        intervalsAdapter = IntervalAdapter(intervals, intervalHandler)

        driverReturnIntervalsRv.layoutManager = LinearLayoutManager(driverReturnIntervalsRv.context, RecyclerView.HORIZONTAL, false)
        driverReturnIntervalsRv.addItemDecoration(MarginItemDecorator(driverReturnIntervalsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), vertical = false))
        driverReturnIntervalsRv.adapter = intervalsAdapter

        driverReturnAddress.setOnClickListener {
            eventBus.post(LocationEvent(true))
        }
    }

    private var intervalHandler = object : IntervalAdapter.Handler{
        override fun itemClicked(position: Int, text: String, offers: List<Long>) {
            if(needDriver && driverExtras.isPremium){
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

        eventBus.post(ReturnFilledEvent(ReturnFragmentExtras(intervalId, latLng)))
    }

    private fun updateEnabled(enabled: Boolean){
        driverReturnAddress.isEnabled = enabled

        driverReturnLabel.isEnabled = enabled
        driverReturnDepartureLabel.isEnabled = enabled
        driverReturnDepartureValue.isEnabled = enabled

        driverReturnAddressDisable.visibility = if(enabled) View.GONE else View.VISIBLE

        driverReturnIntervalsDisable.visibility = if(enabled && driverExtras.isPremium) View.GONE else View.VISIBLE
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }
}