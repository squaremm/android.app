package com.square.android.ui.fragment.places

import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_day_short.*

class DaysShortAdapter(data: List<String>,
                  private val handler: Handler?) : BaseAdapter<String, DaysShortAdapter.DayHolder>(data) {

    var selectedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.item_day_short

    override fun getItemCount() = data.size

    override fun bindHolder(holder: DayHolder, position: Int) {
        holder.bind(data[position], selectedItemPosition)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: DayHolder, position: Int, payloads: MutableList<Any>) {
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

    override fun instantiateHolder(view: View): DayHolder = DayHolder(view, handler)

    class DayHolder(containerView: View,
                    handler: Handler?) : BaseHolder<String>(containerView) {

        init {
            containerView.setOnClickListener { handler?.dayItemClicked(adapterPosition) }
        }

        override fun bind(item: String, vararg extras: Any? ) {

            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            bindSelected(item, selectedPosition)

            itemDayShortValue.text = item
        }

        fun bindSelected(item: String, selectedPosition: Int?) {
            itemDayShortValue.isChecked = (selectedPosition == adapterPosition)
            itemDayShortValue.isEnabled = true
        }
    }

    interface Handler {
        fun dayItemClicked(position: Int)
    }

    object SelectedPayload
}