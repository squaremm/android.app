package com.square.android.ui.fragment.fillProfileSecond

import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_select_city.*

class SelectCityAdapter(data: List<String>,
                       private val handler: Handler?) : BaseAdapter<String, SelectCityAdapter.ViewHolder>(data) {

    var selectedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.item_select_city

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
                         handler: Handler?) : BaseHolder<String>(containerView) {

        init {
            containerView.setOnClickListener { handler?.itemClicked(adapterPosition) }
        }

        override fun bind(item: String, vararg extras: Any?) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            bindSelected(item, selectedPosition)


            itemSelectCityText.text = item
        }

        fun bindSelected(item: String, selectedPosition: Int?) {
            itemSelectCityText.isChecked = (selectedPosition == adapterPosition)
        }

    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}