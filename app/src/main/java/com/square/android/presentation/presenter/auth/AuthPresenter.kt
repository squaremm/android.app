package com.square.android.presentation.presenter.auth

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.network.errorMessage
import com.square.android.data.network.response.AuthResponse
import com.square.android.data.pojo.AuthData

import com.square.android.presentation.presenter.BasePresenter

import com.square.android.presentation.view.auth.AuthView

enum class AuthAction {
    REGISTER, LOGIN, RESET_PASSWORD, NONE
}

@InjectViewState
class AuthPresenter : BasePresenter<AuthView>() {

    var currentAuthAction = AuthAction.NONE

    fun registerAction() {
        currentAuthAction = AuthAction.REGISTER
    }

    fun loginAction() {
        currentAuthAction = AuthAction.LOGIN
    }

    fun resetPasswordAction() {
        currentAuthAction = AuthAction.RESET_PASSWORD
    }

    fun actionClicked(authData: AuthData) {
        when (currentAuthAction) {
            AuthAction.REGISTER -> registerClicked(authData)
            AuthAction.LOGIN -> loginClicked(authData)
            AuthAction.RESET_PASSWORD -> forgotPasswordClicked(authData.email)
            AuthAction.NONE -> return
        }
    }

    fun forgotPasswordClicked(email: String) {
        viewState.showProgress()
        launch({
            val response = repository.resetPassword(email).await()

            viewState.hideProgress()

            viewState.showLoginFields()
        }, { error ->
            viewState.hideProgress()

            viewState.showMessage(error.errorMessage)
        })
    }

    fun loginClicked(authData: AuthData) {
        viewState.showProgress()
        launch({
            val response = repository.loginUser(authData).await()
            authDone(response)
        }, { error ->
            viewState.hideProgress()

            viewState.showMessage(error.errorMessage)
        })
    }

    fun registerClicked(authData: AuthData) {
        viewState.showProgress()

        launch({
            val response = repository.registerUser(authData).await()
            authDone(response)
        }, { error ->
            viewState.hideProgress()

            viewState.showMessage(error.errorMessage)
        })
    }

    fun authDone(response: AuthResponse) {
        launch ({
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

}
