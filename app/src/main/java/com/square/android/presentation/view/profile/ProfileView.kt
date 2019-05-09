package com.square.android.presentation.view.profile

import com.mukesh.countrypicker.Country
import com.square.android.data.pojo.Profile
import com.square.android.presentation.view.BaseView
import com.square.android.presentation.view.ProgressView

interface ProfileView : BaseView, ProgressView {
    fun showUser(user: Profile.User)
    fun displayNationality(country: Country?)

}
