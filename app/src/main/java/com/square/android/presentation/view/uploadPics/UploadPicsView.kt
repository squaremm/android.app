package com.square.android.presentation.view.uploadPics

import com.square.android.data.pojo.Participation
import com.square.android.presentation.view.ProgressView

interface UploadPicsView : ProgressView {

    fun replaceToApproval()

    fun reloadData(participation: Participation)
}