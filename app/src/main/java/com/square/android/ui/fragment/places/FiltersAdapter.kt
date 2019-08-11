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

    private var activatedItems: MutableList<Int> = mutableListOf()

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
                .forEach { holder.bindSelected() }
    }

    fun setSelectedItem(position: Int?, contains: Boolean) {

        if (position == null) return

        if(contains){
            activatedItems.remove(position)
        } else{
            if(!activatedItems.contains(position)){
                activatedItems.add(position)
            }
        }

        notifyItemChanged(position, SelectedPayload)
    }

    override fun instantiateHolder(view: View): FilterHolder = FilterHolder(view, handler, activatedItems)

    class FilterHolder(containerView: View,
                    handler: Handler?, val activatedItems: MutableList<Int> ) : BaseHolder<String>(containerView) {

        init {
            containerView.setOnClickListener { handler?.filterClicked(adapterPosition) }
        }

        override fun bind(item: String, vararg extras: Any?) {
            itemFilterText.text = item

            bindSelected()

            //TODO icon for every place type
//            when(item){
//
//            }

            //TODO delete when icons made for every place type
            itemFilterIcon.setImageDrawable(ContextCompat.getDrawable(itemFilterIcon.context, R.drawable.ic_marker_pink))
        }

        fun bindSelected() {
            itemFilterContainer.isActivated = activatedItems.contains(adapterPosition)
        }
    }

    interface Handler {
        fun filterClicked(place: Int)
    }

    object SelectedPayload
}
