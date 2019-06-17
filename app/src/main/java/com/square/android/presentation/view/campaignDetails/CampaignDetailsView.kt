package com.square.android.presentation.view.campaignDetails

import com.square.android.data.pojo.OldCampaign
import com.square.android.presentation.view.BaseView

interface CampaignDetailsView : BaseView {
    fun showData(oldCampaign: OldCampaign)
    fun showThanks()
    fun showProgress()
    fun hideProgress()
}