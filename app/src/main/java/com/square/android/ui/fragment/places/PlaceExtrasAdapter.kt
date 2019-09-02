package com.square.android.ui.fragment.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.square.android.R
import com.square.android.extensions.loadImageForIcon

class PlaceExtrasAdapter (var data: List<String>) : RecyclerView.Adapter<PlaceExtrasAdapter.ViewHolder>(){

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var icon: ImageView = v.findViewById(R.id.placeExtraImage) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceExtrasAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_place_extra, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.icon.loadImageForIcon(data[position])
    }

    override fun getItemCount(): Int {
        return if(data.isEmpty()) 0 else data.size
    }
}