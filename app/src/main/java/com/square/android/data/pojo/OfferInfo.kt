package com.square.android.data.pojo;

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class OfferInfo(
        @field:JsonProperty("_id")
        var id: Long = 0,
        var closed: Boolean = false,
        @field:JsonProperty("isAvailable")
        var isAvailable: Boolean = false,
        var composition: List<String> = listOf(),
        var credits: Map<String, Int> = mapOf(),
        var name: String = "",
        var photo: String = "",
        var mainImage: String? = "",
        var place: Int = 0,
        var price: Int = 0,
        var user: Int = 0,
        var timeframes: List<String>? = null
): Parcelable {
    fun compositionAsList() = buildString {
        composition.forEachIndexed { index, component ->
            append("${index + 1}. $component\n")
        }
    }

    fun stringTimeframes() = timeframes
            ?.filter(String::isNotEmpty)
            ?.joinToString(separator = "\n")

    fun compositionAsString() = composition.joinToString(separator = ",")

    fun compositionAsStr() = composition.joinToString(separator = "\n")
}
