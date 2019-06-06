package com.square.android.ui.fragment.entries


import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.square.android.R
import com.square.android.extensions.loadImage
import org.jetbrains.anko.dimen

class EntriesAdapter (var imageUrls: List<String>) : RecyclerView.Adapter<EntriesAdapter.ViewHolder>(){

    fun setUrls(urls: List<String>){
        imageUrls = urls
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView = v.findViewById(R.id.itemEntryImg) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntriesAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_entry, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(imageUrls!![position], roundedCornersRadiusPx = holder.image.context!!.dimen(R.dimen.value_4dp))
    }

    override fun getItemCount(): Int {
        return if(imageUrls.isEmpty()) 0 else imageUrls.size
    }

}