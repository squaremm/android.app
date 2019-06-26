package com.square.android.data.pojo

class UserInfo(val photo: String?,
               val name: String,
               val id: Long = 0,
               val socialLink: String?,
               val isPaymentRequired: Boolean = true)