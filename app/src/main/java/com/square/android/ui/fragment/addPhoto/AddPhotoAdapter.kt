package com.square.android.ui.fragment.addPhoto

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.square.android.R
import com.square.android.extensions.loadImage
import org.jetbrains.anko.dimen

class AddPhotoAdapter (var images: List<Uri?>, private val handler: Handler?) : RecyclerView.Adapter<AddPhotoAdapter.ViewHolder>(){

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView = v.findViewById(R.id.itemPhotoImv) as ImageView
        var container: ViewGroup = v.findViewById(R.id.itemPhotoContainer) as ViewGroup
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPhotoAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_square, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(images[position] == null){
            holder.image.loadImage(R.drawable.add_photo, roundedCornersRadiusPx = holder.image.context!!.dimen(R.dimen.value_4dp))
        } else{
            holder.image.loadImage(images[position]!!, roundedCornersRadiusPx = holder.image.context!!.dimen(R.dimen.value_4dp))
        }

        holder.container.setOnClickListener {handler?.itemClicked(position,images[position] == null )}
    }

    override fun getItemCount(): Int {
        return if(images.isEmpty()) 0 else images.size
    }

    interface Handler {
        fun itemClicked(index: Int,isEmpty: Boolean)
    }

}