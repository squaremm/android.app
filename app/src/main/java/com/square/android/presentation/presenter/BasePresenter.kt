package com.square.android.presentation.presenter

import com.arellomobile.mvp.MvpPresenter
import com.square.android.data.Repository
import com.square.android.data.network.errorMessage
import com.square.android.presentation.view.BaseView
import com.square.android.presentation.view.ProgressView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import ru.terrakok.cicerone.Router

abstract class BasePresenter<V : BaseView> : MvpPresenter<V>(), KoinComponent {
    val repository: Repository by inject()

    protected val router: Router by inject()

    private val defaultCatch: suspend CoroutineScope.(Throwable) -> Unit = {
        viewState.showMessage(it.errorMessage)

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
}