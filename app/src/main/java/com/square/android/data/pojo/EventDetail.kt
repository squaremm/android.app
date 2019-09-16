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

        // if not
        // hide itemEventDetailsIcon
        // transfer - hide itemEventDetailsNameTo and itemEventDetailsArrow, itemEventDetailsNameTo text = "-"
        // other views - disabled state
        // dinner - above + hide itemEventDetailsContainer, show no dinner available label
        var available: Boolean = true,

        // for icon and line
        //(EXCEPT PARTY - see checkedIn) if highlighted - user can't click it and access it's options
        var highlighted: Boolean = false,


        // for party, if not checked in - allow clicking for check in + below
        // for everything else - if checkedIn - itemEventDetailsContainer checked state
        var checkedIn: Boolean = false
)