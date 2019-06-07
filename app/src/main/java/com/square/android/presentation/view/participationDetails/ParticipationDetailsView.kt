package com.square.android.presentation.view.participationDetails

import com.square.android.data.pojo.Participation
import com.square.android.presentation.view.BaseView
import com.square.android.presentation.view.ProgressView

interface ParticipationDetailsView : ProgressView {
    fun showData(participation: Participation)
}