package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import kotlinx.android.synthetic.main.dialog_congratulations.view.*

class CongratulationsDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(creditsEarned: Int, onAction: () ->Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_congratulations, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(false)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.cDialogCredits.text = context.getString(R.string.credits_plus_format, creditsEarned)

        view.cDialogOk.setOnClickListener { dialog.dismiss()
            onAction.invoke() }

        dialog.show()
    }
}