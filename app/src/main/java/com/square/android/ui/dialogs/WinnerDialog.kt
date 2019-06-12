package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import kotlinx.android.synthetic.main.dialog_winner.view.*

class WinnerDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(rewards: String, onAction: () -> Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_winner, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(false)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.winnerDialogRewards.text = rewards

        view.winnerDialogOk.setOnClickListener { dialog.dismiss()
            onAction.invoke() }

        dialog.show()
    }
}