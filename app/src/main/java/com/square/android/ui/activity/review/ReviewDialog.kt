package com.square.android.ui.activity.review

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.square.android.App
import com.square.android.R
import com.square.android.data.pojo.ReviewInfo
import com.square.android.data.pojo.ReviewType
import com.square.android.extensions.content
import com.square.android.extensions.loadImageInside
import com.square.android.extensions.setHtml
import com.square.android.utils.ClipboardUtil
import kotlinx.android.synthetic.main.review_dialog.view.*

const val RATING_VALUE_UNDEFINED = -1

class StageResult(val stage: Int,
                  val rating: Int,
                  val isLast: Boolean,
                  val doneClicked: Boolean)

class ReviewDialog(private val context: Context, private val reviewSample: String) {
    private var currentStage = -1

    private lateinit var reviewType: ReviewType
    private lateinit var view: View
    private lateinit var dialog: Dialog
    private var latestStepAccepted: Int = -1

    @SuppressLint("InflateParams")
    fun show(reviewType: ReviewType, coins: Int, stageChangedCallback: (StageResult) -> Unit) {
        this.reviewType = reviewType

        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.review_dialog, null, false)

        dialog = AlertDialog.Builder(context)
                .setView(view)
                .create()

        view.reviewCopyText.text = reviewSample
        view.reviewDialogType.text = context.getString(R.string.add_a) + " " + context.getString(reviewType.titleRes)
        view.reviewDialogCoins.text = context.getString(R.string.award_single_line_format, coins)
        view.reviewDialogImage.loadImageInside(reviewType.imageRes)

        view.reviewCopyActionText.setOnClickListener {
            ClipboardUtil.copyText(context, view.reviewCopyText.content)
            Toast.makeText(context, R.string.text_copied, Toast.LENGTH_SHORT).show()
            unlockActionButton()
        }

        view.reviewDialogDone.setOnClickListener {
            sendCallback(stageChangedCallback, true)

            dialog.dismiss()
        }

        view.reviewDialogBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            unlockActionButton()
        }

        view.reviewDialogSubmit.setOnClickListener {
            sendCallback(stageChangedCallback, false)

            if (currentStage == reviewType.stages.size - 1) {
                dialog.dismiss()
            } else {
                lockActionButton()
                nextStage()
            }
        }

        nextStage()

        dialog.show()
    }

    private fun lockActionButton() {
        view.reviewDialogSubmit.isEnabled = false
    }

    private fun unlockActionButton() {
        view.reviewDialogSubmit.isEnabled = true
    }

    private fun sendCallback(stageChangedCallback: (StageResult) -> Unit, doneClicked: Boolean) {
        val ratingNeeded = reviewType.stages[currentStage].ratingNeeded

        val currentRating = if (ratingNeeded) {
            view.reviewDialogBar.rating.toInt()
        } else {
            RATING_VALUE_UNDEFINED
        }

        val isLast = currentStage == reviewType.stages.size - 1

        stageChangedCallback(StageResult(currentStage, currentRating, isLast, doneClicked))
    }

    private fun nextStage() {
        currentStage++

        if (currentStage == reviewType.stages.size - 1) {
            unlockActionButton()
        }

        view.step.text = context.getString(R.string.step) + " " + (currentStage + 1) + "/" + (reviewType.stages.size)

        val stage = reviewType.stages[currentStage]

        view.reviewDialogTask.setHtml(stage.content)

        val ratingVisibility = if (stage.ratingNeeded) View.VISIBLE else View.GONE
        view.reviewDialogBar.visibility = ratingVisibility

        if (stage.buttonText != R.string.copy_review)
            view.reviewDialogSubmit.setText(stage.buttonText)

        val doneVisibility = if (stage.doneEnabled) View.VISIBLE else View.GONE

        view.reviewDialogDone.visibility = doneVisibility

        val reviewVis = if (stage.buttonText == R.string.copy_review) {
            View.VISIBLE
        } else {
            View.GONE
        }

        view.reviewCopyText.visibility = reviewVis
        view.reviewCopyActionText.visibility = reviewVis

//        if (stage.subtitleRes != null) {
//            view.reviewDialogTitle.visibility = View.VISIBLE
//            view.reviewDialogTitle.setText(stage.subtitleRes)
//        } else {
//            view.reviewDialogTitle.visibility = View.GONE
//        }
    }
}