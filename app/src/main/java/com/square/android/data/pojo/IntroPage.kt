package com.square.android.data.pojo

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize

@Parcelize
class IntroPage(@StringRes val titleRes: Int,
                @StringRes val contentRes: Int,
                @DrawableRes val imageRes: Int) : Parcelable