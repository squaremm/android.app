package com.square.android.data.pojo

//TODO may not work, from api docs: Star, Link, feedback
class ReviewInfo(var link: String? = "",
                 var postType: String? = null,
                 var feedback: String = "",
                 var stars: Int = 0)