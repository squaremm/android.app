package com.square.android.presentation.presenter.auth

import com.arellomobile.mvp.InjectViewState
import com.square.android.BuildConfig
import com.square.android.Network
import com.square.android.SCREENS
import com.square.android.data.network.errorMessage

import com.square.android.presentation.presenter.BasePresenter

import com.square.android.presentation.view.auth.AuthView
import com.square.android.utils.buildInstagramUrl


@InjectViewState
class AuthPresenter : BasePresenter<AuthView>() {
    fun authCLicked() {
        val url = buildInstagramUrl(
                BuildConfig.INSTAGRAM_CLIENT_ID,
                Network.INSTAGRAM_CALLBACK_URL
        )

        viewState.showProgress()
        viewState.showAuthDialog(url, Network.INSTAGRAM_TRIGGER)
    }

    fun authDone(code: String) {
        launch ({
            viewState.showProgress()

            val response = repository.registerUser(code).await()

            repository.setUserToken(response.token!!)

            val profile = repository.getCurrentUser().await()

            repository.setUserId(profile.id)
            repository.setLoggedIn(true)
            repository.setAvatarUrl(profile.photo)
            repository.setUserName(profile.name, profile.surname)
            repository.setSocialLink(profile.instagram.username)

            viewState.hideProgress()

            if (profile.newUser) {
                router.replaceScreen(SCREENS.FILL_PROFILE_FIRST)
            } else {
                repository.setProfileFilled(true)
                router.replaceScreen(SCREENS.MAIN)
            }

            router.showSystemMessage(response.message)
        }, { error ->
            viewState.hideProgress()

            viewState.showMessage(error.errorMessage)
        })
    }

    fun dialogCanceled() {
        viewState.hideProgress()
    }
}
