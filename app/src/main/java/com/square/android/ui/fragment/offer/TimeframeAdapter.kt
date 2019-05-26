package com.square.android.ui.fragment.offer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.square.android.R

class TimeframeAdapter (var timeframes: List<String>) : RecyclerView.Adapter<TimeframeAdapter.ViewHolder>(){

    fun setTframes(tframes: List<String>){
        timeframes = tframes
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var timeframeText: TextView = v.findViewById(R.id.timeframeText) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeframeAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_timeframe, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.timeframeText.text = timeframes[position]
    }

    override fun getItemCount(): Int {
        return if(timeframes.isEmpty()) 0 else timeframes.size
    }

}