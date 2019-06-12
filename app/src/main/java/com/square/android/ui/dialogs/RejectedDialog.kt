package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import kotlinx.android.synthetic.main.dialog_rejected.view.*

class RejectedDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(onAction: () -> Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_rejected, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(false)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.rDialogContinue.setOnClickListener { dialog.dismiss()
            onAction.invoke() }

        dialog.show()
    }
}