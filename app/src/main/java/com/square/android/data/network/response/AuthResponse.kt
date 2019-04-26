package com.square.android.data.network.response

import com.fasterxml.jackson.annotation.JsonProperty

class AuthResponse(var message: String = "",
                   var token: String? = null,
                   @field:JsonProperty("isChangePasswordRequired")
                   var isChangePasswordRequired: Boolean? = false)