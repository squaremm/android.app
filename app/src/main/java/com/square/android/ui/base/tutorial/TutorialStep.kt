package com.square.android.ui.base.tutorial

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef

class TutorialStep : Parcelable {

    @IntDef(ArrowPos.TOP, ArrowPos.BOTTOM)
    @kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
    annotation class ArrowPos {
        companion object {
            const val TOP = 0
            const val BOTTOM = 1
        }
    }

    var stepText: String = ""

    var waitValue: Float = 0f

    @DrawableRes var arrowDrawable: Int = 0

    var infoWindowPercentagePos: FloatArray? = null

    var arrowHorizontalPercentagePos: Float = 0.5f

    // x1, x2, y1, y2
    var transparentViewPixelPos: FloatArray? = null

    @ArrowPos var arrowPos: Int = 0

    constructor(infoWindowPercentagePos: FloatArray,
                stepText: String,
                @ArrowPos arrowPos: Int,
                @DrawableRes arrowDrawable: Int,
                arrowHorizontalPercentagePos: Float,
                transparentViewPixelPos: FloatArray,
                waitValue: Float = 0f) {

        this.infoWindowPercentagePos = infoWindowPercentagePos
        this.stepText = stepText
        this.arrowPos = arrowPos
        this.arrowDrawable = arrowDrawable
        this.arrowHorizontalPercentagePos = arrowHorizontalPercentagePos
        this.transparentViewPixelPos = transparentViewPixelPos
        this.waitValue = waitValue
    }

    constructor(parcel: Parcel) {
        infoWindowPercentagePos = parcel.createFloatArray()
        stepText = parcel.readString() ?: ""
        arrowPos = parcel.readInt()
        arrowDrawable = parcel.readInt()
        arrowHorizontalPercentagePos = parcel.readFloat()
        transparentViewPixelPos = parcel.createFloatArray()
        waitValue = parcel.readFloat()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeFloatArray(infoWindowPercentagePos)
        dest.writeString(stepText)
        dest.writeInt(arrowPos)
        dest.writeInt(arrowDrawable)
        dest.writeFloat(arrowHorizontalPercentagePos)
        dest.writeFloatArray(transparentViewPixelPos)
        dest.writeFloat(waitValue)
    }

    companion object CREATOR : Parcelable.Creator<TutorialStep> {
        override fun createFromParcel(parcel: Parcel): TutorialStep {
            return TutorialStep(parcel)
        }

        override fun newArray(size: Int): Array<TutorialStep?> {
            return arrayOfNulls(size)
        }
    }
}