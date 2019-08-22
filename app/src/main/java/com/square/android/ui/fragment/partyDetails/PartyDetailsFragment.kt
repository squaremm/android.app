package com.square.android.ui.fragment.partyDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.partyDetails.PartyDetailsPresenter
import com.square.android.presentation.view.partyDetails.PartyDetailsView
import com.square.android.ui.activity.party.EXTRA_PARTY
import com.square.android.ui.activity.party.PartyActivity
import com.square.android.ui.fragment.BaseFragment
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



    }



    override fun hideBookingProgress() {
        (activity as PartyActivity).hideBookingProgress()
    }


    override fun setSelectedPlaceItem(id: Long) {

    }

    override fun updateRestaurantName(placename: String) {

    }

    override fun updateMonthName(calendar: Calendar) {


      //...
      //  (activity as PartyActivity).updateDateLabel(...)
    }



    private fun getParty() = arguments?.getParcelable(EXTRA_PARTY) as Place
}