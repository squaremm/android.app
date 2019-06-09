package com.square.android.ui.fragment.approval

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.extensions.loadImage
import android.graphics.Color
import kotlinx.android.synthetic.main.dialog_photo.view.*

class PhotoDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(photoUrl: String) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_photo, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.dialogPhotoImage.loadImage(photoUrl)

        dialog.show()
    }
}