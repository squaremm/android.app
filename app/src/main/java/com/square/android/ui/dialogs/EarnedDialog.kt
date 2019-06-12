package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import kotlinx.android.synthetic.main.dialog_earned.view.*

class EarnedDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(creditsEarned: Int, balance: Int, onAction: () -> Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_earned, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(false)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        view.earnedDialogLabel.text = context.getString(R.string.earned_credits_format, creditsEarned)
        view.earnedDialogText.text = context.getString(R.string.dialog_earned_text_format, creditsEarned)

        view.earnedDialogBalance.text = context.getString(R.string.credits_format_lowercase, balance)

        view.earnedDialogOk.setOnClickListener { dialog.dismiss()
            onAction.invoke() }

        dialog.show()
    }
}