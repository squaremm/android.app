package com.square.android.data.pojo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthData(
        val email: String,
        val password: String,
        val confirmPassword: String
)