package com.square.android.presentation.presenter.editProfile

import com.arellomobile.mvp.InjectViewState
import com.mukesh.countrypicker.Country
import com.square.android.SCREENS
import com.square.android.SCREENS.GALLERY
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.ProfileInfo

import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.profile.ProfileUpdatedEvent

import com.square.android.presentation.view.editProfile.EditProfileView
import com.square.android.utils.AnalyticsManager
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class EditProfilePresenter : BasePresenter<EditProfileView>() {
    private val eventBus: EventBus by inject()

    private var user: Profile.User? = null

    private var arePushNotificationsAllowed = false

    private var isGeolocationAllowed = false

    init {
        loadData()
    }

    fun loadData() {
        launch {

            viewState.showProgress()

            user = repository.getCurrentUser().await()

            arePushNotificationsAllowed = repository.getPushNotificationsAllowed()
            isGeolocationAllowed = repository.getGeolocationAllowed()

            viewState.showData(user!!, arePushNotificationsAllowed, isGeolocationAllowed)

            viewState.hideProgress()
        }
    }

    fun setPushNotificationsAllowed(allowed: Boolean){
        arePushNotificationsAllowed = allowed
    }

    fun setGeolocationAllowed(allowed: Boolean){
        isGeolocationAllowed = allowed
    }

    fun save(profileInfo: ProfileInfo) {
        launch {
            viewState.showProgress()

            val result = repository.fillProfile(profileInfo).await()

            repository.setUserName(profileInfo.name, profileInfo.surname)

            repository.setPushNotificationsAllowed(arePushNotificationsAllowed)
            repository.setGeolocationAllowed(isGeolocationAllowed)

            val event = ProfileUpdatedEvent()
            eventBus.post(event)

            val userId = repository.getUserId()
            val isUserPremium = repository.isUserPremium()
            val isPaymentRequired = repository.getUserInfo().isPaymentRequired
            println("User Analytics - Profile edited -> userId: "+userId+", isUserPremium: "+isUserPremium+", isPaymentRequired: "+isPaymentRequired)
            AnalyticsManager.logUser(userId, isUserPremium, isPaymentRequired)

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

    fun openGallery() {
        router.navigateTo(GALLERY, user)
    }
}
