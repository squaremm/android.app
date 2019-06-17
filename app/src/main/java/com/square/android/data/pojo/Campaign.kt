package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class Campaign(
        @field:JsonProperty("_id")
        var id: Long = 0,
        var title: String = "",
        var description: String? = null,
        var type: String? = null,
        var imageUrl: String? = null,
        var availableFrom: String = "",
        var availableTill: String = "",
        var startAt: String = "",
        var uploadPicturesTo: String? = null,
        var uploadPicturesInstagramTo: String? = null,
        var tasks: List<Task>? = null,
        var rewards: List<Reward>? = null
): Parcelable {

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Task(
            @field:JsonProperty("_id")
            var id: String? = null,
            var type: String? = null,
            var count: Int = 0
    ): Parcelable

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Reward(
            @field:JsonProperty("_id")
            var id: String? = null,
            var description: String? = null,
            var isGlobal: Boolean = true,
            var type: String? = null,
            var value: Int = 0
    ): Parcelable

}