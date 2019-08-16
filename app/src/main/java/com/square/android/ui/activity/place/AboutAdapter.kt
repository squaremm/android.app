package com.square.android.ui.activity.place

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.square.android.R

class AboutAdapter (var iconTypes: List<String>, private val handler: Handler? = null) : RecyclerView.Adapter<AboutAdapter.ViewHolder>(){

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var icon: ImageView = v.findViewById(R.id.itemAboutIcon) as ImageView
        var container: ViewGroup = v.findViewById(R.id.itemAboutContainer) as ViewGroup
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_about, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val drawable : Drawable? = when(iconTypes[position]){
            "phone" -> ContextCompat.getDrawable(holder.icon.context, R.drawable.r_dial)
            "email" -> ContextCompat.getDrawable(holder.icon.context, R.drawable.r_mail)
            "www" -> ContextCompat.getDrawable(holder.icon.context, R.drawable.r_www)
            "insta" -> ContextCompat.getDrawable(holder.icon.context, R.drawable.r_insta)
            else -> null
        }

        drawable?.let {
            holder.icon.setImageDrawable(it)
        }

//        holder.container.setOnClickListener{handler?.itemClicked(}
    }

    override fun getItemCount(): Int {
        return if(iconTypes.isEmpty()) 0 else iconTypes.size
    }

    interface Handler {
        fun itemClicked(index: Int)
    }

}