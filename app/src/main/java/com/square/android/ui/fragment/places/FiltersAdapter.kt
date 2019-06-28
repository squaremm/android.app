package com.square.android.ui.fragment.places

import android.view.View
import androidx.core.content.ContextCompat
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_filter.*

class FiltersAdapter(data: List<String>,
                  private val handler: Handler?) : BaseAdapter<String, FiltersAdapter.FilterHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_filter

    override fun getItemCount() = data.size

    override fun bindHolder(holder: FilterHolder, position: Int) {
        holder.bind(data[position])
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: FilterHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is FiltersAdapter.SelectedPayload }
                .forEach { holder.bindSelected(true) }

        payloads.filter { it is FiltersAdapter.UnselectedPayload }
                .forEach { holder.bindSelected(false) }
    }

    fun setSelectedItem(position: Int?, contains: Boolean) {

        if (position == null) return

        if(contains){
            notifyItemChanged(position, UnselectedPayload)
        } else{
            notifyItemChanged(position, SelectedPayload)
        }
    }

    override fun instantiateHolder(view: View): FilterHolder = FilterHolder(view, handler)

    class FilterHolder(containerView: View,
                    handler: Handler?) : BaseHolder<String>(containerView) {

        init {
            containerView.setOnClickListener { handler?.filterClicked(adapterPosition) }
        }

        override fun bind(item: String, vararg extras: Any?) {
            itemFilterText.text = item

            //TODO icon for every place type
//            when(item){
//
//            }

            //TODO delete when icons made for every place type
            itemFilterIcon.setImageDrawable(ContextCompat.getDrawable(itemFilterIcon.context, R.drawable.ic_marker_pink))
        }

        fun bindSelected(activate: Boolean) {
            itemFilterContainer.isActivated = activate
        }
    }

    interface Handler {
        fun filterClicked(position: Int)
    }

    object SelectedPayload
    object UnselectedPayload
}
