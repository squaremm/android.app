package com.square.android.utils

import android.net.Uri

fun buildInstagramUrl(clientId: String, redirectUrl: String): String {
    return "https://api.instagram.com/oauth/authorize/" +
            "?client_id=$clientId" +
            "&response_type=code" +
            "&redirect_uri=${Uri.encode(redirectUrl)}"
}