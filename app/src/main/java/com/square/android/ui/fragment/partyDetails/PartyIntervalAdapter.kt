package com.square.android.ui.fragment.partyDetails

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_interval_party.*

class PartyIntervalAdapter(data: List<Place.Interval>,
                      private val handler: Handler?) : BaseAdapter<Place.Interval, PartyIntervalAdapter.ViewHolder>(data) {

    var selectedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.item_interval_party

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
                     var handler: Handler?) : BaseHolder<Place.Interval>(containerView) {

        override fun bind(item: Place.Interval, vararg extras: Any? ) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            bindSelected(item, selectedPosition)

            var enabled = item.slots > 0

            bookingContainer.isEnabled = enabled
            bookingInterval.isEnabled = enabled
            bookingSpots.isEnabled = enabled
            bookingSeparator1.isEnabled = enabled
            bookingSeparator2.isEnabled = enabled
            bookingDescription.isEnabled = enabled

            var text = bookingInterval.context.getString(R.string.time_range, item.start, item.end)
            bookingInterval.text = text

            bookingDescription.text = item.description

            bookingContainer.setOnClickListener {
                if(enabled){
                    handler?.itemClicked(adapterPosition, enabled)
                }
            }

            bookingSpots.text = item.slots.toString()
        }

        fun bindSelected(item: Place.Interval,selectedPosition: Int?) {
            bookingContainer.isChecked = (selectedPosition == adapterPosition)
            bookingInterval.isChecked = (selectedPosition == adapterPosition)
            bookingSpots.isChecked = (selectedPosition == adapterPosition)
            bookingSeparator1.isChecked = (selectedPosition == adapterPosition)
            bookingSeparator2.isChecked = (selectedPosition == adapterPosition)
            bookingDescription.isChecked = (selectedPosition == adapterPosition)
        }
    }

    interface Handler {
        fun itemClicked(position: Int, enabled: Boolean)
    }

    object SelectedPayload
}
