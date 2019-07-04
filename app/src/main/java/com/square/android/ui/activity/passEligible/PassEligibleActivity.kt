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
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_pass_eligible.*
import ru.terrakok.cicerone.Navigator
import com.android.billingclient.api.BillingClient.FeatureType
import com.crashlytics.android.Crashlytics
import com.square.android.ui.activity.BaseBillingActivity

class PassEligibleActivity: BaseBillingActivity(), PassEligibleView{

    @InjectPresenter
    lateinit var presenter: PassEligiblePresenter

    @ProvidePresenter
    fun providePresenter() = PassEligiblePresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private var productLoaded = false

    private var skuDetailsList: List<SkuDetails>? = null

    private var selectedSkuDetails: SkuDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_eligible)

        passPayBtn.setOnClickListener {
            if(subscriptionsSupported(billingClient)){
                if(checkBillingReady()){
                    selectedSkuDetails?.let {
                        val flowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(it)
                                .build()
                        billingClient.launchBillingFlow(this, flowParams)
                    }
                }
            } else{
                Crashlytics.logException(Throwable("BILLING -> PassEligibleActivity: subscriptions not supported"))

                Log.d("BILLING","subscriptions not supported")

                showMessage(getString(R.string.subscriptions_not_supported))
            }
        }

        // must be called at the end of onCreate()
        connectBilling()
    }

    override fun onBillingConnected() {
        if(!productLoaded){
            getProductDetails()
        }
    }

    private fun subscriptionsSupported(client: BillingClient): Boolean =
            client.isFeatureSupported(FeatureType.SUBSCRIPTIONS).responseCode == BillingClient.BillingResponseCode.OK

    private fun getProductDetails(){
        val skuList = ArrayList<String>()
        skuList.add(GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME)
        skuList.add(GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_NAME)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {

                this.skuDetailsList = skuDetailsList

                productLoaded = true

                passProgress.visibility = View.GONE
                passPayBtn.visibility = View.VISIBLE

                //TODO show passCardView and hide passMainProgress

                setSelectedProduct(0)

            } else {
                Crashlytics.logException(Throwable("BILLING -> PassEligibleActivity: querySkuDetailsAsync() | responseCode != OK  or skuDetailsList == null\""))

                Log.d("BILLING","| PassEligibleActivity: querySkuDetailsAsync() | responseCode != OK  or skuDetailsList == null")
            }
        }
    }

    private fun setSelectedProduct(selectedProduct: Int){
        if(!skuDetailsList.isNullOrEmpty()){

            when(selectedProduct){
                0 -> {
                    selectedSkuDetails = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_NAME }
                }

                1 -> {
                    selectedSkuDetails = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME }
                }
            }

            selectedSkuDetails?.let {
                passCardPrice.text = it.price
                passCardTime.text = if(it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_NAME) getString(R.string.slash_month) else getString(R.string.slash_week)
            }
        }
    }

    override fun handlePurchases(nullOrEmpty: Boolean) {
        if(nullOrEmpty){
            showMessage(getString(R.string.something_went_wrong))
        } else{
            //TODO maybe show loading dialog that cannot be cancelled instead of code below

            passPayBtn.visibility = View.GONE
            passProgress.visibility = View.VISIBLE
        }
    }

    override fun purchasesComplete() {
        showMessage(getString(R.string.purchase_completed_successfully))
        onBackPressed()
    }

}