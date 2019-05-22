package com.square.android.data.network

import com.square.android.data.network.response.AuthResponse
import com.square.android.data.network.response.MessageResponse
import com.square.android.data.pojo.*
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @POST("auth/user/signin")
    fun registerUser(@Body authData: AuthData): Call<AuthResponse>

    @POST("auth/user/login")
    fun loginUser(@Body authData: AuthData): Call<AuthResponse>

    @POST("user/forgotPassword")
    fun resetPassword(@Body authData: AuthData): Deferred<MessageResponse>

    @GET("user/current")
    fun getCurrentProfile(): Deferred<Profile.User>

    @PUT("user/current")
    fun editProfile(@Body body: ProfileInfo): Call<MessageResponse>

    @GET("place")
    fun getPlaces(): Call<List<Place>>

    @GET("place/{id}")
    fun getPlace(@Path("id") id: Long): Deferred<Place>

    @POST("v2/place/{id}/book")
    fun book(@Path("id") id: Long,
             @Body body: BookInfo): Call<MessageResponse>

    @GET("place/{placeId}/booking/{bookingId}/offers")
    fun getOffersForBooking(@Path("placeId") placeId: Long,
                            @Path("bookingId") bookingId: Long): Deferred<List<OfferInfo>>

    @GET("place/{placeId}/booking/{bookingId}/offers")
    fun getOffersFor(@Path("placeId") placeId: Long,
                            @Path("bookingId") bookingId: Long,
                            @Query("start") start: String,
                            @Query("end") end: String): Deferred<List<OfferInfo>>

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

    @GET("place/{id}/intervals")
    fun getIntervals(@Path("id") placeId: Long,
                     @Query("date") date: String) : Deferred<IntervalsWrapper>

    @GET("place/{id}/book/slots")
    fun getIntervalSlots(@Path("id") placeId: Long,
                         @Query("date") date: String) : Deferred<List<Place.Interval>>

    @GET("offer/{id}/booking/{bookingId}/actions")
    fun getActions(@Path("id") offerId: Long,
                   @Path("bookingId") bookingId: Long) : Deferred<List<ReviewNetType>>

    @DELETE("user/{id}/images")
    fun removePhoto(@Path("id") userId: Long,
                    @Query("imageId") photoId: String) : Deferred<MessageResponse>

    @Multipart
    @POST("user/{id}/images")
    fun addPhoto(@Path("id") userId: Long,
                 @Part photo: MultipartBody.Part) : Deferred<Images>

    @PUT("user/{id}/images/{imageId}/main")
    fun setPhotoAsMain(@Path("id") userId: Long,
                       @Path("imageId") imageId: String) : Deferred<MessageResponse>

    @PUT("user/{id}/device")
    fun sendFcmToken(@Path("id") userId: Long,
                     @Body fcmTokenData: FcmTokenData) : Deferred<MessageResponse>
}