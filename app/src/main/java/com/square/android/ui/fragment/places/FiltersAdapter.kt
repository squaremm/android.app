package com.square.android.ui.fragment.places

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.Filter
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_filter.*

class FiltersAdapter(data: List<Filter>, private val handler: Handler, private val handlerClear: HandlerClear) : BaseAdapter<Filter, FiltersAdapter.FiltersHolder>(data){

    override fun instantiateHolder(view: View): FiltersHolder = FiltersHolder(view, handler, handlerClear)
    override fun getLayoutId(viewType: Int) = R.layout.item_filter
    override fun getItemCount() = data.size

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: FiltersHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is FilterPayload }
                .forEach { holder.bindSelected(data[position]) }

        payloads.filter { it is DeactivatePayload }
                .forEach { holder.bindDeactivated(data[position]) }
    }

    fun changeActivated(position: Int?) {
        if (position == null) return
        notifyItemChanged(position, FilterPayload)
    }

    fun deactivate(position: Int){
        notifyItemChanged(position, DeactivatePayload)
    }

    class FiltersHolder(containerView: View,
                        handler: Handler, var handlerClear: HandlerClear ) : BaseHolder<Filter>(containerView) {
        init {
            containerView.setOnClickListener {handler.itemClicked(adapterPosition)}
        }

        override fun bind(item: Filter, vararg extras: Any?) {
            itemFilterContainer.isActivated = item.activated
            itemFilterText.text = item.text
        }

        fun bindSelected(item: Filter) {
            itemFilterText.text = item.text
            item.activated = !item.activated
            itemFilterContainer.isActivated = item.activated

            handlerClear.bindDone()
        }

        fun bindDeactivated(item: Filter) {
            itemFilterText.text = item.text
            item.activated = false
            itemFilterContainer.isActivated = item.activated
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    interface HandlerClear {
        fun bindDone()
    }
}

object FilterPayload

object DeactivatePayload










