package com.square.android.ui.activity.campaignDetails

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.campaignDetails.CampaignDetailsPresenter
import com.square.android.presentation.view.campaignDetails.CampaignDetailsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_campaign_details.*
import ru.terrakok.cicerone.Navigator
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.square.android.extensions.loadImage
import com.square.android.ui.activity.campaigns.CAMPAIGN_EXTRA_ID
import kotlinx.android.synthetic.main.activity_place_detail.*

class CampaignDetailsActivity: BaseActivity(), CampaignDetailsView {

    @InjectPresenter
    lateinit var presenter: CampaignDetailsPresenter

    @ProvidePresenter
    fun providePresenter() = CampaignDetailsPresenter(getCampaignId())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private val PERCENTAGE_TO_COLOR = 65
    private var mMaxScrollSize: Int = 0
    private var mIsElementWhite: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_details)

        setSupportActionBar(campaignDetailsToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        placeDetailAppbar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {

                if (mMaxScrollSize == 0)
                    mMaxScrollSize = placeDetailAppbar.totalScrollRange

                val currentScrollPercentage = Math.abs(p1) * 100 / mMaxScrollSize

                if (currentScrollPercentage >= PERCENTAGE_TO_COLOR) {
                    if (!mIsElementWhite) {
                        mIsElementWhite = true
                        campaignDetailsName.setTextColor(ContextCompat.getColor(campaignDetailsName.context, R.color.nice_pink))
                        campaignDetailsBack.imageTintList = ColorStateList.valueOf(Color.BLACK)
                        placeDetailToolbar.setBackgroundColor(ContextCompat.getColor(placeDetailToolbar.context, android.R.color.transparent))
                        campaignDetailsType.visibility = View.INVISIBLE
                    }
                }

                if (currentScrollPercentage < PERCENTAGE_TO_COLOR) {
                    if (mIsElementWhite) {
                        mIsElementWhite = false
                        campaignDetailsName.setTextColor(ContextCompat.getColor(campaignDetailsName.context, R.color.white))
                        campaignDetailsBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(campaignDetailsBack.context, R.color.white))
                        placeDetailToolbar.setBackgroundColor(ContextCompat.getColor(placeDetailToolbar.context, R.color.black_trans_30))
                        campaignDetailsType.visibility = View.VISIBLE
                    }
                }
            }
        })

        campaignDetailsBack.setOnClickListener {presenter.exit()}
        campaignDetailsParticipate.setOnClickListener {presenter.participateClicked()}
    }

    override fun showData(campaign: Campaign) {

        when(campaign.type){
            1 ->{
                campaignDetailsType.text = campaignDetailsType.context.getString(R.string.gifting_campaign)
                campaignDetailsType.background = ContextCompat.getDrawable(campaignDetailsType.context, R.drawable.round_bg_pink_pinkish)
            }
            2 ->{
                campaignDetailsType.text = campaignDetailsType.context.getString(R.string.influencer_campaign)
                campaignDetailsType.background = ContextCompat.getDrawable(campaignDetailsType.context, R.drawable.round_bg_purple_purpleish)
            }
        }

        campaign.mainImage?.let {campaignDetailsImage.loadImage(it)}

        campaignDetailsName.text = campaign.name

//        campaignDetailsDescription.text = campaign.description
//        campaignDetailsRewards.text = campaign.rewards
//        campaign.rewardImage?.let {campaignDetailsRewardImage.loadImage(it)}
//        campaignDetailsDaysLeft.text = campaign.daysLeft
//        campaignDetailsTaskLeft.text = campaign.taskname
//        campaignDetailsTaskRight.text = campaign.tasks
//        campaignDetailsHowItWorks.text = campaign.howItWorks
    }

    private fun getCampaignId() = intent.getLongExtra(CAMPAIGN_EXTRA_ID, 0)
}
