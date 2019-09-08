package com.square.android.ui.fragment.review

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.core.text.HtmlCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.pojo.ReviewType
import com.square.android.data.pojo.TYPE_PICTURE
import com.square.android.extensions.loadImageInside
import kotlinx.android.synthetic.main.review_dialog.view.*

class ReviewDialog(private val context: Context) {

    private lateinit var reviewType: ReviewType
    private lateinit var view: View

    @SuppressLint("InflateParams")
    fun show(reviewType: ReviewType, coins: Int, index: Int, onAction: (reviewTypeKey: String, index: Int) -> Unit) {
        this.reviewType = reviewType

        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.review_dialog, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.reviewDialogImage.loadImageInside(reviewType.imageRes)
        view.reviewDialogCoins.text = context.getString(R.string.credits_plus_format, coins)
        view.reviewDialogType.text = context.getString(R.string.add_a) + " " + reviewType.title

        reviewType.app_name?.let {
            view.reviewDialogTask.text = context.getString(R.string.review_description_format, it)
        } ?: run {
            reviewType.description?.let {
                view.reviewDialogTask.text = it
            }
        }

        reviewType.content?.let {
            view.reviewDialogContent.visibility = View.VISIBLE
            view.reviewDialogContent.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        if (reviewType.showUploadLabel) {
            view.reviewDialogUpload.visibility = View.VISIBLE
            view.reviewBtnAction.text = context.getString(R.string.upload_a_screenshot)
        } else if (reviewType.key == TYPE_PICTURE) {
            view.reviewBtnAction.text = context.getString(R.string.next)
        } else {
            view.reviewBtnAction.text = context.getString(R.string.ok)
        }

        view.reviewBtnAction.setOnClickListener {
            dialog.dismiss()
            onAction.invoke(reviewType.key, index)
        }

        dialog.show()
    }
}