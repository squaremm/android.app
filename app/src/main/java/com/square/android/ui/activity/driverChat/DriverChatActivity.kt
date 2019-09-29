package com.square.android.ui.activity.driverChat

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.Driver
import com.square.android.presentation.presenter.driverChat.DriverChatPresenter
import com.square.android.presentation.view.driverChat.DriverChatView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import ru.terrakok.cicerone.Navigator

class DriverChatActivity: BaseActivity(), DriverChatView{

    @InjectPresenter
    lateinit var presenter: DriverChatPresenter

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_chat)
    }


    override fun showData(items: List<Any>, driver: Driver, userId: Long) {

    }

    override fun showProgress() {

    }

    override fun hideProgress() {

    }
}