package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class RedemptionFull(
        @field:JsonProperty("place")
        var redemption: Redemption = Redemption()
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Redemption(
            @field:JsonProperty("_id")
            var id: Long = 0,
            var claimed: Boolean = false,
            var closed: Boolean = false,
            var creationDate: String = "",
            var date: String = "",
            var endTime: String = "",
            var place: PlaceInfo = PlaceInfo(),
            var startTime: String = "",
            var user: Int = 0
    )
}