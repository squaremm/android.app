package com.square.android.ui.fragment.partyPlace

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
import com.square.android.presentation.presenter.partyPlace.PartyPlacePresenter
import com.square.android.presentation.view.partyPlace.PartyPlaceView
import com.square.android.ui.activity.party.EXTRA_PARTY_PLACE
import com.square.android.ui.activity.party.PartyActivity
import com.square.android.ui.activity.place.IntervalAdapter
import com.square.android.ui.activity.place.OfferDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.places.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_party_place.*
import org.jetbrains.anko.bundleOf

class PartyPlaceFragment: BaseFragment(), PartyPlaceView {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(place: Place): PartyPlaceFragment {
            val fragment = PartyPlaceFragment()

            //TODO should be just id?
            val args = bundleOf(EXTRA_PARTY_PLACE to place)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: PartyPlacePresenter

    @ProvidePresenter
    fun providePresenter() = PartyPlacePresenter(getPlace())

    private var dialog: OfferDialog? = null

    private var intervalsAdapter: IntervalAdapter? = null

    private var offerAdapter: PartyPlaceOfferAdapter? = null

    private var decorationAdded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_party_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        partyPlaceOffersSelectBtn.setOnClickListener {
            presenter.selectClicked()

            (activity as PartyActivity).backToParty()
            (activity as PartyActivity).setIsPlaceFragment(false)
        }
    }

    override fun showData(place: Place, offers: List<OfferInfo>) {
        (activity as PartyActivity).showPlaceData(place.name, place.mainImage ?: (place.photos?.firstOrNull() ?: ""),place.address, place.location.latLng())
        (activity as PartyActivity).setIsPlaceFragment(true)

        if(!offers.isNullOrEmpty()){
            partyPlaceOffersCl.visibility = View.VISIBLE

            offerAdapter = PartyPlaceOfferAdapter(offers, object: PartyPlaceOfferAdapter.Handler {
                override fun itemClicked(position: Int) {
                    presenter.offersItemClicked(position)

                    offerAdapter?.itemClicked(position)
                }
                override fun itemLongClicked(position: Int) {
                    presenter.offersItemLongClicked(position, place)
                }
            })

            partyPlaceOffersRv.layoutManager = GridLayoutManager(activity!!, 3)
            partyPlaceOffersRv.adapter = offerAdapter
            partyPlaceOffersRv.addItemDecoration(GridItemDecoration(3,partyPlaceOffersRv.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(), false))
        }
    }

    override fun showOfferDialog(offer: OfferInfo, place: Place?) {
        dialog = OfferDialog(activity!!, false)
        dialog!!.show(offer, place, dialogHandler)
        (activity as PartyActivity).setOfferDialogShowing(true)
    }

    var dialogHandler = object : OfferDialog.Handler{
        override fun dialogCancelled() {
            (activity as PartyActivity).setOfferDialogShowing(false)
        }
    }

    override fun showIntervals(data: List<Place.Interval>) {
        intervalsAdapter = IntervalAdapter(data, intervalHandler)

        partyPlaceIntervalsRv.layoutManager = GridLayoutManager(activity!!, 2)
        partyPlaceIntervalsRv.adapter = intervalsAdapter

        if(!decorationAdded){
            decorationAdded = true
            partyPlaceIntervalsRv.addItemDecoration(GridItemDecoration(2,partyPlaceIntervalsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
        }
    }

    private var intervalHandler = object : IntervalAdapter.Handler{
        override fun itemClicked(position: Int, text: String, offers: List<Long>) {
            presenter.intervalItemClicked(position)
            partyPlaceOffersSelectBtn.isEnabled = true
        }
    }

    override fun setSelectedIntervalItem(position: Int) {
        intervalsAdapter?.setSelectedItem(position)
    }

    private fun getPlace() = arguments?.getParcelable(EXTRA_PARTY_PLACE) as Place
}