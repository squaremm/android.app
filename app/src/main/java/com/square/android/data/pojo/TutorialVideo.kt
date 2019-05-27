package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TutorialVideo(
        var videoUrl: String = "",

        var thumbnailUrl: String = "",

        var title: String = ""
) : Parcelable
