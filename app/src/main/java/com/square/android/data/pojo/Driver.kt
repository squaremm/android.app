package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class Driver(
        var id: String = "",
        var car: Car? = null,
        var name: String = "",
        var picture: String,
        var spots: Int = 0,

        var rating: Double = 0.0
) : Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    class Car(
            var model: String = "",
            var licensePlate: String = ""
    ) : Parcelable

}