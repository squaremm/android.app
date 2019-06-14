package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


//TODO: update when API done

@Parcelize
data class Campaign(var id: Long = 0,
                    var name: String = "",
                    var description: String? = null,
                    var mainImage: String? = null,
                    var winnerImage: String? = null,
                    var type: Int = 0,
                    var available: Boolean = true,
                    var participantsImages: List<String>? = null,
                    var credits: Int = 0,
                    var rewards: List<Reward>? = null,
                    var winnerRewards: List<Reward>? = null,
                    var modelTypeImages: List<String>? = null,
                    var storiesRequired: Int = 0,
                    var postsRequired: Int = 0,
                    var participateDays: Int = 0,
                    var uploadPicsDays: Int = 0,
                    var uploadIgDays: Int = 0,
                    var moodboardImages: List<String>? = null

): Parcelable
