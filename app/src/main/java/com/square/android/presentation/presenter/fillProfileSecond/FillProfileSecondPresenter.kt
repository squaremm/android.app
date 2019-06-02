package com.square.android.presentation.presenter.fillProfileSecond

import com.arellomobile.mvp.InjectViewState
import com.mukesh.countrypicker.Country
import com.square.android.SCREENS
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.fillProfileSecond.FillProfileSecondView

@InjectViewState
class FillProfileSecondPresenter(val info: ProfileInfo) : BasePresenter<FillProfileSecondView>() {

    init {
        viewState.showData(info)
    }

    fun nextClicked(account: String, phone: String, motherAgency: String, currentAgency: String, phoneN: String, phoneC: String) {
        info.instagramName = account
        info.phone = phone
        info.motherAgency = motherAgency
        info.currentAgency = currentAgency
        info.phoneN = phoneN
        info.phoneC = phoneC

        router.navigateTo(SCREENS.FILL_PROFILE_THIRD, info)
    }

    fun countrySelected(country: Country) {
        info.flagCode = country.flag

        viewState.showDialInfo(country)
    }
}
