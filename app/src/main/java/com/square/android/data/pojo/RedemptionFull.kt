package com.square.android.data.pojo

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
class RedemptionFull(
        @Json(name="place")
        var redemption: Redemption = Redemption()
) {
    @JsonClass(generateAdapter = true)
    class Redemption(
            @Json(name="_id")
            var id: Long = 0,
            var claimed: Boolean = false,
            var closed: Boolean = false,
            var creationDate: String? = "",
            var date: String = "",
            var endTime: String = "",
            var place: PlaceInfo = PlaceInfo(),
            var startTime: String = "",
            var user: Int = 0
    )
}