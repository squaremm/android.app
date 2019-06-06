package com.square.android.presentation.presenter.participationDetails

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Participation
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.participationDetails.ParticipationDetailsView

@InjectViewState
class ParticipationDetailsPresenter(val participationId: Long): BasePresenter<ParticipationDetailsView>(){

    private var data: Participation? = null

    init {
        loadData()
    }

    private fun loadData() {
        launch {
            //            data = repository.getParticipation(participationId).await()
//
//            viewState.showData(data!!)
        }
    }

    fun exit(){
        router.exit()
    }

}
