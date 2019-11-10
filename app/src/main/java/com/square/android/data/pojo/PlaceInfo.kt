package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

const val SOCIAL_FACEBOOK = "facebook"
const val SOCIAL_GOOGLE = "google"
const val SOCIAL_TRIPADVISOR = "tripAdvisor"
const val SOCIAL_INSTAGRAM = "instagram"
const val SOCIAL_YELP = "yelp"

@JsonClass(generateAdapter = true)
@Parcelize
class PlaceInfo(
        @Json(name="_id")
        var id: Long = 0,
        var socials: Map<String, String> = mapOf(),
        var name: String = "",
        var address: String = "",
        var mainImage: String?= "",
        var photo: String? = ""

): Parcelable