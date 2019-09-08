package com.square.android.data.pojo

import com.squareup.moshi.Json

data class FcmTokenData(
        @Json(name="uid")
        val uuid: String? = null,

        val type: String = "Android",

        val newToken: String? = null,

        val oldToken: String? = null
)