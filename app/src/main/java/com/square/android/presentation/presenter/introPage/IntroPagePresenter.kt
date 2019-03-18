package com.square.android.presentation.presenter.introPage

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS

import com.square.android.presentation.presenter.BasePresenter

import com.square.android.presentation.view.introPage.IntroPageView


@InjectViewState
class IntroPagePresenter : BasePresenter<IntroPageView>() {
    fun nextClicked() {
        repository.introDisplayed()

        router.replaceScreen(SCREENS.AUTH)
    }
}
