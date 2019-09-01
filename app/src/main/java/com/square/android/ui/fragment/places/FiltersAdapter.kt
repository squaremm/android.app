package com.square.android.ui.fragment.places

import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_filter.*

class FiltersAdapter(data: List<String>,
                  private val handler: Handler?, private var activatedItems: MutableList<String>) : BaseAdapter<String, FiltersAdapter.FilterHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_filter

    override fun getItemCount() = data.size

    override fun bindHolder(holder: FilterHolder, position: Int) {
        holder.bind(data[position], activatedItems)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: FilterHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is FiltersAdapter.SelectedPayload }
                .forEach { holder.bindSelected(data[position], activatedItems) }
    }

    fun updateData(activated: MutableList<String>){
        activatedItems = activated

        notifyItemRangeChanged(0, data.size, SelectedPayload)
    }

    override fun instantiateHolder(view: View): FilterHolder = FilterHolder(view, handler)

    class FilterHolder(containerView: View,
                    handler: Handler?) : BaseHolder<String>(containerView) {

        init {
            containerView.setOnClickListener { handler?.filterClicked(adapterPosition) }
        }

        override fun bind(item: String, vararg extras: Any?) {
            val activatedItems = if(extras[0] == null) null else extras[0] as MutableList<String>

            itemFilterName.text = item

            bindSelected(item, activatedItems)
        }

        fun bindSelected(item: String, activatedItems: MutableList<String>?) {
            activatedItems?.let {
                itemFilterContainer.isChecked = it.contains(item)
                itemFilterName.isChecked = it.contains(item)
            }
        }
    }

    interface Handler {
        fun filterClicked(place: Int)
    }

    object SelectedPayload
}
