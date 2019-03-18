package com.square.android.presentation.view.fillProfileFirst

import com.mukesh.countrypicker.Country
import com.square.android.presentation.view.BaseView

interface FillProfileFirstView : BaseView {
    fun showBirthday(birthday: String)
    fun displayNationality(country: Country)
}
