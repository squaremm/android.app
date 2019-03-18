package com.square.android.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet


class CircleImageView : AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setImageBitmap(bm: Bitmap) {
        post { setBitmap(bm, width, height) }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (drawable is BitmapDrawable) {

            val bmp = drawable.bitmap

            post { setBitmap(bmp, width, height) }
        } else {
            super.setImageDrawable(drawable)
        }
    }

    private fun setBitmap(bm: Bitmap, width: Int, height: Int) {
        val scaled = Bitmap.createScaledBitmap(bm, width, height, false)

        val result = prepareAvatar(scaled)
        super.setImageDrawable(result)
    }

    private fun prepareAvatar(original: Bitmap): RoundedBitmapDrawable {
        val result = RoundedBitmapDrawableFactory.create(resources, original)
        result.isCircular = true
        return result
    }
}
