package com.square.android.data.pojo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class QrInfo(
        val qrCode: String? = null
)