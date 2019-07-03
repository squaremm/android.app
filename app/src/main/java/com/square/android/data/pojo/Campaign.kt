package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class Campaign(
        // not joined ///////
        @field:JsonProperty("_id")
        var id: Long = 0,
        var title: String = "",
        var description: String? = null,
        var type: String? = null,
        var tasks: List<Task>? = null,
        var rewards: List<Reward>? = null,
        var exampleImages: List<String>? = null,
        var moodboardImages: List<String>? = null,
        var mainImage: String? = null,
        var winners: List<Winner>? = null,
        var daysToStart: Int = 0,
        var daysToPicture: Int = 0,
        var daysToInstagramPicture: Int = 0,
        @field:JsonProperty("isJoinable")
        var isJoinable: Boolean = true,

        @field:JsonProperty("isParticipant")
        var isParticipant: Boolean = false,
        @field:JsonProperty("isWinner")
        var isWinner: Boolean = false,

        @field:JsonProperty("isGiftTaken")
        var isGiftTaken: Boolean? = false,

        var hasWinner: Boolean = false,
        ////////////////////

        var userWinner: UserWinner? = null,

        var imageCount: Int? = 0,
        var status: Int? = 0, // 0 - not joined or joined and waiting for acceptance?, 1 - joined and accepted -> Upload pics fragment or Approval fragment if photos already uploaded(isPictureUploadAllow == false)?
        var images: List<Photo>? = null,
        var statusDescription: String? = null,
        @field:JsonProperty("isPictureUploadAllow")
        var isPictureUploadAllow: Boolean? = false,

        var location: CampaignInterval.Location? = null,
        var slot: CampaignInterval.Slot? = null

): Parcelable {

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Task(
            @field:JsonProperty("_id")
            var id: String? = null,
            var type: String? = null,
            var description: String? = null,
            var count: Int = 0
    ): Parcelable

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Reward(
            @field:JsonProperty("_id")
            var id: String? = null,
            var description: String? = null,
            @field:JsonProperty("isGlobal")
            var isGlobal: Boolean = true,
            var type: String? = null,
            var value: Int = 0,
            var imageUrl: String? = "",
            var position: Int? = -1
    ): Parcelable

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Winner(
            var user: Long = 0,
            var position: Int = 0
    ): Parcelable

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class UserWinner(
            @field:JsonProperty("_id")
            var userId: Long = 0,
            var mainImage: String? = null
    ): Parcelable
}