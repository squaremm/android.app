package com.square.android.ui.activity.campaignDetails

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.square.android.R
import com.square.android.data.pojo.Reward
import com.square.android.extensions.loadImage
import com.square.android.extensions.setTextColorRes

class RewardsAdapter (var rewards: List<Reward>, private val handler: Handler?, var coloredText: Boolean = false) : RecyclerView.Adapter<RewardsAdapter.ViewHolder>(){

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView = v.findViewById(R.id.rewardImage) as ImageView
        var name: TextView = v.findViewById(R.id.rewardName)
        var container: ViewGroup = v.findViewById(R.id.rewardContainer) as ViewGroup
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_reward, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.image.loadImage(rewards[position].imageUrl)
        holder.name.text = rewards[position].name
        if (coloredText) holder.name.setTextColorRes(R.color.nice_pink)

        holder.container.setOnClickListener {handler?.itemClicked(holder.adapterPosition)}
    }

    override fun getItemCount(): Int {
        return if(rewards.isEmpty()) 0 else rewards.size
    }

    interface Handler {
        fun itemClicked(index: Int)
    }

}