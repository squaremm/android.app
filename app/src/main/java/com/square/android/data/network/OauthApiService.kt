package com.square.android.data.network

import com.square.android.data.pojo.*
import retrofit2.Call
import retrofit2.http.*

interface OauthApiService {

    @FormUrlEncoded
    @POST("token")
    fun getToken(
            @Field("grant_type", encoded = true) grantType: String,
            @Field("client_id", encoded = true) clientId: String,
            @Field("client_secret", encoded = true) clientSecret: String,
            @Field("code", encoded = true) code: String,
            @Field("redirect_uri", encoded = true) redirectUri: String): Call<RefreshTokenResult>
}

