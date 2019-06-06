package com.square.android.presentation.presenter.jobDetails

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Job
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.jobDetails.JobDetailsView

@InjectViewState
class JobDetailsPresenter(val jobId: Long): BasePresenter<JobDetailsView>(){

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


    fun exit(){
        router.exit()
    }


    fun participateClicked(){

    }

}
