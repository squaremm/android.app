package com.square.android.utils

import android.annotation.SuppressLint
import android.content.Context
import java.util.*

const val FALLBACK_DEVICE_UUID = "0b67d211-be66-4eb7-a6f2-c72b999296b9"

object DeviceUtil {

    @SuppressLint("HardwareIds")
    fun getUniqueDeviceId(context: Context): String {
        val androidId = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID) ?: FALLBACK_DEVICE_UUID
        val deviceUuid = UUID(androidId.hashCode().toLong(), androidId.hashCode().toLong().shl(32))
        return deviceUuid.toString()
    }
}