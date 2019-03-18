package com.square.android.presentation.presenter.editProfile

import com.arellomobile.mvp.InjectViewState
import com.mukesh.countrypicker.Country
import com.square.android.SCREENS
import com.square.android.data.pojo.ProfileInfo

import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.profile.ProfileUpdatedEvent

import com.square.android.presentation.view.editProfile.EditProfileView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class EditProfilePresenter : BasePresenter<EditProfileView>() {
    private val eventBus: EventBus by inject()

    init {
        loadData()
    }

    private fun loadData() {
        launch {
            val user = repository.getCurrentUser().await()

            viewState.showData(user)
        }
    }

    fun save(profileInfo: ProfileInfo) {
        launch {
            viewState.showProgress()

            val result = repository.fillProfile(profileInfo).await()

            repository.setUserName(profileInfo.name, profileInfo.surname)

            val event = ProfileUpdatedEvent()
            eventBus.post(event)

            router.showSystemMessage(result.message)
            
            router.exit()
        }
    }

    fun birthSelected(date: String) {
        viewState.showBirthday(date)
    }

    fun logout() {
        repository.clearUserData()

        router.replaceScreen(SCREENS.START)
    }

    fun countrySelected(country: Country) {
        viewState.displayNationality(country)
    }
}
