package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class FcmTokenData(
        @field:JsonProperty("uid")
        val uuid: String? = null,

        val type: String = "Android",

        val newToken: String? = null,

        val oldToken: String? = null
)