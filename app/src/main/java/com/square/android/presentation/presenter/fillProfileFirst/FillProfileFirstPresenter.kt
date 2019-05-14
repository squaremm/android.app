package com.square.android.presentation.presenter.fillProfileFirst

import com.arellomobile.mvp.InjectViewState
import com.mukesh.countrypicker.Country
import com.square.android.SCREENS
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.fillProfileFirst.FillProfileFirstView

@InjectViewState
class FillProfileFirstPresenter : BasePresenter<FillProfileFirstView>() {
    private val model = ProfileInfo()

    fun birthSelected(birthday: String) {
        model.birthDate = birthday

        viewState.showBirthday(birthday)
    }

    fun nextClicked(name: String, surname: String) {
        model.name = name
        model.surname = surname

        router.navigateTo(SCREENS.FILL_PROFILE_SECOND, model)
    }

    fun countrySelected(country: Country) {
        model.nationality = country.name

        viewState.displayNationality(country)
    }

    fun genderSelected(gender: String) {
        model.gender = gender
        viewState.displayGender(gender)
    }
}
