package com.square.android.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.square.android.R

class ScrollViewMaxHeight : ScrollView {

    private var maxHeight = WITHOUT_MAX_HEIGHT_VALUE

    constructor(context: Context) : super(context) {

        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle, 0)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val a = context.theme.obtainStyledAttributes(
                attrs, R.styleable.ScrollViewMaxHeight, defStyleAttr, defStyleRes)
        maxHeight = a.getDimensionPixelSize(R.styleable.ScrollViewMaxHeight_max_height, maxHeight)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        try {
            var heightSize = MeasureSpec.getSize(heightMeasureSpec)
            if (maxHeight != WITHOUT_MAX_HEIGHT_VALUE && heightSize > maxHeight) {
                heightSize = maxHeight
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST)
            layoutParams.height = heightSize
        } catch (e: Exception) {
        } finally {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun setMaxHeight(maxHeight: Int) {
        this.maxHeight = maxHeight
    }

    companion object {
        var WITHOUT_MAX_HEIGHT_VALUE = -1
    }
}
