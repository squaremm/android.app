package com.square.android.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.LinearLayout
import android.view.ViewGroup
import android.view.View

class CheckableLinearLayout: LinearLayout,  Checkable {

    var mChecked: Boolean = false

    private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int ) : super(context, attributeSet, defStyleAttr)

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        setChecked(!mChecked);
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            refreshDrawableState()
            setCheckedRecursive(this, checked);
        }
    }

    private fun setCheckedRecursive(parent: ViewGroup, checked: Boolean) {
        val count = parent.childCount
        for (i in 0 until count) {
            val v = parent.getChildAt(i)
            if (v is Checkable) {
                (v as Checkable).isChecked = checked
            }

            if (v is ViewGroup) {
                setCheckedRecursive(v, checked)
            }
        }
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()

        val drawable = background
        if (drawable != null) {
            val myDrawableState = drawableState
            drawable.state = myDrawableState
            invalidate()
        }
    }
}