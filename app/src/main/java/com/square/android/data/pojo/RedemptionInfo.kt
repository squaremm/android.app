package com.square.android.data.pojo

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
class RedemptionInfo(
        @Json(name="_id")
        var id: Long = 0,
        var closed: Boolean = false,
        var date: String = "",
        var endTime: String = "",
        var offers: List<Long> = listOf(),
        var place: PlaceInfo = PlaceInfo(),
        var claimed: Boolean = false,
        var startTime: String = "",
        var user: Int = 0
) {
}