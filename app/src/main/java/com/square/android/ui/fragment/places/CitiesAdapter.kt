package com.square.android.ui.fragment.places

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.City
import com.square.android.extensions.loadImage
import com.square.android.extensions.loadImageForIcon
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_city.*

class CitiesAdapter(data: List<City>, private val handler: Handler?) : BaseAdapter<City, CitiesAdapter.ViewHolder>(data) {

    var selectedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.item_city

    override fun getItemCount() = data.size

    override fun bindHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], selectedItemPosition)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is SelectedPayload }
                .forEach { holder.bindSelected(data[position], selectedItemPosition) }
    }

    fun setSelectedItem(position: Int?) {
        if (position == null) return

        val previousPosition = selectedItemPosition
        selectedItemPosition = position

        previousPosition?.let { notifyItemChanged(it, SelectedPayload) }

        notifyItemChanged(position)
    }

    override fun instantiateHolder(view: View): ViewHolder = ViewHolder(view, handler)

    class ViewHolder(containerView: View,
                     var handler: Handler?) : BaseHolder<City>(containerView) {

        override fun bind(item: City, vararg extras: Any? ) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            cityContainer.setOnClickListener { handler?.itemClicked(adapterPosition) }

            cityName.text = item.name

            cityImage.loadImage(url = item.image, placeholder = android.R.color.white)

            bindSelected(item, selectedPosition)
        }

        fun bindSelected(item: City,selectedPosition: Int?) {
            cityContainer.isChecked = (selectedPosition == adapterPosition)
            cityName.isChecked = (selectedPosition == adapterPosition)
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}