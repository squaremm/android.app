package com.square.android.ui.fragment.booking

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.Day
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_day.*

class DaysAdapter(data: List<Day>,
                   private val handler: Handler?) : BaseAdapter<Day, DaysAdapter.DayHolder>(data) {

    private var selectedItemPosition: Int? = null
    var selectedMonth: Int = 0

    override fun getLayoutId(viewType: Int) = R.layout.item_day

    override fun getItemCount() = data.size

    override fun bindHolder(holder: DayHolder, position: Int) {
        holder.bind(data[position], listOf(selectedItemPosition, selectedMonth))
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: DayHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is SelectedPayload }
                .forEach { holder.bindSelected(data[position], selectedItemPosition, selectedMonth) }
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
                      handler: Handler?) : BaseHolder<Day>(containerView) {

        init {
            containerView.setOnClickListener { handler?.itemClicked(adapterPosition) }
        }

        override fun bind(item: Day, vararg extras: Any? ) {
            val selectedPosition = extras[0] as Int?
            val selectedMonth = extras[1] as Int?

            bindSelected(item, selectedPosition, selectedMonth)

            itemDayName.text = item.dayName
            itemDayValue.text = item.dayValue.toString()
            itemDayValue.checkMarkDrawable = null
        }

        fun bindSelected(item: Day,selectedPosition: Int?, selectedMonth: Int?) {
            itemDayValue.isChecked = (selectedPosition == adapterPosition)
            itemDayValue.isEnabled = true

//            itemDayValue.isEnabled = (item.monthNumber == selectedMonth)
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}