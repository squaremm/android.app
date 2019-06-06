package com.square.android.presentation.view.campaignFinished

import com.square.android.data.pojo.Campaign
import com.square.android.presentation.view.BaseView

interface CampaignFinishedView : BaseView {
    fun showData(campaign: Campaign)
}