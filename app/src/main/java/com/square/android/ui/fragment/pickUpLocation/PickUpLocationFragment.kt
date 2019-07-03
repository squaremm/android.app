package com.square.android.ui.fragment.pickUpLocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.CampaignInterval
import com.square.android.presentation.presenter.pickUpLocation.PickUpLocationPresenter
import com.square.android.presentation.view.pickUpLocation.PickUpLocationView
import com.square.android.ui.activity.campaignDetails.EXTRA_CAMPAIGN_LOCATION
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_pick_up_location.*
import org.jetbrains.anko.bundleOf
import android.content.Intent
import android.net.Uri

class PickUpLocationFragment: BaseFragment(), PickUpLocationView{

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(location: CampaignInterval.Location): PickUpLocationFragment {
            val fragment = PickUpLocationFragment()

            val args = bundleOf(EXTRA_CAMPAIGN_LOCATION to location)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: PickUpLocationPresenter

    @ProvidePresenter
    fun providePresenter(): PickUpLocationPresenter = PickUpLocationPresenter(getCampaignLocation())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pick_up_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickupLocationOpen.setOnClickListener { openMapsIntent() }
    }

    private fun openMapsIntent(){
        val uri = "http://maps.google.com/maps?q=loc: ${presenter.location.coordinates?.latitude},${presenter.location.coordinates?.longitude}"
        val intent = Intent(android.content.Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        val chooser = Intent.createChooser(intent, getString(R.string.select_an_app))
        startActivity(chooser)
    }

    override fun showData(location: CampaignInterval.Location) {
        pickupLocationAddress.text = location.getAddressString()
    }

    private fun getCampaignLocation() = arguments?.getParcelable(EXTRA_CAMPAIGN_LOCATION) as CampaignInterval.Location
}