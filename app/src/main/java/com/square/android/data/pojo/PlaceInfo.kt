package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

const val SOCIAL_FACEBOOK = "facebook"
const val SOCIAL_GOOGLE = "google"
const val SOCIAL_TRIPADVISOR = "tripAdvisor"
const val SOCIAL_INSTAGRAM = "instagram"
const val SOCIAL_YELP = "yelp"

val CREDITS_TO_SOCIAL = mapOf(
        TYPE_FACEBOOK_POST to SOCIAL_FACEBOOK,
        TYPE_GOOGLE_PLACES to SOCIAL_GOOGLE,
        TYPE_INSTAGRAM_POST to SOCIAL_INSTAGRAM,
        TYPE_INSTAGRAM_STORY to SOCIAL_INSTAGRAM,
        TYPE_TRIP_ADVISOR to SOCIAL_TRIPADVISOR,
        TYPE_YELP to SOCIAL_YELP
)

@JsonIgnoreProperties(ignoreUnknown = true)
class PlaceInfo(
        @field:JsonProperty("_id")
        var id: Long = 0,
        var address: String = "",
        var location: Location = Location(),
        var name: String = "",
        var photo   : String = "",
        var socials: Map<String, String> = mapOf()
)