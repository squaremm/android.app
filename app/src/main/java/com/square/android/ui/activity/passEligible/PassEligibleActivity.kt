package com.square.android.ui.activity.passEligible

import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.billingclient.api.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.GOOGLEBILLING
import com.square.android.R
import com.square.android.presentation.presenter.passEligible.PassEligiblePresenter
import com.square.android.presentation.view.passEligible.PassEligibleView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_pass_eligible.*
import ru.terrakok.cicerone.Navigator
import com.android.billingclient.api.BillingClient.FeatureType

class PassEligibleActivity: BaseActivity(), PassEligibleView, PurchasesUpdatedListener{

    @InjectPresenter
    lateinit var presenter: PassEligiblePresenter

    @ProvidePresenter
    fun providePresenter() = PassEligiblePresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private var productLoaded = false

    private var selectedSkuDetails: SkuDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_eligible)

        passPayBtn.setOnClickListener {
                if(billingClient!!.isReady){
                    if(subscriptionsSupported(billingClient!!)){

                        selectedSkuDetails?.let {
                            val flowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(it)
                                    .build()
                            billingClient!!.launchBillingFlow(this, flowParams)
                        }

                    } else{

                        Log.d("BILLING","| subscriptionsSupported | responseCode != OK ")

                        showMessage(getString(R.string.subscriptions_not_supported))
                    }
                }
        }

        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {

                    Log.d("BILLING","| startConnection | RESULT OK")

                    if(!productLoaded){
                        getProductDetails()
                    }

                } else{
                    Log.d("BILLING","| startConnection | RESULT ${billingResult.responseCode}")
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.d("BILLING","| startConnection | DISCONNECTED")

                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
        getProductDetails()
    }

    private fun subscriptionsSupported(client: BillingClient): Boolean =
            client.isFeatureSupported(FeatureType.SUBSCRIPTIONS).responseCode == BillingClient.BillingResponseCode.OK

    fun getProductDetails(){
        val skuList = ArrayList<String>()
        skuList.add(GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient!!.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    productLoaded = true

                    val sku = skuDetails.sku
                    val price = skuDetails.price

                    if (GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME == sku) {
                        passCardPrice.text = price

                        selectedSkuDetails = skuDetails

                        passProgress.visibility = View.GONE
                        passPayBtn.visibility = View.VISIBLE
                    }
                }
            } else {
                Log.d("BILLING","| querySkuDetailsAsync | responseCode != OK  or skuDetailsList == null")

                showMessage(getString(R.string.something_went_wrong))
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {

//        billingResult?.let {
//            if (it.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
//                for (purchase in purchases) {
//                    super.handlePurchase(purchase)
//                }
//
//                //TODO handle successful purchase
//
//
//            } else if (it.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
//                Log.d("BILLING","| onPurchasesUpdated | responseCode == BillingClient.BillingResponseCode.USER_CANCELED")
//            } else {
//                showMessage(getString(R.string.something_went_wrong))
//                Log.d("BILLING","| onPurchasesUpdated | responseCode == other error code")
//            }
//        } ?: run {
//            showMessage(getString(R.string.something_went_wrong))
//            Log.d("BILLING","| onPurchasesUpdated | billingResult == null")
//        }
    }






}