package com.square.android.ui.activity.campaignFinished

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Campaign
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.campaignFinished.CampaignFinishedPresenter
import com.square.android.presentation.view.campaignFinished.CampaignFinishedView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_campaign_finished.*
import ru.terrakok.cicerone.Navigator

const val CAMPAIGN_EXTRA_ID = "CAMPAIGN_EXTRA_ID"

class CampaignFinishedActivity: BaseActivity(), CampaignFinishedView {

    @InjectPresenter
    lateinit var presenter: CampaignFinishedPresenter

    @ProvidePresenter
    fun providePresenter() = CampaignFinishedPresenter(getId())

    var campaign: Campaign? = null

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_finished)

        finishedBack.setOnClickListener {onBackPressed()}
    }

    override fun showData(campaign: Campaign) {
        this.campaign = campaign

        setUpPager()

        campaign.mainImage?.let { finishedBg.loadImage(it)}

        finishedName.text = campaign.title
    }

    private fun setUpPager() {
        finishedPager.adapter = JobFinishedAdapter(supportFragmentManager, campaign)
        finishedTab.setupWithViewPager(finishedPager)
        finishedPager.offscreenPageLimit = 2
    }

    private fun getId() = intent.getLongExtra(CAMPAIGN_EXTRA_ID, 0)
}