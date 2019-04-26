package com.square.android.data.network

import com.square.android.data.network.response.AuthResponse
import com.square.android.data.network.response.MessageResponse
import com.square.android.data.pojo.*
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @POST("auth/user/signin")
    fun registerUser(@Body authData: AuthData): Call<AuthResponse>

    @POST("auth/user/login")
    fun loginUser(@Body authData: AuthData): Call<AuthResponse>

    @POST("user/forgotPassword")
    fun resetPassword(@Query("email") email: String): Deferred<MessageResponse>

    @GET("user/current")
    fun getCurrentProfile(): Deferred<Profile.User>

    @PUT("user/current")
    fun editProfile(@Body body: ProfileInfo): Call<MessageResponse>

    @GET("place")
    fun getPlaces(): Call<List<Place>>

    @GET("place/{id}")
    fun getPlace(@Path("id") id: Long): Deferred<Place>

    @POST("place/{id}/book")
    fun book(@Path("id") id: Long,
             @Body body: BookInfo): Call<MessageResponse>

    @GET("user/{id}/bookings")
    fun getRedemptions(@Path("id") userId: Long) : Deferred<List<RedemptionInfo>>

    @GET("place/book/{id}")
    fun getRedemption(@Path("id") id: Long) : Deferred<RedemptionFull>

    @GET("place/{id}/offer")
    fun getPlaceOffers(@Path("id") id: Long): Deferred<List<OfferInfo>>

    @DELETE("place/book/{id}")
    fun deleteRedemption(@Path("id") id: Long) : Deferred<MessageResponse>

    @PUT("place/book/{id}/claim")
    fun claimRedemption(@Path("id") id: Long) : Call<MessageResponse>

    @GET("place/offer/{offerId}")
    fun getOffer(@Path("offerId") offerId: Long,
                 @Query("userID") userId: Long) : Call<Offer>

    @POST("place/offer/{id}/post")
    fun addReview(@Path("id") id: Long,
                  @Body info: ReviewInfo) : Call<MessageResponse>

    @GET("place/{id}/sample")
    fun getFeedbackBody(@Path("id") id: Long) : Deferred<MessageResponse>

    @GET("user/{id}/bookNum")
    fun getBadgeCount(@Path("id") id: Long) : Deferred<BadgeInfo>

    @PUT("place/book/{id}/offer")
    fun addOfferToBook(@Path("id") bookId: Long,
                       @Body body: OfferToBook) : Call<MessageResponse>

    @GET("place/{id}/book/slots")
    fun getIntervals(@Path("id") placeId: Long,
                     @Query("date") date: String) : Deferred<List<Place.Interval>>
}