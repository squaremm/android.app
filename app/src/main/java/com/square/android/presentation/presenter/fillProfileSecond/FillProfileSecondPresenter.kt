package com.square.android.presentation.presenter.fillProfileSecond

import com.arellomobile.mvp.InjectViewState
import com.mukesh.countrypicker.Country
import com.square.android.SCREENS
import com.square.android.data.pojo.ProfileInfo

import com.square.android.presentation.presenter.BasePresenter

import com.square.android.presentation.view.fillProfileSecond.FillProfileSecondView


@InjectViewState
class FillProfileSecondPresenter(private val info: ProfileInfo) : BasePresenter<FillProfileSecondView>() {

    fun nextClicked(email: String, phone: String, motherAgency: String, currentAgency: String) {
        info.email = email
        info.phone = phone
        info.motherAgency = motherAgency
        info.currentAgency = currentAgency

        router.navigateTo(SCREENS.FILL_PROFILE_THIRD, info)
    }

    fun countrySelected(country: Country) {
        viewState.showDialInfo(country)
    }
}
