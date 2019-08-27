package com.square.android.utils.widget

import android.content.Context
import android.view.MotionEvent
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class CustomViewPager : ViewPager {

    var isPagingEnabled = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return this.isPagingEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event)
    }
}