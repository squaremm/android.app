package com.square.android.presentation.presenter.driver

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.driver.DriverView
import com.square.android.ui.activity.driver.DriverExtras

@InjectViewState
class DriverPresenter(val extras: DriverExtras): BasePresenter<DriverView>(){


    fun finish(){
        router.exit()
    }

}