package com.square.android.ui.fragment.map

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecorator(private val betweenMargin: Int, private val vertical: Boolean = false, private val marginStart: Int = 0, private val marginEnd: Int = 0) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {

        if (!vertical) {
            with(outRect) {
                parent.adapter?.let {

                    left = if (parent.getChildAdapterPosition(view) == 0) {
                        marginStart
                    } else {
                        betweenMargin
                    }

                    right = if (parent.getChildAdapterPosition(view) == it.itemCount - 1) {
                        marginEnd
                    } else {
                        betweenMargin
                    }
                }

                top = 0
                bottom = 0
            }
        } else {
            with(outRect) {
                parent.adapter?.let {

                    top = if (parent.getChildAdapterPosition(view) == 0) {
                        marginStart
                    } else {
                        betweenMargin
                    }

                    bottom = if (parent.getChildAdapterPosition(view) == it.itemCount - 1) {
                        marginEnd
                    } else {
                        betweenMargin
                    }
                }

                left = 0
                right = 0
            }

        }
    }
}