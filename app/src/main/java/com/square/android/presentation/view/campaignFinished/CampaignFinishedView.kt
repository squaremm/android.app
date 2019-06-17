package com.square.android.presentation.view.campaignFinished

import com.square.android.data.pojo.OldCampaign
import com.square.android.presentation.view.BaseView

interface CampaignFinishedView : BaseView {
    fun showData(oldCampaign: OldCampaign)
}