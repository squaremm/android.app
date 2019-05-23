package com.square.android.presentation.presenter

import android.text.TextUtils
import com.arellomobile.mvp.MvpPresenter
import com.square.android.SCREENS
import com.square.android.data.Repository
import com.square.android.data.network.errorMessage
import com.square.android.presentation.view.BaseView
import com.square.android.presentation.view.ProgressView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import ru.terrakok.cicerone.Router
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.square.android.ui.activity.noConnection.NoConnectionClosedEvent

abstract class BasePresenter<V : BaseView> : MvpPresenter<V>(), KoinComponent {
    val repository: Repository by inject()

    protected val router: Router by inject()

    private val eventBus: EventBus = EventBus.getDefault()

    private var allowNoConnectionScreen = true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTutorialLoadedEvent(event: NoConnectionClosedEvent) {
        allowNoConnectionScreen = true
    }

    private val defaultCatch: suspend CoroutineScope.(Throwable) -> Unit = {

        if((it is  UnknownHostException || it is SocketTimeoutException || it is ConnectException)){
                if(allowNoConnectionScreen){
                    allowNoConnectionScreen = false
                    router.navigateTo(SCREENS.NO_CONNECTION)
                }
        } else{
            if(!TextUtils.isEmpty(it.errorMessage)){
                viewState.showMessage(it.errorMessage)
            }
        }

        (viewState as? ProgressView)?.hideProgress()
    }

    protected fun launch(tryBlock: suspend CoroutineScope.() -> Unit,
                         catchBlock: suspend CoroutineScope.(Throwable) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                tryBlock()
            } catch (e: Throwable) {
                catchBlock(e)
            }
        }
    }

    protected fun launch(tryBlock: suspend CoroutineScope.() -> Unit) {
        launch(tryBlock, defaultCatch)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        if(!eventBus.isRegistered(this)){
            eventBus.register(this)
        }
    }

    override fun onDestroy() {
        if(eventBus.isRegistered(this)){
            eventBus.unregister(this)
        }
        super.onDestroy()
    }

}