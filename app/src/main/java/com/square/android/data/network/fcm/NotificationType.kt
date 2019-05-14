package com.square.android.data.network.fcm

enum class NotificationType(val notifName: String) {
    USER_ACCEPTED("userAccept"),
    USER_REJECTED("userRejected"),
    ACTION_ACCEPTED("actionAccepted"),
    CREDITS_ADDED("creditsAdded"),
    BOOKING_CLOSED("bookingClosed"),
    NEW_RESTAURANT("newRestaurant"),
    NEW_OFFER("newOffer")
}