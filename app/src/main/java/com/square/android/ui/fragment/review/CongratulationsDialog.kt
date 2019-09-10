package com.square.android.ui.fragment.review

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.square.android.R
import kotlinx.android.synthetic.main.congratulations_dialog.view.*

class CongratulationsDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(listener: () -> Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.congratulations_dialog, null, false)

        val dialog = AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create()

        view.congratulationsSubmit.setOnClickListener { dialog.cancel(); listener.invoke() }

        dialog.show()
    }
}