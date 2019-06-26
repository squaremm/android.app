package com.square.android.ui.activity

import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import org.koin.android.ext.android.inject

abstract class BaseBillingActivity: BaseActivity(){

    val billingClient: BillingClient by inject()

    open fun onBillingConnected() {}

    fun connectBilling(){
        checkBillingReady()
    }

    fun checkBillingReady(): Boolean{
        if(billingClient.isReady){
            onBillingConnected()
            return true
        } else{
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                        onBillingConnected()
                    } else{
                        Log.d("BILLING","| BaseBillingActivity: startConnection | RESULT ${billingResult.responseCode}")
                    }
                }
                override fun onBillingServiceDisconnected() {
                    Log.d("BILLING","| BaseBillingActivity: startConnection | DISCONNECTED")
                }
            })

            return false
        }
    }
}