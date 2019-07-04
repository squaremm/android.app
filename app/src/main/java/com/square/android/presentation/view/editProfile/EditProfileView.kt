package com.square.android.presentation.view.editProfile

import com.mukesh.countrypicker.Country
import com.square.android.data.pojo.Profile
import com.square.android.presentation.view.ProgressView

interface EditProfileView : ProgressView {
    fun showBirthday(date: String)
    fun showData(user: Profile.User, arePushNotificationsAllowed: Boolean, isGeolocationAllowed: Boolean)
    fun displayNationality(country: Country?)
}