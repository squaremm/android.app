package com.square.android.utils.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.square.android.R

class RecyclerViewMaxHeight: RecyclerView{

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
                attrs, R.styleable.RecyclerViewMaxHeight, defStyleAttr, defStyleRes)
        maxHeight = a.getDimensionPixelSize(R.styleable.RecyclerViewMaxHeight_recycler_max_height, maxHeight)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var heightMeasureSpec = heightSpec
        try {
            var heightSize = MeasureSpec.getSize(heightMeasureSpec)
            if (maxHeight != WITHOUT_MAX_HEIGHT_VALUE && heightSize > maxHeight) {
                heightSize = maxHeight
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST)
            layoutParams.height = heightSize
        } catch (e: Exception) {
        } finally {
            super.onMeasure(widthSpec, heightMeasureSpec)
        }
    }

    fun setMaxHeight(maxHeight: Int) {
        this.maxHeight = maxHeight
    }

    companion object {
        var WITHOUT_MAX_HEIGHT_VALUE = -1
    }
}