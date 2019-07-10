package com.square.android.ui.fragment.pickUpSpot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.CampaignInterval
import com.square.android.presentation.presenter.pickUpSpot.PickUpSpotPresenter
import com.square.android.presentation.view.pickUpSpot.PickUpSpotView
import com.square.android.ui.activity.campaignDetails.EXTRA_CAMPAIGN_ID
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_pick_up_spot.*
import org.jetbrains.anko.bundleOf

class PickUpSpotFragment: BaseFragment(), PickUpSpotView{

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(campaignId: Long): PickUpSpotFragment {
            val fragment = PickUpSpotFragment()

            val args = bundleOf(EXTRA_CAMPAIGN_ID to campaignId)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: PickUpSpotPresenter

    @ProvidePresenter
    fun providePresenter(): PickUpSpotPresenter = PickUpSpotPresenter(getCampaignId())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pick_up_spot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickupContinue.setOnClickListener { presenter.continueClicked() }
    }

    override fun dataLoaded(spots: List<CampaignInterval.Location>) {
        pickupMainProgress.visibility = View.GONE
        pickupSpotsRv.visibility = View.VISIBLE

        //TODO create, init and set adapter for spots(pickupSpotsRv -> item_spot)
    }

    private fun getCampaignId() = arguments?.getLong(EXTRA_CAMPAIGN_ID, 0) as Long
}