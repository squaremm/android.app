package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
class Profile(var message: String = "",
              var user: User = User()) {

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class User(@field:JsonProperty("_id") var id: Long = 0,
               var newUser: Boolean = false,
               var accepted: Boolean = false,
               @field:JsonProperty("isAcceptationPending")
               var isAcceptationPending: Boolean = false,
               var admin: Boolean = false,
               var city: String = "",
               var currentAgency: String = "",
               var email: String = "",
               var gender: String = "",
               var credits: Long = 0,
               var birthDate: String = "",
               var instagram: Instagram = Instagram(),
               var level: Int = 0,
               var referralCode: String = "",
               var motherAgency: String = "",
               var name: String = "",
               var nationality: String = "",
               var phone: String = "",
               var photo: String? = "",
               var mainImage: String? = "",
               var images: List<Photo>? = null,
               var surname: String = "",
               var isPaymentRequired: Boolean = true) : Parcelable {

        @Parcelize
        class Instagram(var counts: Counts = Counts(),
                        @field:JsonProperty("full_name") var fullName: String = "",
                        var id: String = "",
                        var username: String = "") : Parcelable {

            @Parcelize
            class Counts(@field:JsonProperty("followed_by")
                         var followedBy: Int = 0,
                         var follows: Int = 0,
                         var media: Int = 0) : Parcelable
        }
    }
}