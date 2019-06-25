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

abstract class BasePresenter<V : BaseView> : MvpPresenter<V>(), KoinComponent {
    val repository: Repository by inject()

    private val billingRepository: BillingRepository by inject()

    protected val router: Router by inject()

    private val eventBus: EventBus = EventBus.getDefault()

    private var allowNoConnectionScreen = true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNoConnectionClosedEvent(event: NoConnectionClosedEvent) {
        allowNoConnectionScreen = true
        checkSubscriptions()
    }

    fun checkSubscriptions() = launch {

        //TODO get user's isPaymentRequired
        val isPaymentRequired = true

        val subscriptions: MutableList<BillingSubscription> = mutableListOf()

        if(!isPaymentRequired){
            grantAllEntitlements()
        } else{

            //TODO clear all locally stored entitlements?


            //TODO get sub tokens from API and UNCOMMENT
//            val billings: List<BillingTokenInfo> = repository.getSubTokens().await()
            //TODO DELETE
            val billings: List<BillingTokenInfo> = mutableListOf()

            for (billing in billings) {
                val data = billingRepository.getSubscription(billing.subscriptionId!!, billing.token!!).await()
                data.subscriptionId = billing.subscriptionId
                data.token = billing.token

                subscriptions.add(data)
            }

            //TODO get actual time from API
            val actualTimeInMillis: Long = 4032656546


            //////// check for every subscriptionId in app products ///////////
            val perWeekValidSub = subscriptions.filter { it.subscriptionId == SUBSCRIPTION_PER_WEEK_NAME && it.paymentState != 0}.sortedByDescending {it.expiryTimeMillis}.firstOrNull()

            perWeekValidSub?.let {
                if( (it.expiryTimeMillis - actualTimeInMillis ) > 1000 ){
                    grantEntitlement(BillingTokenInfo().apply { subscriptionId = it.subscriptionId; token = it.token },
                            it.acknowledgementState == 0)
                }
            }
            ///////////////////////////////////////////////////////////

        }
    }

    private fun grantAllEntitlements(){
        //TODO grant all entitlements to user
    }

    private fun grantEntitlement(billingTokenInfo: BillingTokenInfo, needsAcknowledgement: Boolean){
        //TODO grant an entitlement to user by billingTokenInfo.subscriptionId

        if(needsAcknowledgement){
            launch {
                billingRepository.acknowledgeSubscription(billingTokenInfo.subscriptionId!!, billingTokenInfo.token!!, TokenInfo().apply { payload = null }).await()
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