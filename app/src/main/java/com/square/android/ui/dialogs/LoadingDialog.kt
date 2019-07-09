package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R

class LoadingDialog(private val context: Context) {

    var dialog: MaterialDialog? = null

    @SuppressLint("InflateParams")
    fun show() {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_loading, null, false)

        dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(false)
                .build()

        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog!!.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}