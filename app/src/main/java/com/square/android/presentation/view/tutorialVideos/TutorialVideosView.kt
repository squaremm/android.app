package com.square.android.presentation.view.tutorialVideos

import com.square.android.data.pojo.TutorialVideo
import com.square.android.presentation.view.BaseView

interface TutorialVideosView : BaseView {

    fun showData(data: List<TutorialVideo>)
}
