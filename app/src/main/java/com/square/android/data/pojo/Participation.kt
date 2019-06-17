package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//TODO: update when API done

@Parcelize
data class Participation(var id: Long = 0,
                         var name: String = "",
                         var mainImage: String? = null,
                         var active: Boolean = true,
                         var photos: List<Photo>? = null,
                         var daysLeft: Int = 0,
                         var status: Int = 0,
                         var toUpload: Int = 0,
                         var toUploadIg: Int = 0
) : Parcelable