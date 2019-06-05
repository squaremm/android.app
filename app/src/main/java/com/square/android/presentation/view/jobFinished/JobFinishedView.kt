package com.square.android.presentation.view.jobFinished

import com.square.android.data.pojo.Job
import com.square.android.presentation.view.BaseView

interface JobFinishedView : BaseView {
    fun showData(job: Job)
}