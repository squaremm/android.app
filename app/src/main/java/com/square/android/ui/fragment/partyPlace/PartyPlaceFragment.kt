package com.square.android.ui.fragment.partyPlace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.partyPlace.PartyPlacePresenter
import com.square.android.presentation.view.partyPlace.PartyPlaceView
import com.square.android.ui.activity.party.EXTRA_PARTY_PLACE
import com.square.android.ui.fragment.BaseFragment
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_party_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun getPlace() = arguments?.getParcelable(EXTRA_PARTY_PLACE) as Place
}