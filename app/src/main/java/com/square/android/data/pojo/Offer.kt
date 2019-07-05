package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

const val TYPE_FACEBOOK_POST = "fbPost"
const val TYPE_INSTAGRAM_POST = "instaPost"
const val TYPE_INSTAGRAM_STORY = "instaStories"
const val TYPE_TRIP_ADVISOR = "tripAdvisorPost"
const val TYPE_GOOGLE_PLACES = "gPost"
const val TYPE_YELP = "yelpPost"
const val TYPE_PICTURE = "sendPicture"

@JsonIgnoreProperties(ignoreUnknown = true)
class Offer(
        @field:JsonProperty("_id")
        var id: Long = 0,
        var closed: Boolean = false,
        var composition: List<String> = listOf(),
        var credits: Map<String, Int> = mapOf(),
        var name: String = "",
        var photo: String = "",
        var place: PlaceInfo = PlaceInfo(),
        var isAvailable: Boolean = false,
        var price: Int = 0,
        var user: Int = 0,
        var instaUser: String = "",
        var posts: MutableList<Post> = mutableListOf(),
        var timeframes: List<String>? = null
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Post(
            @field:JsonProperty("_id")
            var id: Int = 0,
            var accepted: Boolean = false,
            var credits: Int = 0,
            var feedback: String = "",
            var link: String = "",
            var offer: Int = 0,
            var place: Int = 0,
            var type: String = "",
            var user: Long = 0
    )

    fun stringTimeframes() = timeframes
            ?.filter(String::isNotEmpty)
            ?.joinToString(separator = "\n")

    fun compositionAsString() = composition.joinToString(separator = "\n")
}