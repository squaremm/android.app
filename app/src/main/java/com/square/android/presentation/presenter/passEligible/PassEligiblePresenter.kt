package com.square.android.presentation.presenter.passEligible

import android.text.TextUtils
import android.util.Log
import com.android.billingclient.api.Purchase
import com.arellomobile.mvp.InjectViewState
import com.square.android.GOOGLEBILLING.APP_PUBLIC_KEY
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.TokenInfo
import com.square.android.di.PurchasesUpdatedEvent
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.passEligible.PassEligibleView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.lang.Exception
import java.security.KeyFactory
import java.security.Signature
import android.util.Base64
import com.crashlytics.android.Crashlytics
import java.security.spec.X509EncodedKeySpec

@InjectViewState
class PassEligiblePresenter: BasePresenter<PassEligibleView>(){

    private val eventBus: EventBus by inject()

    init {
        eventBus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPurchasesUpdatedEvent(event: PurchasesUpdatedEvent) = launch ({

        Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: onPurchasesUpdatedEvent()"))

        val purchases = event.data

        viewState.handlePurchases(purchases.isNullOrEmpty())

        purchases?.let {

            val verifiedList: MutableList<Boolean> = mutableListOf()

            for (purchase in purchases) {
                val verified = verifyPurchase(purchase.originalJson , purchase.signature)

                if(!verified){
                    Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: verifyPurchase() -> NOT VERIFIED, id: ${purchase.sku}"))

                    Log.d("PURCHASE","| PassEligiblePresenter: verifyPurchase() -> NOT VERIFIED")
                } else{
                    Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: verifyPurchase() -> VERIFIED SUCCESSFULLY, id: ${purchase.sku}"))
                }

                verifiedList.add(verified)
            }

            Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: onPurchasesUpdatedEvent() -> purchases: ${purchases.toString()}"))

            var listPos = -1
            for (purchase in purchases) {
                listPos++

                if(verifiedList[listPos]){
                    repository.sendPaymentToken(BillingTokenInfo().apply { subscriptionId = purchase.sku; token = purchase.purchaseToken }).await()

                    Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: onPurchasesUpdatedEvent() -> sending payment token: subscriptionId = " +
                            "${purchase.sku}, token = ${purchase.purchaseToken}"))
                }
            }

            listPos = -1
            for (purchase in purchases) {
                listPos++

                if(verifiedList[listPos]){
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        repository.setUserEntitlement(purchase.sku, true)

                        Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: onPurchasesUpdatedEvent() -> setting user's entitlement: ${purchase.sku}"))
                    } else{
                        Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: onPurchasesUpdatedEvent() -> cannot set user's entitlement, " +
                                "purchase.purchaseState == ${purchase.purchaseState.toString()}"))
                    }
                }
            }

            listPos = -1
            for (purchase in purchases) {
                listPos++

                if(verifiedList[listPos]){
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: onPurchasesUpdatedEvent() -> purchase.purchaseState == Purchase.PurchaseState.PURCHASED"))

                        billingRepository.acknowledgeSubscription(purchase.sku, purchase.purchaseToken, TokenInfo().apply { payload = "" }).await()

                        Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: onPurchasesUpdatedEvent() -> acknowledgeSubscription"))

                    } else{
                        Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: onPurchasesUpdatedEvent() -> purchase.purchaseState == ${purchase.purchaseState.toString()}"))
                    }
                }
            }

            Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: onPurchasesUpdatedEvent() -> PURCHASE COMPLETED SUCCESSFULLY"))
            viewState.purchasesComplete()
        } ?: Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: onPurchasesUpdatedEvent() -> purchases ARE NULL"))

    }, { error ->
        //TODO must handle errors here - mixing repository and billingRepository

        viewState.hideDialog()

        Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: onPurchasesUpdatedEvent() -> error: ${error.toString()}"))
        Log.d("PURCHASE","PassEligiblePresenter: onPurchasesUpdatedEvent() -> error: ${error.toString()}")
    })

    private fun verifyPurchase(signedData: String, signature: String): Boolean {

        if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(signature)) {
            Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: verifyPurchase() - Purchase verification failed: missing data"))
            Log.e("PURCHASE", "PassEligiblePresenter: verifyPurchase() - Purchase verification failed: missing data")
            return false
        }

        try {
            val x509publicKey = X509EncodedKeySpec(Base64.decode(APP_PUBLIC_KEY.toByteArray(), Base64.DEFAULT))
            val publicKey = KeyFactory.getInstance("SHA256withRSA").generatePublic(x509publicKey)

            val rsaVerify = Signature.getInstance("SHA256withRSA") // ,provider = "BC" ?
            rsaVerify.initVerify(publicKey)
            rsaVerify.update(signedData.toByte())
            return rsaVerify.verify(signature.toByteArray())

        } catch (e: Exception){
            Crashlytics.logException(Throwable("PURCHASE -> PassEligiblePresenter: verifyPurchase() - Exception: ${e.toString()}"))
            Log.e("PURCHASE", "PassEligiblePresenter: verifyPurchase() - Exception: ${e.toString()}")
            return false
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
