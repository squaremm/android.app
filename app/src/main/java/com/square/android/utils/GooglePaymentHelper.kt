package com.square.android.utils

import android.app.Activity
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.square.android.GOOGLEPAY
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

object GooglePaymentHelper {

    val MICROS = BigDecimal(1000000.0)

    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    private fun gatewayTokenizationSpecification(): JSONObject {
        if (GOOGLEPAY.PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS.isEmpty()) {
            throw RuntimeException(
                    "Please edit the Constants.java file to add gateway name and other " +
                            "parameters your processor requires")
        }

        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put("parameters", JSONObject(GOOGLEPAY.PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS))
        }
    }

    private fun directTokenizationSpecification(): JSONObject {
        if (GOOGLEPAY.DIRECT_TOKENIZATION_PUBLIC_KEY == "REPLACE_ME" ||
                (GOOGLEPAY.DIRECT_TOKENIZATION_PARAMETERS.isEmpty() ||
                        GOOGLEPAY.DIRECT_TOKENIZATION_PUBLIC_KEY.isEmpty())) {

            throw RuntimeException(
                    "Please edit the Constants.java file to add protocol version & public key.")
        }

        return JSONObject().apply {
            put("type", "DIRECT")
            put("parameters", JSONObject(GOOGLEPAY.DIRECT_TOKENIZATION_PARAMETERS))
        }
    }

    private val allowedCardNetworks = JSONArray(GOOGLEPAY.SUPPORTED_NETWORKS)

    private val allowedCardAuthMethods = JSONArray(GOOGLEPAY.SUPPORTED_METHODS)

    // Optionally, you can add billing address/phone number associated with a CARD payment method.
    private fun baseCardPaymentMethod(billingAddressRequired: Boolean): JSONObject {
        return JSONObject().apply {

            val parameters = JSONObject().apply {
                put("allowedAuthMethods", allowedCardAuthMethods)
                put("allowedCardNetworks", allowedCardNetworks)

                if(billingAddressRequired){
                    put("billingAddressRequired", true)
                    put("billingAddressParameters", JSONObject().apply {
                        put("format", "FULL")
                    })
                }

            }

            put("type", "CARD")
            put("parameters", parameters)
        }
    }

    private fun cardPaymentMethod(billingAddressRequired: Boolean): JSONObject {
        val cardPaymentMethod = baseCardPaymentMethod(billingAddressRequired)
        cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification())

        //TODO can add paypal too: https://developer.paypal.com/docs/checkout/how-to/googlepay-integration/

        return cardPaymentMethod
    }

    fun isReadyToPayRequest(billingAddressRequired: Boolean = false): JSONObject? {
        return try {
            val isReadyToPayRequest = JSONObject(baseRequest.toString())
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod(billingAddressRequired)))

            isReadyToPayRequest

        } catch (e: JSONException) {
            null
        }
    }

    private val merchantInfo: JSONObject
        @Throws(JSONException::class)
        get() = JSONObject().put("merchantName", GOOGLEPAY.MERCHAT_NAME)

    fun createPaymentsClient(activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
                .setEnvironment(GOOGLEPAY.PAYMENTS_ENVIRONMENT)
                .build()

        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    @Throws(JSONException::class)
    private fun getTransactionInfo(price: String, priceStatusFinal: Boolean ): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)

            if(priceStatusFinal){
                put("totalPriceStatus", "FINAL")
            } else{
                put("totalPriceStatus", "ESTIMATED")
            }

            put("currencyCode", GOOGLEPAY.CURRENCY_CODE)
        }
    }

    fun getPaymentDataRequest(price: String, priceStatusFinal: Boolean = true, billingAddressRequired: Boolean = false, shippingRequired: Boolean = false, phoneNumberRequired: Boolean = false): JSONObject? {
        try {
            return JSONObject(baseRequest.toString()).apply {
                put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod(billingAddressRequired)))
                put("transactionInfo", getTransactionInfo(price, priceStatusFinal))
                put("merchantInfo", merchantInfo)

                if(shippingRequired){
                    val shippingAddressParameters = JSONObject().apply {
                        put("phoneNumberRequired", phoneNumberRequired)
                        put("allowedCountryCodes", JSONArray(GOOGLEPAY.SHIPPING_SUPPORTED_COUNTRIES))
                    }
                    put("shippingAddressParameters", shippingAddressParameters)
                }

                put("shippingAddressRequired", shippingRequired)
            }
        } catch (e: JSONException) {
            return null
        }
    }

}

fun Long.microsToString() = BigDecimal(this)
        .divide(GooglePaymentHelper.MICROS)
        .setScale(2, RoundingMode.HALF_EVEN)
        .toString()
