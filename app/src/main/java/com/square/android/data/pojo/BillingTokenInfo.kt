package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class BillingTokenInfo(
        @Json(name="id")
        var subscriptionId: String? = null,
        var token: String? = null
): Parcelable
