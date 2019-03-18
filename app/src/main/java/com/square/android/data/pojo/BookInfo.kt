package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonProperty

class BookInfo(@field:JsonProperty("userID") val userId: Long?,
               val date: String?,
               val interval: Int?)