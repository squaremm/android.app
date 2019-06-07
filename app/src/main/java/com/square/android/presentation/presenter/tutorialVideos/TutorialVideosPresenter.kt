package com.square.android.presentation.presenter.tutorialVideos

import com.arellomobile.mvp.InjectViewState
import com.square.android.App.Companion.getString
import com.square.android.R
import com.square.android.data.pojo.TutorialVideo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.tutorialVideos.TutorialVideosView

@InjectViewState
class TutorialVideosPresenter: BasePresenter<TutorialVideosView>(){

    val data: MutableList<TutorialVideo> = mutableListOf()

    init {
        initData()
    }

    private fun initData(){
        var tutorialVideo = TutorialVideo()
        tutorialVideo.title = getString(R.string.how_to_credits)
        tutorialVideo.videoUrl = getString(R.string.youtube_how_to_credits)
        tutorialVideo.thumbnailUrl = getString(R.string.thumbnail_how_to_credits)
        data.add(tutorialVideo)

        tutorialVideo = TutorialVideo()
        tutorialVideo.title = getString(R.string.how_to_review)
        tutorialVideo.videoUrl = getString(R.string.youtube_how_to_review)
        tutorialVideo.thumbnailUrl = getString(R.string.thumbnail_how_to_review)
        data.add(tutorialVideo)

        tutorialVideo = TutorialVideo()
        tutorialVideo.title = getString(R.string.how_to_instagram_story)
        tutorialVideo.videoUrl = getString(R.string.youtube_how_to_instagram_story)
        tutorialVideo.thumbnailUrl = getString(R.string.thumbnail_how_to_instagram_story)
        data.add(tutorialVideo)


        viewState.showData(data.toList())
    }

}






