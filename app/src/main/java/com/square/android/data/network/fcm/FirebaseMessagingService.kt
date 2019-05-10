package com.square.android.data.network.fcm


import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.square.android.data.Repository
import com.square.android.utils.DeviceUtil
import com.square.android.utils.TokenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    protected val repository: Repository by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        remoteMessage?.data?.get(NotificationType.EARNED.toString())?.run {

        }
        remoteMessage?.data?.get(NotificationType.LOST.toString())?.run {

        }
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        if (repository.getUserInfo().id != 0L) {
            TokenUtils.sendTokenToApi(applicationContext, repository, token)
        }
    }
}
