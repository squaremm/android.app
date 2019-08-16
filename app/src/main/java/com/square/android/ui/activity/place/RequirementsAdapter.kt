package com.square.android.ui.activity.place

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.square.android.R
import com.square.android.data.pojo.Requirement

class RequirementsAdapter (var requirements: List<Requirement>, private val handler: Handler? = null) : RecyclerView.Adapter<RequirementsAdapter.ViewHolder>(){

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var icon: ImageView = v.findViewById(R.id.itemRequirementIcon) as ImageView
        var container: ViewGroup = v.findViewById(R.id.itemRequirementContainer) as ViewGroup
        var nameTv: TextView = v.findViewById(R.id.itemRequirementName) as TextView
        var valueTv: TextView = v.findViewById(R.id.itemRequirementValue) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequirementsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_requirement, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTv.text = requirements[position].name
        holder.valueTv.text = requirements[position].value

        val drawable : Drawable? = when(requirements[position].name){
            holder.icon.context.getString(R.string.dress_code) -> ContextCompat.getDrawable(holder.icon.context, R.drawable.r_hanger)
            holder.icon.context.getString(R.string.minimum_tip) -> ContextCompat.getDrawable(holder.icon.context, R.drawable.r_discount)
            else -> null
        }

        drawable?.let {
            holder.icon.setImageDrawable(it)
        }

//        holder.container.setOnClickListener{handler?.itemClicked(}
    }

    override fun getItemCount(): Int {
        return if(requirements.isEmpty()) 0 else requirements.size
    }

    interface Handler {
        fun itemClicked(index: Int)
    }

}
