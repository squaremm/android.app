package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class QrInfo(
        val qrCode: String? = null
)