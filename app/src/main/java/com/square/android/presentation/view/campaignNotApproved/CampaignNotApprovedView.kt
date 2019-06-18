package com.square.android.presentation.view.campaignNotApproved

import com.square.android.data.pojo.Campaign
import com.square.android.presentation.view.BaseView

interface CampaignNotApprovedView : BaseView {
    fun showData(campaign: Campaign)
    fun showThanks()
    fun showProgress()
    fun hideProgress()
}