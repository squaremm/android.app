package com.square.android.ui.activity.eventDetails

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.*
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_event_detail.*

class EventDetailsAdapter(data: List<EventDetail>,
                  private val handler: Handler?) : BaseAdapter<EventDetail, EventDetailsAdapter.ViewHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_event_detail

    override fun getItemCount() = data.size

    override fun bindHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        onBindViewHolder(holder, position)
    }

    override fun instantiateHolder(view: View): ViewHolder = ViewHolder(view, handler)

    class ViewHolder(containerView: View,
                    handler: Handler?) : BaseHolder<EventDetail>(containerView) {

        init {
            containerView.setOnClickListener { handler?.itemClicked(adapterPosition) }
        }

        override fun bind(item: EventDetail, vararg extras: Any? ) {
            itemEventDetailsIcon.visibility = View.GONE
            itemEventDetailsTransfer.visibility = View.GONE
            itemEventDetailsTransferLl.visibility = View.GONE
            itemEventDetailsName.visibility = View.GONE
            itemEventDetailsSecondary.visibility = View.GONE
            itemEventDetailsContainer.visibility = View.GONE
            itemEventDetailsNoDinnerLabel.visibility = View.GONE
            itemEventDetailsContainerDetails.visibility = View.GONE

            if(item.available){
                itemEventDetailsIcon.visibility = View.VISIBLE
            }

            if(item.highlighted){
                // itemEventDetailsLine.setImageDrawable() to highlighted
                // itemEventDetailsIcon.imageTintList to highlighted
            } else{
                // itemEventDetailsLine.setImageDrawable() to normal
                // itemEventDetailsIcon.imageTintList to normal
            }

            when(item.type){
                TYPE_ONE_WAY, TYPE_PARTY, TYPE_RETURN_TO, TYPE_DINNER  -> {
                    // itemEventDetailsIcon.setImageDrawable() to circle

                    itemEventDetailsContainer.visibility = View.VISIBLE
                    itemEventDetailsName.visibility = View.VISIBLE
                    itemEventDetailsSecondary.visibility = View.VISIBLE

                    itemEventDetailsStatus.text = item.status
                }

                TYPE_TRANSFER -> {
                    // itemEventDetailsIcon.setImageDrawable() to square

                    itemEventDetailsTransfer.visibility = View.VISIBLE
                    itemEventDetailsTransferLl.visibility = View.VISIBLE

                    if(item.available){
                        itemEventDetailsTransfer.isEnabled = true
                        itemEventDetailsNameFrom.isEnabled = true
                        itemEventDetailsNameTo.isEnabled = true

                        itemEventDetailsArrow.visibility = View.VISIBLE
                        itemEventDetailsNameTo.visibility = View.VISIBLE

                        itemEventDetailsNameFrom.text = item.placeFrom
                        itemEventDetailsNameTo.text = item.placeTo
                    } else{
                        itemEventDetailsTransfer.isEnabled = false
                        itemEventDetailsNameFrom.isEnabled = false
                        itemEventDetailsNameTo.isEnabled = false

                        itemEventDetailsArrow.visibility = View.GONE
                        itemEventDetailsNameTo.visibility = View.GONE

                        itemEventDetailsNameFrom.text = "-"
                    }
                }

            }

        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}