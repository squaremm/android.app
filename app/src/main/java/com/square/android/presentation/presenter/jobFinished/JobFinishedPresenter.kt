package com.square.android.presentation.presenter.jobFinished

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Job
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.jobFinished.JobFinishedView

@InjectViewState
class JobFinishedPresenter(private val jobId: Long): BasePresenter<JobFinishedView>() {

    private var data: Job? = null

    init {
        loadData()
    }

    private fun loadData() {
        launch {
//            data = repository.getJob(jobId).await()
//
//            viewState.showData(data!!)
        }
    }
}