package com.square.android.ui.activity.passEligible

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.square.android.GOOGLEPAY
import com.square.android.R
import com.square.android.presentation.presenter.passEligible.PassEligiblePresenter
import com.square.android.presentation.view.passEligible.PassEligibleView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.utils.GooglePaymentHelper
import com.square.android.utils.microsToString
import kotlinx.android.synthetic.main.activity_pass_eligible.*
import org.json.JSONException
import org.json.JSONObject
import ru.terrakok.cicerone.Navigator
import kotlin.math.roundToLong

const val LOAD_PAYMENT_DATA_REQUEST_CODE = 991

class PassEligibleActivity: BaseActivity(), PassEligibleView {

    @InjectPresenter
    lateinit var presenter: PassEligiblePresenter

    @ProvidePresenter
    fun providePresenter() = PassEligiblePresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private var clickable: Boolean = false

    private lateinit var paymentsClient: PaymentsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_eligible)

        passPayBtn.setOnClickListener {
            if(clickable){ requestPayment() }
        }

        paymentsClient = GooglePaymentHelper.createPaymentsClient(this)
        possiblyShowPayButton()
    }

    private fun possiblyShowPayButton() {
        val isReadyToPayJson = GooglePaymentHelper.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return

        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                Log.w("isReadyToPay failed", exception)
            }
        }
    }

    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            clickable = true
            passProgress.visibility = View.GONE
            passPayBtn.visibility = View.VISIBLE
        } else {
            showMessage(getString(R.string.google_pay_not_available))
        }
    }

    private fun requestPayment() {
        clickable = false

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        val priceMicros = (GOOGLEPAY.PRICE_PER_WEEK * 1000000).roundToLong()
        val price = (priceMicros).microsToString()

        val paymentDataRequestJson = GooglePaymentHelper.getPaymentDataRequest(price)
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch payment data request")
            showMessage(getString(R.string.something_went_wring))
            clickable = true
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK ->
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                        }

                    Activity.RESULT_CANCELED -> {
                        // User cancelled without selecting a payment method
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }
                clickable = true
            }
        }
    }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson() ?: return

        try {
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")

            if (paymentMethodData
                            .getJSONObject("tokenizationData")
                            .getString("type") == "PAYMENT_GATEWAY" && paymentMethodData
                            .getJSONObject("tokenizationData")
                            .getString("token") == "examplePaymentMethodToken") {

                AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("Gateway name set to \"example\" - please modify " +
                                "Constants.java and replace it with your own gateway.")
                        .setPositiveButton("OK", null)
                        .create()
                        .show()
            }

            val billingName = paymentMethodData.getJSONObject("info")
                    .getJSONObject("billingAddress").getString("name")

            Log.d("BillingName", billingName)

            Log.d("GooglePaymentToken", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token"))

            //TODO handle payment successful

        } catch (e: JSONException) {
            showMessage(getString(R.string.something_went_wring))

            Log.e("handlePaymentSuccess", "Error: ${e.toString()}")
        }

    }

    /** User has already seen a popup informing them an error occurred. Normally, only logging is required. */
    private fun handleError(statusCode: Int) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
    }
}