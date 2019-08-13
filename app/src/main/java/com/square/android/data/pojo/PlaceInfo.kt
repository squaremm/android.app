package com.square.android.data.pojo

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

const val SOCIAL_FACEBOOK = "facebook"
const val SOCIAL_GOOGLE = "google"
const val SOCIAL_TRIPADVISOR = "tripAdvisor"
const val SOCIAL_INSTAGRAM = "instagram"
const val SOCIAL_YELP = "yelp"
const val NON_SOCIAL_PHOTO = "photo"

val CREDITS_TO_SOCIAL = mapOf(
        TYPE_FACEBOOK_POST to SOCIAL_FACEBOOK,
        TYPE_GOOGLE_PLACES to SOCIAL_GOOGLE,
        TYPE_INSTAGRAM_POST to SOCIAL_INSTAGRAM,
        TYPE_INSTAGRAM_STORY to SOCIAL_INSTAGRAM,
        TYPE_TRIP_ADVISOR to SOCIAL_TRIPADVISOR,
        TYPE_YELP to SOCIAL_YELP,
        TYPE_PICTURE to NON_SOCIAL_PHOTO
)

@JsonClass(generateAdapter = true)
class PlaceInfo(
        @Json(name="_id")
        var id: Long = 0,
        var address: String = "",
        var location: Location = Location(),
        var name: String = "",
        var photo   : String? = "",
        var socials: Map<String, String> = mapOf()
)