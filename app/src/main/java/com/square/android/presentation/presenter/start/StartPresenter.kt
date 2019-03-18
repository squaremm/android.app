package com.square.android.presentation.presenter.start

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS

import com.square.android.presentation.presenter.BasePresenter

import com.square.android.presentation.view.start.StartView


@InjectViewState
class StartPresenter : BasePresenter<StartView>() {
    init {
        val screen = when {
            repository.shouldDisplayIntro() -> SCREENS.INTRO
            !repository.isLoggedIn() -> SCREENS.AUTH
            !repository.isProfileFilled() -> SCREENS.FILL_PROFILE_FIRST
            else -> SCREENS.MAIN
        }

        router.replaceScreen(screen)
    }
}
