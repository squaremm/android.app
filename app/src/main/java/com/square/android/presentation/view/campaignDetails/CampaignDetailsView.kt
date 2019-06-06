package com.square.android.presentation.view.campaignDetails

import com.square.android.data.pojo.Campaign
import com.square.android.presentation.view.BaseView

interface CampaignDetailsView : BaseView {
    fun showData(campaign: Campaign)
}