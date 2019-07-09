package com.square.android.data.network

import com.square.android.data.pojo.*
import retrofit2.Call
import retrofit2.http.*

interface OauthApiService {

    @FormUrlEncoded
    @POST("token")
    fun refreshToken(@Field("grant_type") grantType: String,
                     @Field("client_id") clientId: String, @Field("client_secret") clientSecret: String,
                     @Field("refresh_token") refreshToken: String,
                     @Field("redirect_uri") redirectUri: String): Call<RefreshTokenResult>
}

