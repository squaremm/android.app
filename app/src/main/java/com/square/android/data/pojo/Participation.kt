package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//TODO: update when API done

@Parcelize
data class Participation(var id: Long = 0,
                         var name: String = "",
                         var mainImage: String? = null,
                         var active: Boolean = true

) : Parcelable