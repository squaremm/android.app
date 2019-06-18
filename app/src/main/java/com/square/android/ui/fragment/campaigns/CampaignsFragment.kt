package com.square.android.ui.fragment.campaigns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.CampaignInfo
import com.square.android.presentation.presenter.campaigns.CampaignsPresenter
import com.square.android.presentation.view.campaigns.CampaignsView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_campaigns.*

const val CAMPAIGN_EXTRA_ID = "CAMPAIGN_EXTRA_ID"
class CampaignsFragment: BaseFragment(), CampaignsView, CampaignsAdapter.Handler {

    @InjectPresenter
    lateinit var presenter: CampaignsPresenter

    @ProvidePresenter
    fun providePresenter() = CampaignsPresenter()

    private var adapter: CampaignsAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_campaigns, container, false)
    }

    override fun showCampaigns(data: List<CampaignInfo>) {

        adapter = CampaignsAdapter(data, this)

        campaignsList.adapter = adapter

        campaignsList.addItemDecoration(MarginItemDecorator(campaignsList.context.resources.getDimension(R.dimen.rv_item_decorator_16).toInt(),true,
                campaignsList.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(),
                campaignsList.context.resources.getDimension(R.dimen.rv_item_decorator_16).toInt()
        ))
    }

    override fun showProgress() {
        campaignsProgress.visibility = View.VISIBLE
        campaignsList.visibility = View.INVISIBLE
    }

    override fun hideProgress() {
        campaignsProgress.visibility = View.INVISIBLE
        campaignsList.visibility = View.VISIBLE
    }

    override fun itemClicked(position: Int) {
        presenter.itemClicked(position)
    }

}