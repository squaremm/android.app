package com.square.android.utils

import android.content.Context
import android.os.Bundle
import com.square.android.data.Repository
import com.square.android.data.network.fcm.NotificationType
import com.square.android.ui.dialogs.CongratulationsDialog
import com.square.android.ui.dialogs.LostCreditsDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DialogDepository(context: Context, val repository: Repository) {

    val lostCreditsDialog = LostCreditsDialog(context)
    val congratulationsDialog = CongratulationsDialog(context)

    fun showDialogFromNotification(notificationType: NotificationType, extras: Bundle) {
        when (notificationType) {
            NotificationType.USER_ACCEPTED -> {}
            NotificationType.USER_REJECTED -> {}
            NotificationType.ACTION_ACCEPTED -> {}
            NotificationType.CREDITS_ADDED -> congratulationsDialog.show(
                    extras["credits"] as Int
            ) {

            }
            NotificationType.BOOKING_CLOSED -> GlobalScope.launch {
                val user = repository.getCurrentUser().await()
                lostCreditsDialog.show(
                        extras["bookingCredits"] as Int,
                        extras["bookingName"] as String,
                        user.credits.toInt()
                ) {

                }
            }
            NotificationType.NEW_RESTAURANT -> {}
            NotificationType.NEW_OFFER -> {}
        }
    }
}