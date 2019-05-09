package com.square.android.presentation.view.gallery

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.Profile
import com.square.android.presentation.view.ProgressView

interface GalleryView: ProgressView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showData(user: Profile.User)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openPhotoPicker()
}