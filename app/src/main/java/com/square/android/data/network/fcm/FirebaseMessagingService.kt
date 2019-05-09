package com.square.android.data.network.fcm


import com.square.android.data.Repository
import com.square.android.utils.DeviceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    protected val repository: Repository by inject()

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        sendTokenToApi(token)
    }

    private fun sendTokenToApi(fcmToken: String?) = GlobalScope.launch(Dispatchers.Main) {

        val token = repository.getFcmToken()

        val response = repository.sendFcmToken(DeviceUtil.getUniqueDeviceId(applicationContext), fcmToken, token).await()

        repository.saveFcmToken(fcmToken)
    }
}
