package com.square.android.ui.fragment.fillProfileFirst

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import kotlinx.android.synthetic.main.dialog_select_gender.view.*
import android.graphics.Color

class SelectGenderDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(onAction: (gender: Int) -> Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_select_gender, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.genderFemale.setOnClickListener { dialog.dismiss()
            onAction.invoke(1) }

        view.genderMale.setOnClickListener { dialog.dismiss()
            onAction.invoke(2) }

        dialog.show()
    }
}
