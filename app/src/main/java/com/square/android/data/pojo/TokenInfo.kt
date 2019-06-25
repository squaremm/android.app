package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonProperty

class TokenInfo(
        @field:JsonProperty("developerPayload")
        var payload: String? = null
)