package com.square.android.ui.activity.campaigns

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.campaigns.CampaignsPresenter
import com.square.android.presentation.view.campaigns.CampaignsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.activity_campaigns.*
import ru.terrakok.cicerone.Navigator

const val CAMPAIGN_EXTRA_ID = "CAMPAIGN_EXTRA_ID"
class CampaignsActivity: BaseActivity(), CampaignsView, CampaignsAdapter.Handler {

    @InjectPresenter
    lateinit var presenter: CampaignsPresenter

    @ProvidePresenter
    fun providePresenter() = CampaignsPresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private var adapter: CampaignsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_campaigns)
    }

    override fun showCampaigns(data: List<Campaign>) {

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