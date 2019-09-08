package com.square.android.data.pojo

import com.squareup.moshi.Json

class TokenInfo(
        @Json(name="developerPayload")
        var payload: String? = null
)