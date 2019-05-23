package com.square.android.ui.activity.noConnection

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.noConnection.NoConnectionPresenter
import com.square.android.presentation.view.noConnection.NoConnectionView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_no_connection.*
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator

class NoConnectionClosedEvent()

class NoConnectionActivity: BaseActivity(), NoConnectionView {

    private val eventBus: EventBus by inject()

    @InjectPresenter
    lateinit var presenter: NoConnectionPresenter

    @ProvidePresenter
    fun providePresenter() = NoConnectionPresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connection)

        noConnectionTryAgain.setOnClickListener{
            presenter.checkConnection(true)
            progressVisible(true)
        }

        presenter.shouldTimerBeWorking(true)
    }

    override fun finishAc() {
       super.onBackPressed()
    }

    override fun progressVisible(visible: Boolean) {
        if(visible){
            noConnectionTryAgain.visibility = View.GONE
            noConnectionProgress.visibility = View.VISIBLE
        } else{
            noConnectionProgress.visibility = View.GONE
            noConnectionTryAgain.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        eventBus.post(NoConnectionClosedEvent())
        super.onDestroy()
    }

    override fun onBackPressed() {

    }

    override fun onStop() {
        presenter.shouldTimerBeWorking(false)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        presenter.shouldTimerBeWorking(true)
    }

}