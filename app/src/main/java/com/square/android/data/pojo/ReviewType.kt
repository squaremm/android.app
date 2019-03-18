package com.square.android.data.pojo

import androidx.annotation.DrawableRes
import androidx.annotation.StringDef
import androidx.annotation.StringRes

@Retention(AnnotationRetention.SOURCE)
@StringDef(TYPE_FACEBOOK_POST, TYPE_INSTAGRAM_POST, TYPE_INSTAGRAM_STORY, TYPE_TRIP_ADVISOR, TYPE_GOOGLE_PLACES)
annotation class ReviewTypeKey

class ReviewType(@DrawableRes val imageRes: Int,
                 @StringRes val titleRes: Int,
                 @StringRes val descriptionRes: Int,
                 @ReviewTypeKey val key: String,
                 var enabled: Boolean = true,
                 var shown: Boolean = true,
                 val stages: List<Stage> = listOf()) {

    class Stage(@StringRes val subtitleRes: Int?,
                var content: String = "",
                var ratingNeeded: Boolean = false,
                var doneEnabled: Boolean =false,
                @StringRes val buttonText: Int)
}