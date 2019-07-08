package com.square.android.ui.fragment.sendPictureChoose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.square.android.R
import com.square.android.data.pojo.SendPictureType
import com.square.android.extensions.loadImageInside

class SendPictureAdapter (var items: List<SendPictureType>, private val handler: Handler?) : RecyclerView.Adapter<SendPictureAdapter.ViewHolder>(){

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView = v.findViewById(R.id.itemSendLogo) as ImageView
        var tv: TextView = v.findViewById(R.id.itemSendTitle) as TextView
        var container: ViewGroup = v.findViewById(R.id.itemSendContainer) as ViewGroup
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SendPictureAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_send_picture, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImageInside(items[position].imageRes)
        holder.tv.setText(items[position].titleRes)

        holder.container.setOnClickListener{handler?.itemClicked(holder.adapterPosition)}
    }

    override fun getItemCount(): Int {
        return if(items.isEmpty()) 0 else items.size
    }

    interface Handler {
        fun itemClicked(index: Int)
    }

}