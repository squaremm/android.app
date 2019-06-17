package com.square.android.presentation.view.campaigns

import com.square.android.data.pojo.OldCampaign
import com.square.android.presentation.view.ProgressView

interface CampaignsView : ProgressView {
    fun showCampaigns(data: List<OldCampaign>)
}
