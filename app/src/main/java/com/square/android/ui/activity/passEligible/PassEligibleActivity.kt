package com.square.android.ui.activity.passEligible

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
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
import com.square.android.ui.dialogs.LoadingDialog

const val PASS_CAN_BACK_EXTRA = "PASS_CAN_BACK_EXTRA"

class PassEligibleActivity: BaseBillingActivity(), PassEligibleView{

    @InjectPresenter
    lateinit var presenter: PassEligiblePresenter

    @ProvidePresenter
    fun providePresenter() = PassEligiblePresenter(intent.getBooleanExtra(PASS_CAN_BACK_EXTRA, false))

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private var productLoaded = false

    private var skuDetailsList: List<SkuDetails>? = null

    private var selectedSkuDetails: SkuDetails? = null

    // 1 - weekly, 2 - monthly
    private var selectedOption = 1

    private var purchaseComplete: Boolean = false

    private var loadingDialog: LoadingDialog? = null

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
                Crashlytics.log("BILLING -> PassEligibleActivity: subscriptions not supported")

                Log.d("BILLING","subscriptions not supported")

                showMessage(getString(R.string.subscriptions_not_supported))
            }
        }

        passBtnWeekly.setOnClickListener { setSelectedTab(1) }
        passBtnMonthly.setOnClickListener { setSelectedTab(2) }
        passPlan1.setOnClickListener { setSelectedProduct(1) }
        passPlan2.setOnClickListener { setSelectedProduct(2) }

        passBack.setOnClickListener { onBackPressed() }

        //TODO unsubscribe
        passUnsubscribe.setOnClickListener {  }

        // must be called at the end of onCreate()
        connectBilling()

        loadingDialog = LoadingDialog(this)
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
        skuList.add(GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_PREMIUM_NAME)
        skuList.add(GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_PREMIUM_NAME)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {

                this.skuDetailsList = skuDetailsList

                productLoaded = true

                passProgress.visibility = View.GONE
                passPayBtn.visibility = View.VISIBLE

                setSelectedTab(1)

                hideMainProgress()

            } else {
                Crashlytics.log("BILLING -> PassEligibleActivity: querySkuDetailsAsync() | responseCode != OK  or skuDetailsList == null\"")

                Log.d("BILLING","| PassEligibleActivity: querySkuDetailsAsync() | responseCode != OK  or skuDetailsList == null")
            }
        }
    }

    private fun setSelectedProduct(selectedProduct: Int){
        if (!skuDetailsList.isNullOrEmpty()) {

            if(selectedOption == 1){
                //week
                when (selectedProduct) {
                    1-> {
                        selectedSkuDetails = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME }

                        passPlan1Bg.isChecked = true
                        passPlan2Bg.isChecked = false
                        passDivider1.isEnabled = true
                        passDivider2.isEnabled = false
                        passCb1.isChecked = true
                        passCb2.isChecked = false
                    }
                    2 ->{
                        selectedSkuDetails = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_PREMIUM_NAME }

                        passPlan2Bg.isChecked = true
                        passPlan1Bg.isChecked = false
                        passDivider2.isEnabled = true
                        passDivider1.isEnabled = false
                        passCb2.isChecked = true
                        passCb1.isChecked = false
                    }
                }

              //month
            } else if(selectedOption == 2){

                when (selectedProduct) {
                    1-> {
                        selectedSkuDetails = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_NAME }

                        passPlan1Bg.isChecked = true
                        passPlan2Bg.isChecked = false
                        passDivider1.isEnabled = true
                        passDivider2.isEnabled = false
                        passCb1.isChecked = true
                        passCb2.isChecked = false
                    }
                    2 ->{
                        selectedSkuDetails = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_PREMIUM_NAME }

                        passPlan2Bg.isChecked = true
                        passPlan1Bg.isChecked = false
                        passDivider2.isEnabled = true
                        passDivider1.isEnabled = false
                        passCb2.isChecked = true
                        passCb1.isChecked = false
                    }
                }

            }
        }
    }

    private fun setSelectedTab(selectedTab: Int){
        selectedOption = selectedTab

        if (!skuDetailsList.isNullOrEmpty()) {
            when (selectedTab) {
                // week
                1 -> {
                    passBtnWeekly.setTextColor(ContextCompat.getColor(passBtnWeekly.context, android.R.color.white))
                    passBtnWeekly.backgroundTintList = null

                    passBtnMonthly.setTextColor(ContextCompat.getColor(passBtnMonthly.context, R.color.gray_disabled))
                    passBtnMonthly.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(passBtnMonthly.context, android.R.color.white))

                    passWeekOff.visibility = View.GONE

                    passPer1.text = getString(R.string.per_week)
                    passPer2.text = getString(R.string.per_week)

                    passPrice1.text = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME }?.price
                    passPrice2.text = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_PREMIUM_NAME }?.price



                    passPrice2.text = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_PREMIUM_NAME }?.price


                    val featuresBasic: List<Feature> = listOf(
                            Feature(getString(R.string.basic_week_feature1),""),
                            Feature(getString(R.string.basic_week_feature2), getString(R.string.basic_week_feature2_secondary))
                    )

                    val featuresPremium: List<Feature> = listOf(
                            Feature(getString(R.string.premium_week_feature1),""),
                            Feature(getString(R.string.premium_week_feature2), getString(R.string.premium_week_feature2_secondary)),
                            Feature(getString(R.string.premium_week_feature3),""),
                            Feature(getString(R.string.premium_week_feature4),"")
                    )

                    passRv1.adapter = FeatureAdapter(featuresBasic)
                    passRv2.adapter = FeatureAdapter(featuresPremium)
                }

                // month
                2 -> {
                    passBtnMonthly.setTextColor(ContextCompat.getColor(passBtnMonthly.context, android.R.color.white))
                    passBtnMonthly.backgroundTintList = null

                    passBtnWeekly.setTextColor(ContextCompat.getColor(passBtnWeekly.context, R.color.gray_disabled))
                    passBtnWeekly.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(passBtnWeekly.context, android.R.color.white))

                    passWeekOff.visibility = View.VISIBLE

                    passPer1.text = getString(R.string.per_month)
                    passPer2.text = getString(R.string.per_month)

                    passPrice1.text = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_NAME }?.price
                    passPrice2.text = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_PREMIUM_NAME }?.price

                    val featuresBasic: List<Feature> = listOf(
                            Feature(getString(R.string.basic_month_feature1),""),
                            Feature(getString(R.string.basic_month_feature2), getString(R.string.basic_month_feature2_secondary))
                    )

                    val featuresPremium: List<Feature> = listOf(
                            Feature(getString(R.string.premium_month_feature1),""),
                            Feature(getString(R.string.premium_month_feature2), getString(R.string.premium_month_feature2_secondary)),
                            Feature(getString(R.string.premium_month_feature3),""),
                            Feature(getString(R.string.premium_month_feature4),"")
                    )

                    passRv1.adapter = FeatureAdapter(featuresBasic)
                    passRv2.adapter = FeatureAdapter(featuresPremium)
                }
            }

            setSelectedProduct(2)
        }
    }

    private fun hideMainProgress(){
        passMainProgress.visibility = View.GONE
        passContent.visibility = View.VISIBLE
    }

    override fun handlePurchases(nullOrEmpty: Boolean) {
        if(nullOrEmpty){
            showMessage(getString(R.string.something_went_wrong))
        } else{
            showDialog()
        }
    }

    override fun hideDialog() {
        loadingDialog?.dismiss()
    }

    override fun showDialog() {
        loadingDialog?.show()
    }

    override fun onBackPressed() {
        if (purchaseComplete || presenter.canGoBack) {
            super.onBackPressed()
        }
    }

    override fun purchasesComplete() {
        purchaseComplete = true
        hideDialog()

        showMessage(getString(R.string.purchase_completed_successfully))
        finish()
    }

}