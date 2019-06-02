package com.square.android.presentation.presenter.fillProfileReferral

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.network.errorMessage
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.fillProfileReferral.FillProfileReferralView

@InjectViewState
class FillProfileReferralPresenter(
        val info: ProfileInfo
) : BasePresenter<FillProfileReferralView>() {

    var keptImages: List<ByteArray>? = null

    init {
        viewState.showData(info)
    }

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

            val userId = repository.getUserId()
            info.images?.forEach {
                repository.addPhoto(userId, it).await()
            }

            viewState.hideProgress()

            repository.setProfileFilled(true)
            repository.saveProfileInfo("",0)

            router.showSystemMessage(response.message)
            viewState.sendFcmToken()
//            viewState.showPendingUser()
            router.replaceScreen(SCREENS.MAIN)
        }, { error ->
            viewState.hideProgress()
            viewState.showMessage(error.errorMessage)
        })
    }
}
