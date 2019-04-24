package com.square.android.data.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class OfferInfo(
        @field:JsonProperty("_id")
        var id: Long = 0,
        var closed: Boolean = false,
        var composition: List<String> = listOf(),
        var credits: Map<String, Int> = mapOf(),
        var name: String = "",
        var photo: String = "",
        var place: Int = 0,
        var price: Int = 0,
        var user: Int = 0
) {
    fun compositionAsList() = buildString {
        composition.forEachIndexed { index, component ->
            append("${index + 1}. $component\n")
        }
    }

    fun compositionAsString() = composition.joinToString(separator = ",")
}
