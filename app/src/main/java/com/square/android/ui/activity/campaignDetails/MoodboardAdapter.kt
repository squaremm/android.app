package com.square.android.ui.activity.campaignDetails

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.square.android.data.pojo.ImageAspect
import com.square.android.extensions.loadImage
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.ViewTreeObserver
import com.square.android.R

const val TYPE_FULL = 0
const val TYPE_HALF_FULL = 1
const val TYPE_25_WIDTH_FULL = 2
const val TYPE_QUARTER = 3
const val TYPE_75_WIDTH_FULL = 4

const val ASPECT_TYPE_SQUARE = 1
const val ASPECT_TYPE_HORIZONTAL = 2
const val ASPECT_TYPE_VERTICAL = 3


class MoodboardAdapter (var images: List<ImageAspect>, private val handler: Handler?) : RecyclerView.Adapter<MoodboardAdapter.ViewHolder>(){

    override fun getItemViewType(position: Int): Int = images[position].arrangeType

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
//        var image: ImageView = v.findViewById(R.id.) as ImageView
//        var container: ViewGroup = v.findViewById(R.id.) as ViewGroup
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodboardAdapter.ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_aspect_image, parent, false)
        itemView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val lp = itemView.layoutParams
                if (lp is StaggeredGridLayoutManager.LayoutParams) {
                    val sglp = lp as StaggeredGridLayoutManager.LayoutParams
                    when (viewType) {

                        TYPE_FULL -> sglp.isFullSpan = true

                        TYPE_HALF_FULL -> {
                            sglp.isFullSpan = false
                            sglp.width = itemView.width / 2
                        }

                        TYPE_25_WIDTH_FULL -> {
                            sglp.isFullSpan = false
                            sglp.width = itemView.width / 4
                        }

                        TYPE_75_WIDTH_FULL -> {
                            sglp.isFullSpan = false
                            sglp.width = (itemView.width * 0.75f).toInt()
                        }

                        TYPE_QUARTER -> {
                            sglp.isFullSpan = false
                            sglp.width = itemView.width / 2
                            sglp.height = itemView.height / 2
                        }

                    }
                    itemView.layoutParams = sglp
                    val lm = (parent as RecyclerView).layoutManager as StaggeredGridLayoutManager?
                    lm!!.invalidateSpanAssignments()
                }
                itemView.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        holder.image.loadImage(images[position].imageUrl, roundedCornersRadiusPx = holder.image.context!!.dimen(R.dimen.value_4dp) )
//        holder.container.setOnClickListener {handler?.itemClicked(holder.adapterPosition)}
    }

    override fun getItemCount(): Int {
        return if(images.isEmpty()) 0 else images.size
    }

    interface Handler {
        fun itemClicked(index: Int)
    }

}