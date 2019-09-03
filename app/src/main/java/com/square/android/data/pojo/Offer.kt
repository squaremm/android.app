package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

const val TYPE_FACEBOOK_POST = "fbPost"
const val TYPE_INSTAGRAM_POST = "instaPost"
const val TYPE_INSTAGRAM_STORY = "instaStories"
const val TYPE_TRIP_ADVISOR = "tripAdvisorPost"
const val TYPE_GOOGLE_PLACES = "gPost"
const val TYPE_YELP = "yelpPost"
const val TYPE_PICTURE = "picture"

const val SUBTYPE_FOOD_PIC = "foodPic"
const val SUBTYPE_ATMOSPHERE = "atmosphere"
const val SUBTYPE_MODEL_IN_VENUE = "model"
const val SUBTYPE_STILL_LIFE = "stillLife"

@JsonClass(generateAdapter = true)
@Parcelize
class Offer(
        @Json(name="_id")
        var id: Long = 0,
        var closed: Boolean = false,
        var composition: List<String> = listOf(),
        var credits: Map<String, Int> = mapOf(),
        var name: String = "",
        var photo: String = "",
        var place: PlaceInfo = PlaceInfo(),
        var price: Int = 0,
        var user: Int = 0,
        var instaUser: String = "",
        var posts: MutableList<Post> = mutableListOf(),
        var creationDate: String = "",
        var post: Long = 0,
        var level: Int = 0,
        var images: List<String> = listOf(),
        var mainImage: String? = null,
        var actions: List<Action> = listOf(),
        var subActions: List<Action> = listOf(),
        var scopes: List<String> = listOf()

        //Not in data from api
//        var isAvailable: Boolean = false,
//        var timeframes: List<String>? = null

) : Parcelable {

    @JsonClass(generateAdapter = true)
    @Parcelize
    class Action(
            var id: String = "",
            var displayName: String = "",
            var type: String = "",
            var credits: Int = 0,
            var imageUrl: String? = null,
            var isPictureRequired: Boolean = false,
            var maxAttempts: Int = 0,
            var parentId: String? = null,
            var attempts: Int = 0
    ): Parcelable

    @JsonClass(generateAdapter = true)
    @Parcelize
    class Post(
            @Json(name="_id")
            var id: Int = 0,
            var accepted: Boolean = false,
            var credits: Int = 0,
            var feedback: String = "",
            var link: String = "",
            var offer: Int = 0,
            var place: Int = 0,
            var type: String = "",
            var user: Long = 0
    ): Parcelable

    fun compositionAsString() = composition.joinToString(separator = "\n")
}