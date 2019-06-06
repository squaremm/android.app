package com.square.android.presentation.view.campaigns

import com.square.android.data.pojo.Campaign
import com.square.android.presentation.view.ProgressView

interface CampaignsView : ProgressView {
    fun showCampaigns(data: List<Campaign>)
}
