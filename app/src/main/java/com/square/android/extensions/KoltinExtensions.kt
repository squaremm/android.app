package com.square.android.extensions

import android.util.Patterns
import com.square.android.App
import com.square.android.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

private val FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

fun Int.toOrdinalString() =
        this.toString() + when (this % 10) {
            in 11..13 -> "th"
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }

fun String.toDate(): Date {
    return FORMAT.parse(this)
}

fun Calendar.relativeTimeString(now: Calendar) : String {
    if (now.isToday(this)) return App.getString(R.string.today)

    val difference = timeInMillis - now.timeInMillis
    if (difference < 0) return App.getString(R.string.past)

    if (now.isTomorrow(this)) return App.getString(R.string.tomorrow)

    val day = get(Calendar.DAY_OF_MONTH).toOrdinalString()
    val month = getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

    return App.INSTANCE.getString(R.string.date_format, day, month)
}

fun Calendar.isToday(other: Calendar): Boolean {
    return get(Calendar.DATE) == other.get(Calendar.DATE)
}

fun Calendar.isTomorrow(other: Calendar): Boolean {
    return get(Calendar.DATE) - other.get(Calendar.DATE) == 1
}

fun String?.isUrl() : Boolean {
    return this != null && Patterns.WEB_URL.matcher(this).matches()
}

fun Calendar.getStringDate(): String {
    return FORMAT.format(time)
}

