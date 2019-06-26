package com.square.android.presentation.presenter

import android.text.TextUtils
import com.arellomobile.mvp.MvpPresenter
import com.google.gson.Gson
import com.square.android.GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME
import com.square.android.SCREENS
import com.square.android.data.BillingRepository
import com.square.android.data.Repository
import com.square.android.data.network.errorMessage
import com.square.android.data.pojo.BillingSubscription
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.ProfileInfo
import com.square.android.data.pojo.TokenInfo
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
import java.util.*

abstract class BasePresenter<V : BaseView> : MvpPresenter<V>(), KoinComponent {
    val repository: Repository by inject()

    val billingRepository: BillingRepository by inject()

    protected val router: Router by inject()

    private val eventBus: EventBus = EventBus.getDefault()

    private var allowNoConnectionScreen = true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNoConnectionClosedEvent(event: NoConnectionClosedEvent) {
        allowNoConnectionScreen = true
        checkSubscriptions()
    }

    fun checkSubscriptions() = launch {
        repository.clearUserEntitlements()

        val isPaymentRequired = repository.getUserInfo().isPaymentRequired

        val subscriptions: MutableList<BillingSubscription> = mutableListOf()

        if(!isPaymentRequired){
            repository.grantAllUserEntitlements()
        } else{

            val userId = repository.getUserId()

            if(userId != 0L){

            val billings: List<BillingTokenInfo> = repository.getPaymentTokens(userId).await()

            for (billing in billings) {
                val data = billingRepository.getSubscription(billing.subscriptionId!!, billing.token!!).await()
                data.subscriptionId = billing.subscriptionId
                data.token = billing.token

                subscriptions.add(data)
            }

            //TODO change to actual time from API
            val actualTimeInMillis: Long = Calendar.getInstance().timeInMillis

            //////// check for every subscriptionId in app products ///////////
            val perWeekValidSub = subscriptions.filter { it.subscriptionId == SUBSCRIPTION_PER_WEEK_NAME && it.paymentState != 0}.sortedByDescending {it.expiryTimeMillis}.firstOrNull()

            perWeekValidSub?.let {
                val validExpiry = (it.expiryTimeMillis - actualTimeInMillis) > 1000

                grantEntitlement(validExpiry, BillingTokenInfo().apply { subscriptionId = it.subscriptionId; token = it.token },
                        it.acknowledgementState == 0)
            }
            //////////////////////////////////////////////////////////////////

            }
        }
    }

    private fun grantEntitlement(validExpiry: Boolean, billingTokenInfo: BillingTokenInfo, acknowledgementRequired: Boolean){
        if(validExpiry){
            repository.setUserEntitlement(billingTokenInfo.subscriptionId!!, true)
        }

        if(acknowledgementRequired){
            launch {
                billingRepository.acknowledgeSubscription(billingTokenInfo.subscriptionId!!, billingTokenInfo.token!!, TokenInfo().apply { payload = "" }).await()
            }
        }
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


    fun saveState(profileInfo: ProfileInfo, fragmentNumber: Int){
        val gson = Gson()
        val stringProfileInfo = gson.toJson(profileInfo)

        repository.saveProfileInfo(stringProfileInfo,fragmentNumber)
    }
}