package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProfileInfo(var name: String = "",
                  var surname: String = "",
                  var birthDate: String = "",
                  var gender: String = "",
                  var nationality: String = "",
                  var email: String = "",
                  var phone: String = "",
                  var motherAgency: String = "",
                  var currentAgency: String = "",
                  var referral: String = "") : Parcelable