package com.square.android.ui.fragment.places

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(private val columnCount: Int, @Px preferredSpace: Int, private val includeEdge: Boolean, private val topSpaceMultiplier: Float = 1f): RecyclerView.ItemDecoration() {

    private val space = if (preferredSpace % 3 == 0) preferredSpace else (preferredSpace + (3 - preferredSpace % 3))

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)

        if (includeEdge) {

            when {
                position % columnCount == 0 -> {
                    outRect.left = space
                    outRect.right = space / 3
                }
                position % columnCount == columnCount - 1 -> {
                    outRect.right = space
                    outRect.left = space / 3
                }
                else -> {
                    outRect.left = space * 2 / 3
                    outRect.right = space * 2 / 3
                }
            }

            if (position < columnCount) {
                outRect.top = (space * topSpaceMultiplier).toInt()
            }

            outRect.bottom = space

        } else {

            when {
                position % columnCount == 0 -> outRect.right = space * 2 / 3
                position % columnCount == columnCount - 1 -> outRect.left = space * 2 / 3
                else -> {
                    outRect.left = space / 3
                    outRect.right = space / 3
                }
            }

            if (position >= columnCount) {
                outRect.top = (space * topSpaceMultiplier).toInt()
            }
        }
    }

}