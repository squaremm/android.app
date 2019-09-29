package com.square.android.presentation.presenter.driverChat

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.driverChat.DriverChatView

@InjectViewState
class DriverChatPresenter: BasePresenter<DriverChatView>(){


    init {
        loadData()
    }

    private fun loadData(){

    }

}