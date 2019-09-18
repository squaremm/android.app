package com.square.android.presentation.presenter

import android.text.TextUtils
import android.util.Log
import com.arellomobile.mvp.MvpPresenter
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import com.square.android.GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_NAME
import com.square.android.GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.BillingRepository
import com.square.android.data.Repository
import com.square.android.data.SubscriptionInfo
import com.square.android.data.network.errorMessage
import com.square.android.data.pojo.BillingSubscription
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.ProfileInfo
import com.square.android.data.pojo.TokenInfo
import com.square.android.presentation.view.BaseView
import com.square.android.presentation.view.ProgressView
import com.square.android.ui.activity.noConnection.NoConnectionClosedEvent
import com.square.android.utils.AnalyticsManager
import com.square.android.utils.BooleanWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.http2.ConnectionShutdownException
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import ru.terrakok.cicerone.Router
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*

// 1 - successful, 2 - no internet connection, 3 - other error
class SubscriptionErrorEvent(var responseType: Int)

abstract class BasePresenter<V : BaseView> : MvpPresenter<V>(), KoinComponent {
    val repository: Repository by inject()

    val billingRepository: BillingRepository by inject()

    protected val router: Router by inject()

    private val allowSubsCheck: BooleanWrapper by inject(name = "allowSubsCheck")

    private val eventBus: EventBus = EventBus.getDefault()

    private var allowNoConnectionScreen = true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNoConnectionClosedEvent(event: NoConnectionClosedEvent) {
        allowNoConnectionScreen = true
        allowSubsCheck.value = true
        checkSubscriptions()
    }

    fun allowAndCheckSubs(){
        allowSubsCheck.value = true
        checkSubscriptions(true)
    }

    fun checkSubscriptions(skipError: Boolean = false){
        if(allowSubsCheck.value) {
            allowSubsCheck.value = false
            launch ({

                Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: checkSubscriptions()"))
                Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: checkSubscriptions()")

                repository.clearUserEntitlements()

                val isPaymentRequired = repository.getUserInfo().isPaymentRequired

                val subscriptions: MutableList<BillingSubscription> = mutableListOf()

                if(!isPaymentRequired){
                    Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> PAYMENT NOT REQUIRED, grant all entitlements to user"))
                    Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> PAYMENT NOT REQUIRED, grant all entitlements to user")
                    repository.grantAllUserEntitlements()
                } else{
                    var valid1 = false
                    var valid2 = false

                    Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> PAYMENT REQUIRED"))
                    Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> PAYMENT REQUIRED")

                    val billings: List<BillingTokenInfo> = repository.getPaymentTokens().await()

                    Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> billings: ${billings.toString()}"))
                    Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> billings: ${billings.toString()}")


                for (billing in billings) {
                    val data = billingRepository.getSubscription(billing.subscriptionId!!, billing.token!!).await()
                    data.subscriptionId = billing.subscriptionId
                    data.token = billing.token

                    subscriptions.add(data)
                }

                    Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> subscriptions: ${subscriptions.toString()}"))
                    Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> subscriptions: ${subscriptions.toString()}")

                    //TODO change to actual time from API
                    val actualTimeInMillis: Long = Calendar.getInstance().timeInMillis

                    //////// check for every subscriptionId in app products ///////////
                    val perWeekValidSub = subscriptions.filter { it.subscriptionId == SUBSCRIPTION_PER_WEEK_NAME && it.paymentState != 0}.sortedByDescending {it.expiryTimeMillis}.firstOrNull()
                    perWeekValidSub?.let {

                        Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> perWeekValidSub NOT NULL"))
                        Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> perWeekValidSub NOT NULL")

                        val validExpiry = (it.expiryTimeMillis - actualTimeInMillis) > 1000

                        valid1 = validExpiry

                        grantEntitlement(validExpiry, BillingTokenInfo().apply { subscriptionId = it.subscriptionId; token = it.token },
                                it.acknowledgementState == 0)
                    } ?: run {
                        Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> perWeekValidSub IS NULL"))
                        Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> perWeekValidSub IS NULL")
                    }

                    val perMonthValidSub = subscriptions.filter { it.subscriptionId == SUBSCRIPTION_PER_MONTH_NAME && it.paymentState != 0}.sortedByDescending {it.expiryTimeMillis}.firstOrNull()
                    perMonthValidSub?.let {

                        Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> perMonthValidSub NOT NULL"))
                        Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> perMonthValidSub NOT NULL")

                        val validExpiry = (it.expiryTimeMillis - actualTimeInMillis) > 1000

                        valid2 = validExpiry

                        grantEntitlement(validExpiry, BillingTokenInfo().apply { subscriptionId = it.subscriptionId; token = it.token },
                                it.acknowledgementState == 0)
                    } ?: run {
                        Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> perMonthValidSub IS NULL"))

                        Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> perMonthValidSub IS NULL")
                    }
                    //////////////////////////////////////////////////////////////////

                    if(!valid1 && !valid2){
                        router.navigateTo(SCREENS.PASS_ELIGIBLE)
                    }

                }
                eventBus.post(SubscriptionErrorEvent(1))

                if(!SubscriptionInfo.subscriptionChecked){
                    SubscriptionInfo.subscriptionChecked = true

                    val userId = repository.getUserId()
                    val isUserPremium = repository.isUserPremium()
                    println("User Analytics - Subscription checked -> userId: "+userId+", isUserPremium: "+isUserPremium+", isPaymentRequired: "+isPaymentRequired)
                    AnalyticsManager.logUser(userId, isUserPremium, isPaymentRequired)
                }

            }, { error ->
                Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: checkSubscriptions() -> error: ${error.toString()}"))
                Log.d("SUBSCRIPTIONS LOG","BasePresenter: checkSubscriptions() -> error: ${error.toString()}")

                //TODO uncomment when subscriptions working correctly
//                    if((error is UnknownHostException || error is SocketTimeoutException || error is ConnectException || error is ConnectionShutdownException)){
//                        eventBus.post(SubscriptionErrorEvent(2))
//
//                        if(!skipError){
//                            if(allowNoConnectionScreen){
//                                allowNoConnectionScreen = false
//                                router.navigateTo(SCREENS.NO_CONNECTION)
//                            }
//                        }
//                    } else{
//                        eventBus.post(SubscriptionErrorEvent(3))
//
//                        if(!skipError){
//                            router.navigateTo(SCREENS.SUBSCRIPTION_ERROR)
//                        }
//                    }

            })
        }
    }

    private fun grantEntitlement(validExpiry: Boolean, billingTokenInfo: BillingTokenInfo, acknowledgementRequired: Boolean){
        Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: grantEntitlement()"))
        Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: grantEntitlement()")

        if(validExpiry){
            repository.setUserEntitlement(billingTokenInfo.subscriptionId!!, true)
            Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: grantEntitlement() -> setting user's entitlement: ${billingTokenInfo.subscriptionId}")
            Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: grantEntitlement() -> setting user's entitlement: ${billingTokenInfo.subscriptionId}"))
        } else{
            Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: grantEntitlement() -> cannot set user's entitlement: ${billingTokenInfo.subscriptionId}, validExpiry == FALSE")
            Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: grantEntitlement() -> cannot set user's entitlement: ${billingTokenInfo.subscriptionId}, validExpiry == FALSE"))
        }

        if(acknowledgementRequired){
            Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: grantEntitlement(): acknowledgement IS REQUIRED")
            Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: grantEntitlement(): acknowledgement IS REQUIRED"))

            launch ({
                billingRepository.acknowledgeSubscription(billingTokenInfo.subscriptionId!!, billingTokenInfo.token!!, TokenInfo().apply { payload = "" }).await()
                Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: grantEntitlement(): ACKNOWLEDGED SUCCESSFULLY")
                Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: grantEntitlement(): ACKNOWLEDGED SUCCESSFULLY"))
            }, { error ->
                Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: grantEntitlement(): acknowledge -> error: ${error.toString()}"))

                Log.d("SUBSCRIPTIONS LOG","BasePresenter: grantEntitlement(): acknowledge -> error: ${error.toString()}")
            })
        } else{
            Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> BasePresenter: grantEntitlement(): acknowledgement NOT REQUIRED")
            Crashlytics.logException(Throwable("SUBSCRIPTIONS -> BasePresenter: grantEntitlement(): acknowledgement NOT REQUIRED"))
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
                if(it.errorMessage == "BOOKING IN THE PAST"){
                    viewState.showMessage(R.string.cant_book_past)
                } else{
                    viewState.showMessage(it.errorMessage)
                }
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