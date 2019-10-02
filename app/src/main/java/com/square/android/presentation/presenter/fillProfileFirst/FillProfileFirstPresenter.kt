package com.square.android.presentation.presenter.fillProfileFirst

import com.arellomobile.mvp.InjectViewState
import com.mukesh.countrypicker.Country
import com.square.android.SCREENS
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.fillProfileFirst.FillProfileFirstView

@InjectViewState
class FillProfileFirstPresenter(val info: ProfileInfo) : BasePresenter<FillProfileFirstView>() {

    init {
        viewState.showData(info)
    }

    fun birthSelected(modelBirthday: String, displayBirthday: String) {
        info.birthDate = modelBirthday
        info.displayBirthday = displayBirthday

        viewState.showBirthday(displayBirthday)
    }

    fun nextClicked(name: String, surname: String, phone: String, phoneN: String, phoneC: String, account: String) {
        info.name = name
        info.surname = surname
        info.phone = phone
        info.phoneN = phoneN
        info.phoneC = phoneC
        info.instagramName = account

        router.navigateTo(SCREENS.FILL_PROFILE_SECOND, info)
    }

    fun countrySelected(country: Country) {
        info.nationality = country.name

        viewState.displayNationality(country)
    }

    fun countryDialSelected(country: Country) {
        info.flagCode = country.flag

        viewState.showDialInfo(country)
    }

    fun genderSelected(gender: String) {
        info.gender = gender
        viewState.displayGender(gender)
    }
}
