package com.square.android.presentation.view.jobDetails

import com.square.android.data.pojo.Job
import com.square.android.presentation.view.BaseView

interface JobDetailsView : BaseView {
    fun showData(job: Job)
}