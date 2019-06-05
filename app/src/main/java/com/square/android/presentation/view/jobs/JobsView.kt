package com.square.android.presentation.view.jobs

import com.square.android.data.pojo.Job
import com.square.android.presentation.view.ProgressView

interface JobsView : ProgressView {
    fun showJobs(data: List<Job>)
}
