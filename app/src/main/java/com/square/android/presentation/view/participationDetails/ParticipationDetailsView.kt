package com.square.android.presentation.view.participationDetails

import com.square.android.data.pojo.Participation
import com.square.android.presentation.view.BaseView

interface ParticipationDetailsView : BaseView {
    fun showData(participation: Participation)
}