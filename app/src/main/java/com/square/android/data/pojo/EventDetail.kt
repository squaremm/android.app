package com.square.android.data.pojo

import com.squareup.moshi.JsonClass

const val TYPE_ONE_WAY = "oneWay"
const val TYPE_DINNER= "dinner"
const val TYPE_TRANSFER = "transfer"
const val TYPE_PARTY = "party"
const val TYPE_RETURN_TO = "returnTo"

@JsonClass(generateAdapter = true)
class EventDetail(
        var type: String = "",
        var placeName: String = "",
        var interval: String = "",
        var address: String = "",
        var status: String = "",
        var placeFrom: String = "",
        var placeTo: String = "",
        var idString: String? = null,
        var idLong: Long? = null,

        var available: Boolean = true,

        //(EXCEPT PARTY - party requires !checkedIn too) if !highlighted - user can click it and access it's options
        var highlighted: Boolean = false,

        var checkedIn: Boolean = false
)