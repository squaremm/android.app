package com.square.android.data.network

import okhttp3.*
import com.square.android.GOOGLEBILLING.CLIENT_SECRET
import com.square.android.GOOGLEBILLING.REFRESH_TOKEN
import com.square.android.Network.OAUTH_API_URL
import com.square.android.Network.OAUTH_CLIENT_ID
import com.square.android.data.local.LocalDataManager
import com.square.android.data.pojo.RefreshTokenResult
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.lang.Exception

class TokenAuthenticator(private val manager: LocalDataManager): Authenticator {

    override fun authenticate(route: Route, response: Response): Request? {

        return if (refreshToken()) {
            val newToken = manager.getOauthToken()

            response.request().newBuilder()
                    .header("Authorization", newToken)
                    .build()
        } else {
            null
        }
    }

    private fun refreshToken(): Boolean {

        val client = Retrofit.Builder()
                .baseUrl(OAUTH_API_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()

        val service: OauthApiService = client.create(OauthApiService::class.java)

        try {
            val refreshExecute = service.refreshToken("refresh_token", OAUTH_CLIENT_ID, CLIENT_SECRET, REFRESH_TOKEN).execute()

            val refreshTokenResult: RefreshTokenResult? = refreshExecute.body()

            if (refreshExecute.isSuccessful && refreshTokenResult != null) {
                manager.setOauthToken(refreshTokenResult.access_token!!)

                return true
            } else {
                return false
            }

        } catch (e: Exception){
            return false
        }
    }

}