package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Action(

        @Json(name="id") val id : String,
        @Json(name="displayName") val displayName : String,
        @Json(name="type") val type : String,
        @Json(name="credits") val credits : Int,
        @Json(name="imageUrl") val imageUrl : String,
        @Json(name="isPictureRequired") val isPictureRequired : Boolean,
        @Json(name="maxAttempts") val maxAttempts : Int,
        @Json(name="parentId") val parentId : String,
        @Json(name="attempts") val attempts : Int
) : Parcelable