package com.square.android.data.pojo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CampaignBookInfo(
               val date: String?,
               val slotId: String?)