package com.square.android.presentation.presenter.jobs

import com.square.android.SCREENS
import com.square.android.data.pojo.Job
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.jobs.JobsView

class JobsPresenter: BasePresenter<JobsView>() {

    private var data: List<Job>? = null

    init {
        loadData()
    }

    private fun loadData() {
        launch {
//            viewState.showProgress()

//            data = repository.getJobs().await()
//
//            viewState.hideProgress()
//            viewState.showJobs(data!!)
        }
    }


    fun itemClicked(position: Int) {
        val id = data!![position].id

        router.navigateTo(SCREENS.JOB_DETAILS, id)
    }

}