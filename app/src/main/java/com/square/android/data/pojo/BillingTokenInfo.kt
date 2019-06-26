package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class BillingTokenInfo(
        @field:JsonProperty("id")
        var subscriptionId: String? = null,
        var token: String? = null
): Parcelable
