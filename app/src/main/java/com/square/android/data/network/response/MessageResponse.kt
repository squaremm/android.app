package com.square.android.data.network.response

private const val NOT_AUTHENTICATED = "Not authenticated"
private const val NOT_ALL_FILLED = "Required fields are not fulfilled"
private const val TIME_ALREADY_BOOKED = "The time is already booked by this user"
private const val REDEMPTION_DELETE_FORBIDDEN = "Could not be deleted. Less than 3 hours left"
private const val WRONG_REFERRAL = "Wrong Referral Code"

val ERRORS = listOf(
        NOT_AUTHENTICATED, NOT_ALL_FILLED,
        TIME_ALREADY_BOOKED, REDEMPTION_DELETE_FORBIDDEN,
        WRONG_REFERRAL
)

class MessageResponse(var message: String = "")