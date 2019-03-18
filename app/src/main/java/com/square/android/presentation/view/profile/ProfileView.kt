package com.square.android.presentation.view.profile

import com.square.android.data.pojo.Profile
import com.square.android.presentation.view.BaseView

interface ProfileView : BaseView {
    fun showUser(user: Profile.User)
}
