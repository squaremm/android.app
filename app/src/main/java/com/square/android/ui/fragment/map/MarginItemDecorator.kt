package com.square.android.ui.fragment.map

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecorator(private val marginWidth: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            parent.adapter?.let {

                left = if(parent.getChildAdapterPosition(view) == 0){
                    0
                } else {
                    marginWidth
                }

                right = if(parent.getChildAdapterPosition(view) == it.itemCount  - 1){
                    0
                } else {
                    marginWidth
                }
            }

            top = 0
            bottom = 0
        }
    }
}