package com.square.android.data

import com.square.android.data.network.PhotoId
import com.square.android.data.network.response.AuthResponse
import com.square.android.data.network.response.MessageResponse
import com.square.android.data.pojo.*
import kotlinx.coroutines.Deferred
import retrofit2.Call

interface Repository {
    fun shouldDisplayIntro(): Boolean
    fun introDisplayed()

    fun isLoggedIn(): Boolean
    fun setLoggedIn(isLogged: Boolean)

    fun setUserToken(token: String)

    fun isProfileFilled(): Boolean
    fun setProfileFilled(isFilled: Boolean)

    fun registerUser(authData: AuthData): Deferred<AuthResponse>
    fun loginUser(authData: AuthData): Deferred<AuthResponse>

    fun resetPassword(authData: AuthData): Deferred<MessageResponse>

    fun fillProfile(info: ProfileInfo): Deferred<MessageResponse>

    fun getCurrentUser(): Deferred<Profile.User>

    fun getPlaces(): Deferred<List<Place>>

    fun setUserId(id: Long)
    fun setAvatarUrl(url: String?)
    fun setUserName(name: String, surname: String)
    fun setSocialLink(username: String)

    fun getUserInfo() : UserInfo

    fun clearUserData()

    fun book(placeId: Long, bookInfo: BookInfo): Deferred<MessageResponse>

    fun getPlace(id: Long): Deferred<Place>

    fun getRedemptions(): Deferred<List<RedemptionInfo>>

    fun getRedemption(redemptionId: Long): Deferred<RedemptionFull>

    fun deleteRedemption(id: Long): Deferred<MessageResponse>

    fun getOffer(offerId: Long): Deferred<Offer>

    fun claimOffer(offerId: Long) : MessageResponse

    fun addReview(offerId: Long, info: ReviewInfo) : MessageResponse

    fun getPlaceOffers(placeId: Long) : Deferred<List<OfferInfo>>

    fun getFeedbackContent(placeId: Long) : Deferred<MessageResponse>

    fun getBadgeCount() : Deferred<BadgeInfo>

    fun addOfferToBook(bookId: Long,
                       offerId: Long) : MessageResponse

    fun getIntervals(placeId: Long, date: String): Deferred<IntervalsWrapper>
    fun getIntervalSlots(placeId: Long, date: String): Deferred<List<Place.Interval>>

    fun getActions(offerId: Long, bookingId: Long): Deferred<List<ReviewNetType>>

    fun removePhoto(userId: Long, photoId: PhotoId): Deferred<MessageResponse>
    fun addPhoto(userId: Long, imageBytes: ByteArray): Deferred<Images>
    fun setPhotoAsMain(userId: Long, photoId: String): Deferred<MessageResponse>

    fun saveFcmToken(fcmToken: String?)
    fun getFcmToken(): String?

    fun sendFcmToken(uuid: String, newFcmToken: String?, oldToken: String?): Deferred<MessageResponse>
    fun getOffersForBooking(placeId: Long, bookingId: Long): Deferred<List<OfferInfo>>
}