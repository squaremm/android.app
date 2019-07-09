package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class RefreshTokenResult(
        var access_token: String? = null,
        var token_type: String? = null,
        var expires_in: Long = 0,
        var refresh_token: String? = null
): Parcelable