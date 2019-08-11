package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
@JsonIgnoreProperties(ignoreUnknown = true)
class RefreshTokenResult(
        var access_token: String? = null,
        var token_type: String? = null,
        var expires_in: Long = 0,
        var refresh_token: String? = null,
        var scope: String? = null
): Parcelable