package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


//TODO: update when API done

@Parcelize
data class Job(var id: Long = 0,
               var name: String = "",
               var mainImage: String? = null,
               var type: Int = 0
): Parcelable
