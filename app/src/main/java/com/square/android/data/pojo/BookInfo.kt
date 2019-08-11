package com.square.android.data.pojo

import com.squareup.moshi.Json

class BookInfo(@Json(name="userID")
               val userId: Long?,
               val date: String?,
               val intervalId: String?)