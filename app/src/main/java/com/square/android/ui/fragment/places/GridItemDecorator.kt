package com.square.android.ui.fragment.places

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

/* IMPORTANT:
 This item decoration can be added several times when calling RecyclerView.addItemDecoration(GridItemDecoration... which may cause layout problems


 WHEN CALLING RecyclerView.addItemDecoration MULTIPLE TIMES: remember to check if it isn't already defined

 var decorationAdded = false
 if(!decorationAdded){
    decorationAdded = true
    RecyclerView.addItemDecoration(GridItemDecoration...
 }

 */

class GridItemDecoration(private val columnCount: Int, @Px val preferredSpace: Int, private val includeEdge: Boolean, private val topSpaceMultiplier: Float = 1f): RecyclerView.ItemDecoration() {

    private val space = if (preferredSpace % 3 == 0) preferredSpace else (preferredSpace + (3 - preferredSpace % 3))

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)

        if(columnCount == 2){

            when {
                position == 0 || position % columnCount == 0 -> outRect.right = preferredSpace / 2

                else -> {
                    outRect.left = preferredSpace / 2
                }
            }

            if (position >= columnCount) {
                outRect.top = (preferredSpace * topSpaceMultiplier).toInt()
            }


// This is probably targeting only columnCount == 3
        } else{
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

}