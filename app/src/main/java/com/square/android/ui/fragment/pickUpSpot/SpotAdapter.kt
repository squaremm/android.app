package com.square.android.ui.fragment.pickUpSpot

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.CampaignInterval
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_spot.*

class SpotAdapter(data: List<CampaignInterval.Location>,
                  private val handler: Handler?) : BaseAdapter<CampaignInterval.Location, SpotAdapter.SpotHolder>(data) {

    var selectedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.item_spot

    override fun getItemCount() = data.size

    override fun bindHolder(holder: SpotHolder, position: Int) {
        holder.bind(data[position], selectedItemPosition)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: SpotHolder, position: Int, payloads: MutableList<Any>) {
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

    override fun instantiateHolder(view: View): SpotHolder = SpotHolder(view, handler)

    class SpotHolder(containerView: View,
                    handler: Handler?) : BaseHolder<CampaignInterval.Location>(containerView) {

        init {
            containerView.setOnClickListener { handler?.itemClicked(adapterPosition) }
        }

        override fun bind(item: CampaignInterval.Location, vararg extras: Any? ) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            bindSelected(item, selectedPosition)

            itemSpotAddress.text = item.getAddressString()
        }

        fun bindSelected(item: CampaignInterval.Location,selectedPosition: Int?) {
            itemSpotContainer.isSelected = selectedPosition == adapterPosition
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}