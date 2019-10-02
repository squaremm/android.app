package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileInfo(var name: String = "",
                       var surname: String = "",
                       var birthDate: String = "",
                       var gender: String = "",
                       var nationality: String = "",
                       var email: String = "",
                       var instagramName: String = "",
                       var phone: String = "",
                       var motherAgency: String = "",
                       var agency1: String = "",
                       var city1: String = "",
                       var agency2: String = "",
                       var city2: String = "",
                       var agency3: String = "",
                       var city3: String = "",
                       var referral: String = "",

//                  @JsonIgnore
//                  var imagesUri: List<Uri>? = null,
                       @JsonIgnore
                       var images: List<ByteArray>? = null,
                       @JsonIgnore
                       var displayBirthday: String = "",
                       @JsonIgnore
                       var phoneN: String = "",
                       @JsonIgnore
                       var phoneC: String = "",
                       @JsonIgnore
                       var flagCode: Int = -1
) : Parcelable