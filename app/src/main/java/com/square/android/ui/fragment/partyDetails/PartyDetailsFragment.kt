package com.square.android.ui.fragment.partyDetails

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
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.extensions.loadImageForIcon
import com.square.android.presentation.presenter.partyDetails.PartyDetailsPresenter
import com.square.android.presentation.view.partyDetails.PartyDetailsView
import com.square.android.ui.activity.party.EXTRA_PARTY
import com.square.android.ui.activity.party.PartyActivity
import com.square.android.ui.activity.place.AboutAdapter
import com.square.android.ui.activity.place.DaysAdapter
import com.square.android.ui.activity.place.OfferAdapter
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import com.square.android.ui.fragment.places.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_party_details.*
import org.jetbrains.anko.bundleOf
import java.util.*

class PartyDetailsFragment: BaseFragment(), PartyDetailsView{

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(party: Place): PartyDetailsFragment {
            val fragment = PartyDetailsFragment()

            val args = bundleOf(EXTRA_PARTY to party)
            fragment.arguments = args

            return fragment
        }
    }

    var partyAboutSize = 0

    private var adapter: AboutAdapter? = null

    private var daysAdapter: DaysAdapter? = null

    private var offersAdapter: OfferAdapter? = null

    private var intervalsAdapter: PartyIntervalAdapter? = null

    private var placesAdapter: PartyPlaceAdapter? = null

    @InjectPresenter
    lateinit var presenter: PartyDetailsPresenter

    @ProvidePresenter
    fun providePresenter() = PartyDetailsPresenter(getParty())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_party_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        partyReadMore.setOnClickListener {
            partyReadMore.visibility = View.GONE
            partyAbout.maxLines = Integer.MAX_VALUE
            checkAndShowAboutRv()
        }
    }

    override fun showData(party: Place, offers: List<OfferInfo>, calendar: Calendar, typeImage: String?, places: List<Place>) {

        //TODO waiting for API
        //TODO get detail from party
//        (activity as PartyActivity).setPartyBookingText(detailName)

        typeImage?.let { partyAboutImage.loadImageForIcon(it) }

        partyAbout.text = party.description

        //TODO waiting for API
        //TODO delete this and get data from party
        val aboutItems = listOf("www", "insta")

        partyAboutSize = aboutItems.size

        adapter = AboutAdapter(aboutItems)
        partyAboutRv.adapter = adapter
        partyAboutRv.layoutManager = LinearLayoutManager(partyAboutRv.context, RecyclerView.HORIZONTAL, false)
        partyAboutRv.addItemDecoration(MarginItemDecorator(partyAboutRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), vertical = false))

        //TODO waiting for API
        //TODO delete this and get data from party
//        val dressCode: String? = ""
//        val minimumTip: String? = ""
//
//        if(!TextUtils.isEmpty(dressCode) || !TextUtils.isEmpty(minimumTip)){
//            partyRequirementsCl.visibility = View.VISIBLE
//
//            if(!TextUtils.isEmpty(dressCode) && !TextUtils.isEmpty(minimumTip)){
//                partyTipValue.text = minimumTip
//                partyTipContainer.visibility = View.VISIBLE
//
//                partyDressCodeValue.text = dressCode
//            } else {
//                minimumTip?.let {
//                    partyDressCodeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.r_discount))
//                    partyDressCodeName.text = getString(R.string.minimal_tip)
//                    partyDressCodeValue.text = it
//
//                } ?: run{
//                    partyDressCodeValue.text = dressCode
//                }
//            }
//        }

        if(!offers.isNullOrEmpty()){
            //TODO waiting for API
            //TODO alpha 0.3 te, ktorych klub nie oferuje

            partyrOffersLabel.visibility = View.VISIBLE
            partyOffersRv.visibility = View.VISIBLE

            offersAdapter = OfferAdapter(offers, null, true)
            partyOffersRv.adapter = offersAdapter

            partyOffersRv.layoutManager = GridLayoutManager(activity!!, 3)
            partyOffersRv.adapter = offersAdapter
            partyOffersRv.addItemDecoration(GridItemDecoration(3,partyOffersRv.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(), false))
        }

        if(!places.isNullOrEmpty()){
            partyDinnerInfoLabel.visibility = View.VISIBLE
            partyDinnerInfoRv.visibility = View.VISIBLE

            placesAdapter = PartyPlaceAdapter(places, placeHandler)
            partyDinnerInfoRv.adapter = offersAdapter
            partyDinnerInfoRv.layoutManager = LinearLayoutManager(partyDinnerInfoRv.context, RecyclerView.HORIZONTAL, false)
            partyDinnerInfoRv.addItemDecoration(MarginItemDecorator(partyAboutRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), vertical = false))
        }

        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        partyBookingMonth.text = getString(R.string.calendar_format, month, calendar.get(Calendar.YEAR))

        val d = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).capitalize()
        val m =  calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).capitalize()

        (activity as PartyActivity).updateDateLabel(d +", " + m +" " + dayToString(calendar.get(Calendar.DAY_OF_MONTH)))

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
        partyBookingCalendar.adapter = daysAdapter
        daysAdapter!!.selectedItemPosition = 0
        daysAdapter!!.notifyItemChanged(0, DaysAdapter.SelectedPayload)

        partyAbout.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onAboutLoaded()
                partyAbout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    var placeHandler = object : PartyPlaceAdapter.Handler{
        override fun itemClicked(position: Int) {
            presenter.placeItemClicked(position)
        }
    }

    var intervalHandler = object : PartyIntervalAdapter.Handler{
        override fun itemClicked(position: Int, enabled: Boolean) {
            if(enabled){
                presenter.intervalItemClicked(position)
                (activity as PartyActivity).setTimeframeSelected(true)
            }
        }
    }

    var dayHandler = object : DaysAdapter.Handler{
        override fun itemClicked(position: Int) {
            presenter.dayItemClicked(position)

            (activity as PartyActivity).disableButton()
        }
    }

    private fun onAboutLoaded(){
        var startOffset: Int
        var endOffset: Int
        var lineToEnd = 3
        val maxLines = 3
        var isLineSelected = false
        var notEmptyLinesToShowMore = 0

        if (!TextUtils.isEmpty(partyAbout.text)) {
            if (partyAbout.layout != null) {
                var shouldShowReadMore = true

                if (partyAbout.layout.lineCount <= maxLines) {
                    shouldShowReadMore = false
                } else {

                    for(i in 2 until partyAbout.layout.lineCount){
                        startOffset = partyAbout.layout.getLineStart(i)
                        endOffset = partyAbout.layout.getLineEnd(i)
                        if (!TextUtils.isEmpty((partyAbout.layout.text.subSequence(startOffset, endOffset)).toString().trim())) {
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
                    partyAbout.maxLines = lineToEnd
                    partyReadMore.visibility = View.VISIBLE
                } else{
                    checkAndShowAboutRv()
                }
            }
        }
    }

    private fun checkAndShowAboutRv(){
        if(partyAboutSize > 0){
            partyAboutRv.visibility = View.VISIBLE
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
        intervalsAdapter = PartyIntervalAdapter(data, intervalHandler)

        partyIntervalsRv.adapter = intervalsAdapter

        partyIntervalsRv.layoutManager = LinearLayoutManager(partyIntervalsRv.context, RecyclerView.VERTICAL, false)
        partyIntervalsRv.addItemDecoration(MarginItemDecorator(partyIntervalsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), vertical = true))
    }

    override fun setSelectedDayItem(position: Int) {
        daysAdapter?.setSelectedItem(position)
    }

    override fun setSelectedIntervalItem(position: Int) {
        intervalsAdapter?.setSelectedItem(position)
    }

    override fun showProgress() {
        partyIntervalsRv.visibility = View.GONE
        partyProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        partyIntervalsRv.visibility = View.GONE
        partyProgress.visibility = View.VISIBLE
    }

    override fun hideBookingProgress() {
        (activity as PartyActivity).hideBookingProgress()
    }

    override fun setSelectedPlaceItem(index: Int) {
        placesAdapter?.setSelectedItem(index)
    }

    override fun updateRestaurantName(placename: String) {
        //TODO set text in rv item "Restaurant: "
    }

    override fun updateMonthName(calendar: Calendar) {
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        partyBookingMonth.text = getString(R.string.calendar_format, month, calendar.get(Calendar.YEAR))

        val d = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).capitalize()
        val m = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).capitalize()

        (activity as PartyActivity).updateDateLabel(d +", " + m +" " + dayToString(calendar.get(Calendar.DAY_OF_MONTH)))
    }

    private fun getParty() = arguments?.getParcelable(EXTRA_PARTY) as Place
}