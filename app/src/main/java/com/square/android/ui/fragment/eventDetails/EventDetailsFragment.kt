package com.square.android.ui.fragment.eventDetails

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Day
import com.square.android.data.pojo.Event
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.extensions.loadImageForIcon
import com.square.android.presentation.presenter.eventDetails.EventDetailsPresenter
import com.square.android.presentation.view.eventDetails.EventDetailsView
import com.square.android.ui.activity.event.EXTRA_EVENT
import com.square.android.ui.activity.event.EventActivity
import com.square.android.ui.activity.place.AboutAdapter
import com.square.android.ui.activity.place.DaysAdapter
import com.square.android.ui.activity.place.OfferAdapter
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import com.square.android.ui.fragment.places.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_event_details.*
import org.jetbrains.anko.bundleOf
import java.util.*

class EventDetailsFragment: BaseFragment(), EventDetailsView{

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(event: Event): EventDetailsFragment {
            val fragment = EventDetailsFragment()

            val args = bundleOf(EXTRA_EVENT to event)
            fragment.arguments = args

            return fragment
        }
    }

    var eventAboutSize = 0

    private var adapter: AboutAdapter? = null

    private var daysAdapter: DaysAdapter? = null

    private var offersAdapter: OfferAdapter? = null

    private var intervalsAdapter: EventIntervalAdapter? = null

    private var placesAdapter: EventPlaceAdapter? = null

    @InjectPresenter
    lateinit var presenter: EventDetailsPresenter

    @ProvidePresenter
    fun providePresenter() = EventDetailsPresenter(getEvent())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventReadMore.setOnClickListener {
            eventReadMore.visibility = View.GONE
            eventAbout.maxLines = Integer.MAX_VALUE
            checkAndShowAboutRv()
        }
    }

    override fun showData(event: Event, offers: List<OfferInfo>, calendar: Calendar, typeImage: String?, places: List<Place>) {

        //TODO waiting for API
        //TODO get detail from event
//        (activity as EventActivity).setEventBookingText(detailName)

        typeImage?.let { eventAboutImage.loadImageForIcon(it) }

//        eventAbout.text = event.description

        //TODO waiting for API
        //TODO delete this and get data from event
        val aboutItems = listOf("www", "insta")

        eventAboutSize = aboutItems.size

        adapter = AboutAdapter(aboutItems)
        eventAboutRv.adapter = adapter
        eventAboutRv.layoutManager = LinearLayoutManager(eventAboutRv.context, RecyclerView.HORIZONTAL, false)
        eventAboutRv.addItemDecoration(MarginItemDecorator(eventAboutRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), vertical = false))

        //TODO waiting for API
        //TODO delete this and get data from event
//        val dressCode: String? = ""
//        val minimumTip: String? = ""
//
//        if(!TextUtils.isEmpty(dressCode) || !TextUtils.isEmpty(minimumTip)){
//            eventRequirementsCl.visibility = View.VISIBLE
//
//            if(!TextUtils.isEmpty(dressCode) && !TextUtils.isEmpty(minimumTip)){
//                eventTipValue.text = minimumTip
//                eventTipContainer.visibility = View.VISIBLE
//
//                eventDressCodeValue.text = dressCode
//            } else {
//                minimumTip?.let {
//                    eventDressCodeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.r_discount))
//                    eventDressCodeName.text = getString(R.string.minimal_tip)
//                    eventDressCodeValue.text = it
//
//                } ?: run{
//                    eventDressCodeValue.text = dressCode
//                }
//            }
//        }

        if(!offers.isNullOrEmpty()){
            //TODO waiting for API
            //TODO alpha 0.3 te, ktorych klub nie oferuje

            eventrOffersLabel.visibility = View.VISIBLE
            eventOffersRv.visibility = View.VISIBLE

            offersAdapter = OfferAdapter(offers, null, true)
            eventOffersRv.adapter = offersAdapter

            eventOffersRv.layoutManager = GridLayoutManager(activity!!, 3)
            eventOffersRv.adapter = offersAdapter
            eventOffersRv.addItemDecoration(GridItemDecoration(3,eventOffersRv.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(), false))
        }

        if(!places.isNullOrEmpty()){
            eventDinnerInfoLabel.visibility = View.VISIBLE
            eventDinnerInfoRv.visibility = View.VISIBLE

            placesAdapter = EventPlaceAdapter(places, placeHandler)
            eventDinnerInfoRv.adapter = offersAdapter
            eventDinnerInfoRv.layoutManager = LinearLayoutManager(eventDinnerInfoRv.context, RecyclerView.HORIZONTAL, false)
            eventDinnerInfoRv.addItemDecoration(MarginItemDecorator(eventAboutRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), vertical = false))
        }

        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        eventBookingMonth.text = getString(R.string.calendar_format, month, calendar.get(Calendar.YEAR))

        val d = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).capitalize()
        val m =  calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).capitalize()

        (activity as EventActivity).updateDateLabel(d +", " + m +" " + dayToString(calendar.get(Calendar.DAY_OF_MONTH)))

        val days = mutableListOf<Day>()
        val calendar2 = Calendar.getInstance().apply { timeInMillis = calendar.timeInMillis }

        for (x in 0 until 7) {
            val day = Day()

            day.monthNumber = calendar2.get(Calendar.MONTH) + 1
            day.dayValue = calendar2.get(Calendar.DAY_OF_MONTH)
            day.dayName = calendar2.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).substring(0, 1)

            days.add(day)

            calendar2.add(Calendar.DAY_OF_YEAR, 1)
        }

        daysAdapter = DaysAdapter(days.toList(), dayHandler)
        eventBookingCalendar.adapter = daysAdapter
        daysAdapter!!.selectedItemPosition = 0
        daysAdapter!!.notifyItemChanged(0, DaysAdapter.SelectedPayload)

        eventAbout.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onAboutLoaded()
                eventAbout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    var placeHandler = object : EventPlaceAdapter.Handler{
        override fun itemClicked(position: Int) {
            presenter.placeItemClicked(position)
        }
    }

    var intervalHandler = object : EventIntervalAdapter.Handler{
        override fun itemClicked(position: Int, enabled: Boolean) {
            if(enabled){
                presenter.intervalItemClicked(position)
                (activity as EventActivity).setTimeframeSelected(true)
            }
        }
    }

    var dayHandler = object : DaysAdapter.Handler{
        override fun itemClicked(position: Int) {
            presenter.dayItemClicked(position)

            (activity as EventActivity).disableButton()
        }
    }

    private fun onAboutLoaded(){
        var startOffset: Int
        var endOffset: Int
        var lineToEnd = 3
        val maxLines = 3
        var isLineSelected = false
        var notEmptyLinesToShowMore = 0

        if (!TextUtils.isEmpty(eventAbout.text)) {
            if (eventAbout.layout != null) {
                var shouldShowReadMore = true

                if (eventAbout.layout.lineCount <= maxLines) {
                    shouldShowReadMore = false
                } else {

                    for(i in 2 until eventAbout.layout.lineCount){
                        startOffset = eventAbout.layout.getLineStart(i)
                        endOffset = eventAbout.layout.getLineEnd(i)
                        if (!TextUtils.isEmpty((eventAbout.layout.text.subSequence(startOffset, endOffset)).toString().trim())) {
                            if (!isLineSelected) {
                                lineToEnd = i + 1
                                isLineSelected = true
                            } else {
                                notEmptyLinesToShowMore++
                            }
                        }
                    }

                    if (notEmptyLinesToShowMore < 2) {
                        shouldShowReadMore = false
                    }
                }

                if(shouldShowReadMore){
                    eventAbout.maxLines = lineToEnd
                    eventReadMore.visibility = View.VISIBLE
                } else{
                    checkAndShowAboutRv()
                }
            }
        }
    }

    private fun checkAndShowAboutRv(){
        if(eventAboutSize > 0){
            eventAboutRv.visibility = View.VISIBLE
        }
    }

    private fun dayToString(day: Int): String{
        val s = when(day){
            1, 21, 31 -> "st"
            2, 22 -> "nd"
            3, 23 -> "rd"
            else -> "th"
        }

        return day.toString() + s
    }

    //TODO one interval or multiple?
    override fun showIntervals(data: List<Place.Interval>) {
        intervalsAdapter = EventIntervalAdapter(data, intervalHandler)

        eventIntervalsRv.adapter = intervalsAdapter

        eventIntervalsRv.layoutManager = LinearLayoutManager(eventIntervalsRv.context, RecyclerView.VERTICAL, false)
        eventIntervalsRv.addItemDecoration(MarginItemDecorator(eventIntervalsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), vertical = true))
    }

    override fun setSelectedDayItem(position: Int) {
        daysAdapter?.setSelectedItem(position)
    }

    override fun setSelectedIntervalItem(position: Int) {
        intervalsAdapter?.setSelectedItem(position)
    }

    override fun showProgress() {
        eventIntervalsRv.visibility = View.GONE
        eventProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        eventIntervalsRv.visibility = View.GONE
        eventProgress.visibility = View.VISIBLE
    }

    override fun hideBookingProgress() {
        (activity as EventActivity).hideBookingProgress()
    }

    override fun setSelectedPlaceItem(index: Int) {
        placesAdapter?.setSelectedItem(index)
    }

    override fun updateRestaurantName(placename: String) {
        //TODO set text in rv item "Restaurant: "
    }

    override fun updateMonthName(calendar: Calendar) {
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        eventBookingMonth.text = getString(R.string.calendar_format, month, calendar.get(Calendar.YEAR))

        val d = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).capitalize()
        val m = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).capitalize()

        (activity as EventActivity).updateDateLabel(d +", " + m +" " + dayToString(calendar.get(Calendar.DAY_OF_MONTH)))
    }

    private fun getEvent() = arguments?.getParcelable(EXTRA_EVENT) as Event
}