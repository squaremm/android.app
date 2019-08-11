package com.square.android.data.pojo

import androidx.annotation.DrawableRes
import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(TYPE_FACEBOOK_POST, TYPE_INSTAGRAM_POST, TYPE_INSTAGRAM_STORY, TYPE_TRIP_ADVISOR, TYPE_GOOGLE_PLACES, TYPE_YELP, TYPE_PICTURE)
annotation class ReviewTypeKey

class ReviewType(@DrawableRes val imageRes: Int,
                 val title: String,
                 val description: String? = null,
                 @ReviewTypeKey val key: String,
                 var enabled: Boolean = true,
                 var shown: Boolean = true,
                 var app_name: String? = null,
                 var content: String? = null,
                 var showUploadLabel: Boolean = false)