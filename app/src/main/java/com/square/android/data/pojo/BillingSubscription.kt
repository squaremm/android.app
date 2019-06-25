package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class BillingSubscription(

        var kind: String? = null,
        var startTimeMillis: Long = 0,
        var expiryTimeMillis: Long = 0,
        var autoResumeTimeMillis: Long = 0,
        var autoRenewing: Boolean = false,
        var priceCurrencyCode: String? = null,
        var priceAmountMicros: Long = 0,
        var countryCode: String? = null,
        var developerPayload: String? = null,

        // 0 - Payment pending
        // 1 - Payment received
        // 2 - Free trial
        // 3 - Pending deferred upgrade/downgrade
        var paymentState: Int = 0,

        // 0 - User canceled the subscription
        // 1 - Subscription was canceled by the system, for example because of a billing problem
        // 2 - Subscription was replaced with a new subscription
        // 3 - Subscription was canceled by the developer
        var cancelReason: Int = -1,

        var userCancellationTimeMillis: Long = 0,
        var cancelSurveyResult: CancelSurveyResult? = null,

        var orderId: String? = null,

        var linkedPurchaseToken: String? = null,
        var purchaseType: Int = 0,
        var priceChange: PriceChange? = null,
        var profileName: String? = null,
        var emailAddress: String? = null,
        var givenName: String? = null,
        var familyName: String? = null,
        var profileId: String? = null,
        var acknowledgementState: Int = 0, // 0 - Yet to be acknowledged, 1 - Acknowledged


        //only in app
        var subscriptionId: String? = null,
        var token: String? = null

): Parcelable {

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class CancelSurveyResult(
            var cancelSurveyReason: Int = 0,
            var userInputCancelReason: String? = null
    ): Parcelable

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class PriceChange(
            var newPrice: NewPrice,

            // 0 - Outstanding: State for a pending price change waiting for the user to agree.
            // In this state, you can optionally seek confirmation from the user using the In-App API.

            // 1 - Accepted: State for an accepted price change that the subscription will renew with unless it's canceled.
            // The price change takes effect on a future date when the subscription renews.
            // Note that the change might not occur when the subscription is renewed next.
            var state: Int = 0

    ): Parcelable{
        @Parcelize
        @JsonIgnoreProperties(ignoreUnknown = true)
        class NewPrice(
                var priceMicros: String? = null,
                var currency: String? = null
        ): Parcelable
    }
}