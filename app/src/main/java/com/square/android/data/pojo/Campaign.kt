package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Campaign(
        // not joined ///////
        @Json(name="_id")
        var id: Long = 0,
        var title: String = "",
        var description: String? = null,
        var type: String? = null,
        var tasks: List<Task>? = null,
        var rewards: List<Reward>? = null,
        var exampleImages: List<Photo>? = null,
        var moodboardImages: List<Photo>? = null,
        var mainImage: String? = null,
        var winners: List<Winner>? = null,
        var daysToStart: Int = 0,
        var daysToPicture: Int = 0,
        var daysToInstagramPicture: Int = 0,
        @Json(name="isJoinable")
        var isJoinable: Boolean = true,

        @Json(name="isParticipant")
        var isParticipant: Boolean = false,
        @Json(name="isWinner")
        var isWinner: Boolean = false,
        @Json(name="isAccepted")
        var isAccepted: Boolean = false,

        @Json(name="isGiftTaken")
        var isGiftTaken: Boolean? = false,

        var hasWinner: Boolean = false,
        ////////////////////

        var userWinner: UserWinner? = null,

        var imageCount: Int? = 0,
        var status: Int? = 0, // 0 - not joined or joined and waiting for acceptance?, 1 - joined and accepted -> Upload pics fragment or Approval fragment if photos already uploaded(isPictureUploadAllow == false)?
        var images: List<Photo>? = null,
        var statusDescription: String? = null,
        @Json(name="isPictureUploadAllow")
        var isPictureUploadAllow: Boolean? = false,

        var location: CampaignInterval.Location? = null,
        var slot: CampaignInterval.Slot? = null,
        var qrCode: String? = null,
        var maxParticipantsCount: Int? = 0,
        var credits: Int? = null


): Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    class Task(
            @Json(name="_id")
            var id: String? = null,
            var type: String? = null,
            var description: String? = null,
            var count: Int = 0
    ): Parcelable

    @Parcelize
    @JsonClass(generateAdapter = true)
    class Reward(
            @Json(name="_id")
            var id: String? = null,
            var description: String? = null,
            @Json(name="isGlobal")
            var isGlobal: Boolean = false,
            var type: String? = null,
            var value: Int = 0,
            var mainImage: String? = "",
            var position: Int? = -1
    ): Parcelable

    @Parcelize
    @JsonClass(generateAdapter = true)
    class Winner(
            var user: Long = 0,
            var position: Int = 0
    ): Parcelable

    @Parcelize
    @JsonClass(generateAdapter = true)
    class UserWinner(
            @Json(name="_id")
            var userId: Long = 0,
            var mainImage: String? = null
    ): Parcelable
}