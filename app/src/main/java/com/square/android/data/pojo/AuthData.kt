package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AuthData(
        val email: String,
        val password: String,
        val confirmPassword: String
)