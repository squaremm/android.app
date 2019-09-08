package com.square.android.data.pojo;

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreType
import com.square.android.data.network.IgnoreStringForArrays
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class OfferInfo(
        @Json(name="_id")
        var id: Long = 0,
        var closed: Boolean = false,
        @Json(name="isAvailable")
        var isAvailable: Boolean = false,
        @IgnoreStringForArrays.IgnoreJsonArrayError
        var composition: List<String>? = listOf(),
//        var credits: Map<String, Int> = mapOf(),
//        var actions: List<Action> = listOf(),
        var name: String = "",
        var photo: String? = "",
        var mainImage: String? = "",
        var place: Int = 0,
        var price: Int = 0,
        var user: Int = 0,
        var timeframes: List<String>? = null
): Parcelable {
    fun compositionAsList() = buildString {
        composition?.forEachIndexed { index, component ->
            append("${index + 1}. $component\n")
        }
    }

    fun stringTimeframes() = timeframes
            ?.filter(String::isNotEmpty)
            ?.joinToString(separator = "\n")

    fun compositionAsString() = composition?.joinToString(separator = ",")

    fun compositionAsStr() = composition?.joinToString(separator = "\n")
}
