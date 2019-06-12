package com.square.android.presentation.presenter.passEligible

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.passEligible.PassEligibleView

@InjectViewState
class PassEligiblePresenter: BasePresenter<PassEligibleView>(){

    fun pay(){
        viewState.showProgress()

//        repository.passPay().await()

        //TODO uncomment? or replace with onBackPressed
//        router.exit()
    }
}