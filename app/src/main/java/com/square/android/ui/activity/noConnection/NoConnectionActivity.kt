package com.square.android.ui.activity.noConnection

import android.os.Bundle
import com.square.android.R
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator

class NoConnectionClosedEvent()

class NoConnectionActivity: BaseActivity() {

    private val eventBus: EventBus by inject()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connection)
    }

    override fun onDestroy() {
        eventBus.post(NoConnectionClosedEvent())
        super.onDestroy()
    }
}