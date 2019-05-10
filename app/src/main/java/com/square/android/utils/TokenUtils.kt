package com.square.android.utils

import android.content.Context
import android.util.Log
import com.square.android.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object TokenUtils {

    fun sendTokenToApi(context: Context, repository: Repository, fcmToken: String?) = GlobalScope.launch(Dispatchers.Main) {
        val token = repository.getFcmToken()
        Log.e("LOL SEND TOKEN TO API", "Old fcm token: $token New fcm token: $fcmToken")

        val response = repository.sendFcmToken(DeviceUtil.getUniqueDeviceId(context), fcmToken, token).await()

        repository.saveFcmToken(fcmToken)
    }
}