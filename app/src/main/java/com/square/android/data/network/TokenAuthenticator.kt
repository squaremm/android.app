package com.square.android.data.network

import android.util.Log
import com.crashlytics.android.Crashlytics
import okhttp3.*
import com.square.android.GOOGLEBILLING.CLIENT_SECRET
import com.square.android.GOOGLEBILLING.GOOGLE_CODE
import com.square.android.GOOGLEBILLING.REFRESH_TOKEN
import com.square.android.Network.OAUTH_API_URL
import com.square.android.Network.OAUTH_CLIENT_ID
import com.square.android.data.local.LocalDataManager
import com.square.android.data.pojo.RefreshTokenResult
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.lang.Exception

class TokenAuthenticator(private val manager: LocalDataManager): Authenticator {

    override fun authenticate(route: Route, response: Response): Request? {
        Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> TokenAuthenticator: authenticate()")
        Crashlytics.logException(Throwable("TokenAuthenticator: authenticate()"))

        return if (refreshToken()) {
            Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> TokenAuthenticator: getToken() -> NEW TOKEN OBTAINED SUCCESSFULLY")
            Crashlytics.logException(Throwable("TokenAuthenticator: getToken() -> NEW TOKEN OBTAINED SUCCESSFULLY"))

            val newToken = manager.getOauthToken()

            response.request().newBuilder()
                    .header("Authorization", newToken)
                    .build()
        } else {
            Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> TokenAuthenticator: getToken() -> NEW TOKEN NOT OBTAINED")
            Crashlytics.logException(Throwable("TokenAuthenticator: getToken() -> NEW TOKEN NOT OBTAINED"))
            null
        }
    }

    private fun refreshToken(): Boolean {
        Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> TokenAuthenticator: getToken()")
        Crashlytics.logException(Throwable("TokenAuthenticator: getToken()"))

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

        val client = Retrofit.Builder()
                .baseUrl(OAUTH_API_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build()

        val service: OauthApiService = client.create(OauthApiService::class.java)

        try {
            val refreshExecute = service.getToken("refresh_token", OAUTH_CLIENT_ID, CLIENT_SECRET, REFRESH_TOKEN).execute()

            println("EEEE :"+ refreshExecute.raw().toString())

            val refreshTokenResult: RefreshTokenResult? = refreshExecute.body()

            if (refreshExecute.isSuccessful && refreshTokenResult != null) {

                Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> TokenAuthenticator: getToken() -> refreshExecute.isSuccessful && refreshTokenResult != null")
                Crashlytics.logException(Throwable("TokenAuthenticator: getToken() -> refreshExecute.isSuccessful && refreshTokenResult != null"))

                manager.setOauthToken(refreshTokenResult.access_token!!)

                return true
            } else {

                if(refreshTokenResult == null){
                    Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> TokenAuthenticator: getToken() ->  refreshTokenResult == null")
                    Crashlytics.logException(Throwable("TokenAuthenticator: getToken() -> refreshTokenResult == null"))
                } else{
                    Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> TokenAuthenticator: getToken() -> refreshExecute IS NOT SUCCESSFUL")
                    Crashlytics.logException(Throwable("TokenAuthenticator: getToken() -> refreshExecute IS NOT SUCCESSFUL"))
                }

                return false
            }

        } catch (e: Exception){

            Log.d("SUBSCRIPTIONS LOG","SUBSCRIPTIONS -> TokenAuthenticator: getToken() -> exception: ${e.toString()}")
            Crashlytics.logException(Throwable("TokenAuthenticator: getToken() -> exception: ${e.toString()}"))

            return false
        }
    }

}