package com.square.android.ui.fragment.entries

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.square.android.R
import com.square.android.extensions.loadImage
import org.jetbrains.anko.dimen

class ImagesAdapter (var imageUrls: List<String>, private val handler: Handler?) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>(){

    fun setUrls(urls: List<String>){
        imageUrls = urls
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView = v.findViewById(R.id.itemPhotoImv) as ImageView
        var container: ViewGroup = v.findViewById(R.id.itemPhotoContainer) as ViewGroup
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_square, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(imageUrls[position], roundedCornersRadiusPx = holder.image.context!!.dimen(R.dimen.value_4dp))
        holder.container.setOnClickListener {handler?.itemClicked(imageUrls[position])}
    }

    override fun getItemCount(): Int {
        return if(imageUrls.isEmpty()) 0 else imageUrls.size
    }

    interface Handler {
        fun itemClicked(url: String)
    }

}