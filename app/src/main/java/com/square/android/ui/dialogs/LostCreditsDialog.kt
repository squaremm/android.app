package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import kotlinx.android.synthetic.main.dialog_lost_credits.view.*

class LostCreditsDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(creditsLost: Int, placeName: String, balance: Int, onAction: () ->Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_lost_credits, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(false)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.lDialogSince.text = context.getString(R.string.lost_credits_message_format, placeName)
        view.lDialogCredits.text = context.getString(R.string.lost_credits_format, creditsLost)
        view.lDialogBalance.text = context.getString(R.string.credits_format_lowercase, balance)

        view.lDialogOk.setOnClickListener { dialog.dismiss()
            onAction.invoke() }

        dialog.show()
    }
}
