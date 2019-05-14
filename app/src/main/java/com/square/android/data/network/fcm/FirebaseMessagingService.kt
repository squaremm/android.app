package com.square.android.data.network.fcm


import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.square.android.data.Repository
import com.square.android.utils.NotificationUtil
import com.square.android.utils.TokenUtils
import org.koin.android.ext.android.inject

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    protected val repository: Repository by inject()

    override fun onCreate() {
        super.onCreate()

        NotificationUtil.createChannels(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        Log.e("LOL", "MESSAGE RECEIVED")
        remoteMessage?.data?.forEach {
            if (it.key == "pushType") {
                Log.e("LOL", "NOTIF ${it.value}")

                sendNotification(remoteMessage.data, remoteMessage.notification)
            }
        }
    }

    private fun sendNotification(data: Map<String, String>, notification: RemoteMessage.Notification?) {
        val builder =
                NotificationUtil.createNotificationBuilder(baseContext, data)
                        .setContentTitle(notification?.title)
                        .setContentText(notification?.body)

        NotificationUtil.setAction(baseContext, builder, data)
        NotificationUtil.setDefaultValue(baseContext, builder)

        NotificationUtil.showNotification(baseContext, builder)
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        if (repository.getUserInfo().id != 0L) {
            TokenUtils.sendTokenToApi(applicationContext, repository, token)
        }
    }
}
