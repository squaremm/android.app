package com.square.android.ui.fragment.eventPlace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.eventPlace.EventPlacePresenter
import com.square.android.presentation.view.eventPlace.EventPlaceView
import com.square.android.ui.activity.event.EXTRA_EVENT_PLACE
import com.square.android.ui.activity.event.EventActivity
import com.square.android.ui.activity.place.IntervalMatchParentAdapter
import com.square.android.ui.activity.place.OfferDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.places.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_event_place.*
import org.jetbrains.anko.bundleOf

class EventPlaceFragment: BaseFragment(), EventPlaceView {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(place: Place): EventPlaceFragment {
            val fragment = EventPlaceFragment()

            //TODO should be just id?
            val args = bundleOf(EXTRA_EVENT_PLACE to place)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: EventPlacePresenter

    @ProvidePresenter
    fun providePresenter() = EventPlacePresenter(getPlace())

    private var dialog: OfferDialog? = null

    private var intervalsAdapter: IntervalMatchParentAdapter? = null

    private var offerAdapter: EventPlaceOfferAdapter? = null

    private var decorationAdded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventPlaceOffersSelectBtn.setOnClickListener {
            presenter.selectClicked()

            (activity as EventActivity).backToEvent()
            (activity as EventActivity).setIsPlaceFragment(false)
        }
    }

    override fun showData(place: Place, offers: List<OfferInfo>) {
        (activity as EventActivity).showPlaceData(place.name, place.mainImage ?: (place.photos?.firstOrNull() ?: ""),place.address, place.location.latLng())
        (activity as EventActivity).setIsPlaceFragment(true)

        if(!offers.isNullOrEmpty()){
            eventPlaceOffersCl.visibility = View.VISIBLE

            offerAdapter = EventPlaceOfferAdapter(offers, object: EventPlaceOfferAdapter.Handler {
                override fun itemClicked(position: Int) {
                    presenter.offersItemClicked(position)

                    offerAdapter?.itemClicked(position)
                }
                override fun itemLongClicked(position: Int) {
                    presenter.offersItemLongClicked(position, place)
                }
            })

            eventPlaceOffersRv.layoutManager = GridLayoutManager(activity!!, 3)
            eventPlaceOffersRv.adapter = offerAdapter
            eventPlaceOffersRv.addItemDecoration(GridItemDecoration(3,eventPlaceOffersRv.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(), false))
        }
    }

    override fun showOfferDialog(offer: OfferInfo, place: Place?) {
        dialog = OfferDialog(activity!!, false)
        dialog!!.show(offer, place, dialogHandler)
        (activity as EventActivity).setOfferDialogShowing(true)
    }

    var dialogHandler = object : OfferDialog.Handler{
        override fun dialogCancelled() {
            (activity as EventActivity).setOfferDialogShowing(false)
        }
    }

    override fun showIntervals(data: List<Place.Interval>) {
        intervalsAdapter = IntervalMatchParentAdapter(data, intervalHandler)

        eventPlaceIntervalsRv.layoutManager = GridLayoutManager(activity!!, 2)
        eventPlaceIntervalsRv.adapter = intervalsAdapter

        if(!decorationAdded){
            decorationAdded = true
            eventPlaceIntervalsRv.addItemDecoration(GridItemDecoration(2,eventPlaceIntervalsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
        }
    }

    private var intervalHandler = object : IntervalMatchParentAdapter.Handler{
        override fun itemClicked(position: Int, text: String, offers: List<Long>) {
            presenter.intervalItemClicked(position)
            eventPlaceOffersSelectBtn.isEnabled = true
        }
    }

    override fun setSelectedIntervalItem(position: Int) {
        intervalsAdapter?.setSelectedItem(position)
    }

    private fun getPlace() = arguments?.getParcelable(EXTRA_EVENT_PLACE) as Place
}