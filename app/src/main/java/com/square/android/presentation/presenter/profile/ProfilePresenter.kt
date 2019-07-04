package com.square.android.presentation.presenter.profile

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.crashlytics.android.Crashlytics
import com.square.android.GOOGLEBILLING
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.pojo.BillingSubscription
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.ProfileView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.util.*

class ProfileUpdatedEvent

@Suppress("unused")
@InjectViewState
class ProfilePresenter : BasePresenter<ProfileView>() {
    private val eventBus: EventBus by inject()

    private var actualTokenInfo: BillingTokenInfo? = null

    init {
        eventBus.register(this)

        loadData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onProfileUpdatedEvent(event: ProfileUpdatedEvent) {
        loadData()
    }

    private fun loadData() {
        launch {
            viewState.showProgress()
            val user = repository.getCurrentUser().await()

            viewState.showUser(user)

            viewState.hideProgress()

            loadSubscriptions()
        }
    }

    private fun loadSubscriptions() = launch ({
        Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions()"))

        viewState.hideButton()
        viewState.hidePremiumLabel()

        actualTokenInfo = null

        val isPaymentRequired = repository.getUserInfo().isPaymentRequired

        if(isPaymentRequired){
            Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> PAYMENT REQUIRED"))

            val subscriptions: MutableList<BillingSubscription> = mutableListOf()

            val billings: List<BillingTokenInfo> = repository.getPaymentTokens().await()

            for (billing in billings) {
                val data = billingRepository.getSubscription(billing.subscriptionId!!, billing.token!!).await()

                data.subscriptionId = billing.subscriptionId
                data.token = billing.token

                subscriptions.add(data)
            }

            Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> subscriptionsList: ${subscriptions.toString()}"))

            //TODO change to actual time from API
            val actualTimeInMillis: Long = Calendar.getInstance().timeInMillis

            val perWeekValidSub = subscriptions.filter { it.subscriptionId == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME}.sortedByDescending {it.expiryTimeMillis}.firstOrNull()
            perWeekValidSub?.let {
                Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perWeekValidSub NOT NULL"))

                val validExpiry = (it.expiryTimeMillis - actualTimeInMillis) > 1000

                if(validExpiry){
                    actualTokenInfo = BillingTokenInfo().apply { subscriptionId = it.subscriptionId; token = it.token }
                }

            } ?: run {
                Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perWeekValidSub IS NULL"))
            }

            val perMonthValidSub = subscriptions.filter { it.subscriptionId == GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_NAME}.sortedByDescending {it.expiryTimeMillis}.firstOrNull()
            perMonthValidSub?.let {
                Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perMonthValidSub NOT NULL"))

                val validExpiry = (it.expiryTimeMillis - actualTimeInMillis) > 1000

                if(validExpiry){
                    actualTokenInfo = BillingTokenInfo().apply { subscriptionId = it.subscriptionId; token = it.token }
                }

            } ?: run {
                Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perMonthValidSub IS NULL"))
            }

            if(actualTokenInfo != null){
                viewState.showButton(hasSubscription = true)
            } else{
                //TODO check if user can get a subscription (required amount of followers on instagram)
                viewState.showButton(hasSubscription = false)
            }

        } else{
            viewState.showPremiumLabel()
            Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> PAYMENT NOT REQUIRED"))
        }

    } ,{ error ->
        Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> error: ${error.toString()}"))

        Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> error: $error")
    })

    fun subButtonClicked(){
        actualTokenInfo?.let {
            cancelSubscription(it)
        } ?: router.navigateTo(SCREENS.PASS_ELIGIBLE)
    }

    private fun cancelSubscription(billingTokenInfo: BillingTokenInfo) = launch ({
        viewState.showSubProgress()

        billingRepository.cancelSubscription(billingTokenInfo.subscriptionId!!, billingTokenInfo.token!!).await()

        Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: cancelSubscription() -> subscription cancelled successfully"))

        loadSubscriptions()
    }, { error ->
        viewState.hideSubProgress()
        viewState.showMessage(R.string.something_went_wrong)

        Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: cancelSubscription() -> error: ${error.toString()}"))

        Log.d("SUBSCRIPTIONS","ProfilePresenter: cancelSubscription() -> error: ${error.toString()}")
    })

    fun openSettings() {
        router.navigateTo(SCREENS.EDIT_PROFILE)
    }

    override fun onDestroy() {
        eventBus.unregister(this)
    }

    fun navigateTutorialVideos(){
        router.navigateTo(SCREENS.TUTORIAL_VIDEOS)
    }
}
