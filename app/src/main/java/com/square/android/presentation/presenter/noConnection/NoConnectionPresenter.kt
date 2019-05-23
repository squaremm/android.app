package com.square.android.presentation.presenter.noConnection

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.noConnection.NoConnectionView
import java.util.*


@InjectViewState
class NoConnectionPresenter: BasePresenter<NoConnectionView>(){

    private var timer: Timer? = null

    fun checkConnection(hideProgress: Boolean = false){
        launch({
            val user = repository.getCurrentUser().await()

            viewState.finishAc()

        }, { error ->
            if(hideProgress){
                viewState.progressVisible(false)
            }
        })
    }

    fun shouldTimerBeWorking(shouldBeWorking: Boolean){
        timer?.cancel()
        timer?.purge()
        timer = Timer()

        if(shouldBeWorking){
            checkConnection()
            checkContinuously()
        }
    }

    fun checkContinuously(){
        timer?.schedule(object : TimerTask() {
            override fun run() {
                checkConnection()
                checkContinuously()
            }
        }, 20000)
    }

    override fun onDestroy() {
        timer?.cancel()
        timer?.purge()
        super.onDestroy()
    }
}