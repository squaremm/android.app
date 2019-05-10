package com.square.android.utils

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.square.android.App
import org.json.JSONObject


enum class AnalyticsEvents {
    VENUE_CLICKED,
    BOOKING_MADE,
    RESTAURANT_OPENED_FROM_MAP,
    RESTAURANT_OPENED_FROM_LIST,
    BOOKING_OPENED,
    ACTIONS_OPENED,
    OFFER_SELECT
}

data class AnalyticsEvent(val eventName: AnalyticsEvents, val payload: HashMap<String, String>? = null)

object AnalyticsManager {

    fun logEvent(analyticsEvent: AnalyticsEvent) {
        val eventName = analyticsEvent.eventName.toString()

        val fabricEvent = CustomEvent(eventName)
        val mixpanelEvent = JSONObject()

        analyticsEvent.payload?.forEach {
            fabricEvent.putCustomAttribute(it.key, it.value)
            mixpanelEvent.put(it.key, it.value)
        }

        Answers.getInstance().logCustom(fabricEvent)
        App.INSTANCE.mixpanel.track(eventName, mixpanelEvent)
    }
}