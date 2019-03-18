package com.square.android.presentation.presenter.fillProfileReferral

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.network.errorMessage
import com.square.android.data.pojo.ProfileInfo

import com.square.android.presentation.presenter.BasePresenter

import com.square.android.presentation.view.fillProfileReferral.FillProfileReferralView


@InjectViewState
class FillProfileReferralPresenter(private val info: ProfileInfo)
    : BasePresenter<FillProfileReferralView>() {

    fun confirmClicked(referral: String) {
        info.referral = referral

        finishRegistration()
    }

    fun skipClicked() {
        info.referral = ""

        finishRegistration()
    }

    private fun finishRegistration() {
        launch({
            viewState.showProgress()

            val response = repository.fillProfile(info).await()

            viewState.hideProgress()

            repository.setProfileFilled(true)

            router.showSystemMessage(response.message)
            router.replaceScreen(SCREENS.MAIN)
        }, { error ->
            viewState.hideProgress()
            viewState.showMessage(error.errorMessage)
        })
    }
}
