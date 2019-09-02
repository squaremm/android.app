package com.square.android.data.network

import com.square.android.data.network.response.AuthResponse
import com.square.android.data.network.response.MessageResponse
import com.square.android.data.pojo.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("city")
    fun getCities(@Header("Authorization") authorization: String): Call<List<City>>

    @GET("place-time-frame")
    fun getTimeFrames(@Header("Authorization") authorization: String): Call<List<FilterTimeframe>>

    @GET("v2/place")
    fun getPlacesByFilters(@Header("Authorization") authorization: String,
                           @Query("tf") timeframe: String,
                           @Query("typology") type: String,
                           @Query("date") date: String,
                           @Query("city") city: String): Call<List<Place>>

    @POST("auth/user/signin")
    fun registerUser(@Body authData: AuthData): Call<AuthResponse>

    @POST("auth/user/login")
    fun loginUser(@Body authData: AuthData): Call<AuthResponse>

    @POST("user/forgotPassword")
    fun resetPassword(@Body authData: AuthData): Call<MessageResponse>

    @GET("user/current")
    fun getCurrentProfile(): Call<Profile.User>

    @PUT("user/current")
    fun editProfile(@Body body: ProfileInfo): Call<MessageResponse>

    @GET("place")
    fun getPlaces(): Call<List<Place>>

    @GET("place/{id}")
    fun getPlace(@Path("id") id: Long): Call<Place>

    @GET("place-type")
    fun getPlaceTypes(@Header("Authorization") authorization: String): Call<List<PlaceType>>

    @GET("place-extra")
    fun getPlaceExtras(@Header("Authorization") authorization: String) : Call<List<PlaceExtra>>

    @POST("v2/place/{id}/book")
    fun book(@Path("id") id: Long,
             @Body body: BookInfo): Call<MessageResponse>

    @GET("place/{placeId}/booking/{bookingId}/offers")
    fun getOffersForBooking(@Path("placeId") placeId: Long,
                            @Path("bookingId") bookingId: Long): Call<List<OfferInfo>>

    @GET("place/{placeId}/booking/{bookingId}/offers")
    fun getOffersFor(@Path("placeId") placeId: Long,
                            @Path("bookingId") bookingId: Long,
                            @Query("start") start: String,
                            @Query("end") end: String): Call<List<OfferInfo>>

    @GET("user/{id}/bookings")
    fun getRedemptions(@Path("id") userId: Long) : Call<List<RedemptionInfo>>

    @GET("place/book/{id}")
    fun getRedemption(@Path("id") id: Long) : Call<RedemptionFull>

    @GET("place/{id}/offer")
    fun getPlaceOffers(@Path("id") id: Long): Call<List<OfferInfo>>

    @DELETE("place/book/{id}")
    fun deleteRedemption(@Path("id") id: Long) : Call<MessageResponse>

    @PUT("place/book/{id}/claim")
    fun claimRedemption(@Path("id") id: Long) : Call<MessageResponse>

    @GET("place/offer/{offerId}")
    fun getOffer(@Path("offerId") offerId: Long,
                 @Query("userID") userId: Long) : Call<Offer>

//    @POST("v2/offer/{id}/booking/{bookingId}/post")
//    fun addReview(@Path("id") id: Long,
//                  @Path("bookingId") bookingId: Long,
//                  @Body info: ReviewInfo) : Call<MessageResponse>

    @POST("v2/offer/{id}/booking/{bookingId}/post")
    @Multipart
    fun addReview(@Path("id") id: Long,
                  @Path("bookingId") bookingId: Long,
                  @Part("info") info: ReviewInfo,
                  @Part image: MultipartBody.Part?) : Call<MessageResponse>

    @GET("place/{id}/sample")
    fun getFeedbackBody(@Path("id") id: Long) : Call<MessageResponse>

    @GET("user/{id}/bookNum")
    fun getBadgeCount(@Path("id") id: Long) : Call<BadgeInfo>

    @PUT("place/book/{id}/offer")
    fun addOfferToBook(@Path("id") bookId: Long,
                       @Body body: OfferToBook) : Call<MessageResponse>

    @GET("place/{id}/intervals")
    fun getIntervals(@Path("id") placeId: Long,
                     @Query("date") date: String) : Call<IntervalsWrapper>

    @GET("place/{id}/book/slots")
    fun getIntervalSlots(@Path("id") placeId: Long,
                         @Query("date") date: String) : Call<List<Place.Interval>>

    @GET("v2/offer/{id}/booking/{bookingId}/actions")
    fun getActions(@Path("id") offerId: Long,
                   @Path("bookingId") bookingId: Long) : Call<List<ReviewNetType>>

    @DELETE("user/{id}/images")
    fun removePhoto(@Path("id") userId: Long,
                    @Query("imageId") photoId: String) : Call<MessageResponse>

    @Multipart
    @POST("user/{id}/images")
    fun addPhoto(@Path("id") userId: Long,
                 @Part photo: MultipartBody.Part) : Call<Images>

    @PUT("user/{id}/images/{imageId}/main")
    fun setPhotoAsMain(@Path("id") userId: Long,
                       @Path("imageId") imageId: String) : Call<MessageResponse>

    @PUT("user/{id}/device")
    fun sendFcmToken(@Path("id") userId: Long,
                     @Body fcmTokenData: FcmTokenData) : Call<MessageResponse>


    @GET("user/paymentToken")
    fun getPaymentTokens(@Header("Authorization") authorization: String): Call<List<BillingTokenInfo>>

    @POST("user/paymentToken")
    fun sendPaymentToken(@Header("Authorization") authorization: String,
                         @Body body: BillingTokenInfo): Call<List<BillingTokenInfo>>

// Campaign
    @GET("campaign")
    fun getCampaigns(@Header("Authorization") authorization: String): Call<List<CampaignInfo>>

    @GET("campaign/{id}")
    fun getCampaign(@Header("Authorization") authorization: String,
                    @Path("id") campaignId: Long): Call<Campaign>

    @POST("campaign/{id}/join")
    fun joinCampaign(@Header("Authorization") authorization: String,
                     @Path("id") campaignId: Long): Call<Campaign>

    @POST("campaign/{id}/review")
    fun requestReview(@Header("Authorization") authorization: String,
                      @Path("id") campaignId: Long): Call<MessageResponse>

    @Multipart
    @POST("campaign/{id}/images")
    fun addCampaignImage(@Header("Authorization") authorization: String,
                         @Path("id") campaignId: Long,
                         @Part image: MultipartBody.Part) : Call<Images>

    @DELETE("campaign/{id}/images")
    fun removeCampaignImage(@Header("Authorization") authorization: String,
                            @Path("id") campaignId: Long,
                            @Query("imageId") imageId: String) : Call<MessageResponse>

    @GET("campaign/{id}/photos")
    fun getCampaignPhotos(@Header("Authorization") authorization: String,
                          @Path("id") campaignId: Long): Call<List<String>>


    //TODO change when API done - probably wrong -------------

    @GET("campaign/{id}/interval")
    fun getCampaignLocations(@Header("Authorization") authorization: String,
                             @Path("id") campaignId: Long) : Call<List<CampaignInterval.Location>>


    @GET("campaign/{id}/interval/{intervalId}/slots")
    fun getCampaignSlots(@Header("Authorization") authorization: String,
                         @Path("id") campaignId: Long,
                         @Path("intervalId") intervalId: Long,
                         @Query("date") date: String) : Call<List<CampaignInterval.Slot>>


    @POST("campaign/{id}/interval/{intervalId}/book")
    fun campaignBook(@Header("Authorization") authorization: String,
                     @Path("id") campaignId: Long,
                     @Path("intervalId") intervalId: Long,
                     @Body body: CampaignBookInfo): Call<MessageResponse>

    //TODO ---------------------------------------------------

    @POST("campaign")
    fun sendQr(@Header("Authorization") authorization: String,
               @Body body: QrInfo
    ): Call<Campaign>

    @GET("campaign/bookings")
    fun getCampaignBookings(@Header("Authorization") authorization: String): Call<List<CampaignBooking>>

    @POST("campaign/{id}/review")
    fun sendCampaignForReview(@Header("Authorization") authorization: String,
               @Path("id") campaignId: Long
    ): Call<MessageResponse>
}