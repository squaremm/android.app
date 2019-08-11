package com.square.android.data.network.response

import com.squareup.moshi.Json

class AuthResponse(var message: String = "",
                   var token: String? = null,
                   @Json(name="isChangePasswordRequired")
                   var isChangePasswordRequired: Boolean? = false)