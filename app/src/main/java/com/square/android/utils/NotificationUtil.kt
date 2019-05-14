package com.square.android.utils

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.square.android.R
import com.square.android.data.network.fcm.NotificationType
import com.square.android.ui.activity.main.MainActivity
import java.util.concurrent.atomic.AtomicInteger


object NotificationUtil {

    private val id = AtomicInteger(0)

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationType.values().map { it.name }.forEach {
                createChannel(it, it, context)
            }
        }
    }

    fun createNotificationBuilder(baseContext: Context, data: Map<String, String>): NotificationCompat.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(baseContext, data.getValue("pushType"))
        } else {
            NotificationCompat.Builder(baseContext, "miscellaneous")
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createChannel(channelId: String, channelName: String, baseContext: Context): NotificationChannel {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
            enableLights(true)
            enableVibration(true)
            lightColor = Color.GREEN
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        val manager = baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        return channel
    }

    fun setAction(baseContext: Context, builder: NotificationCompat.Builder, data: Map<String, String>) {
        val notificationIntent = Intent(baseContext, MainActivity::class.java)
        data.forEach {
            notificationIntent.putExtra(it.key, it.value)
        }

        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = TaskStackBuilder.create(baseContext)
                .addNextIntentWithParentStack(notificationIntent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)

        builder.setContentIntent(pendingIntent)
    }

    fun setDefaultValue(context: Context, builder: NotificationCompat.Builder) =
            builder.setAutoCancel(true)
                    .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.ic_launcher_icon_small)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_icon))

    fun showNotification(baseContext: Context, builder: NotificationCompat.Builder) =
            NotificationManagerCompat.from(baseContext).notify(getID(), builder.build())

    private fun getID() = id.incrementAndGet()
}
